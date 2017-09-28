/**
 * Diese Datei ist Teil des Alexa Skills Rollenspiel Soloabenteuer.
 * Copyright (C) 2016-2017 Ferenc Hechler (github@fh.anderemails.de)
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer ist Freie Software: 
 * Sie koennen es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder spaeteren
 * veroeffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer wird in der Hoffnung, 
 * dass es nuetzlich sein wird, aber
 * OHNE JEDE GEWAEHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewaehrleistung der MARKTFAEHIGKEIT oder EIGNUNG FUER EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License fuer weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.hechler.soloroleplay.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import de.hechler.soloroleplay.util.TextUtil;

public class Decision extends Step {
	
	private final static Logger logger = Logger.getLogger(Decision.class.getName());

	protected String question;
	protected String auswahlText; 
	protected String falscheAuswahlText; 
	protected List<Choice> choices;
	protected Map<String, Integer> choiceName2Index;

	
	public Decision(String question, String auswahlText, String falscheAuswahlText) {
		this.question = question;
		this.auswahlText = auswahlText;
		this.falscheAuswahlText = falscheAuswahlText;
		this.choices = new ArrayList<Choice>();
		this.choiceName2Index = new HashMap<String, Integer>();
	}

	public String getQuestion() {
		return question;
	}
	
	public void addChoice(Choice choice) {
		if (choiceName2Index.containsKey(choice.getName())) {
			throw new RuntimeException("decision contains duplicate choice '"+choice.getName()+"'");
		}
		choiceName2Index.put(choice.getName(), choices.size());
		for (String phrase:choice.getPhrases()) {
			choiceName2Index.put(phrase.trim().toUpperCase(Locale.GERMAN), choices.size());
		}
		choices.add(choice);
	}

	public void validate() {
		if (choiceName2Index.isEmpty()) {
			throw new RuntimeException("No choices for question '"+getQuestion()+"'");
		}
	}

	@Override
	public void executeStep(Response response) {
		if (getQuestion() != null) {
			response.addDescription(getQuestion());
		}
		response.addDescription(getDecisionMainSentence());
		response.setPossibleAnswers(getPossibleAnswers());
	}
	
	protected String getDecisionMainSentence() {
		if (auswahlText != null) {
			return TextUtil.endl + auswahlText;
		}
		return TextUtil.endl + "W"+TextUtil.UML_ae+"hle: " + createDecisionMainList();
	}

	protected StringBuilder createDecisionMainList() {
		StringBuilder result = new StringBuilder();
		List<String> possibleMainAnswers = getPossibleMainAnswers();
		for (int i=0; i<possibleMainAnswers.size(); i++) {
			String possibleMainAnswer = "'"+possibleMainAnswers.get(i)+"'";
			if (i > 0) {
				if (i == possibleMainAnswers.size()-1) {
					possibleMainAnswer = " oder "+possibleMainAnswer;
				}
				else {
					possibleMainAnswer = ", "+possibleMainAnswer;
				}
			}
			result.append(possibleMainAnswer);
		}
		return result;
	}

	@Override
	public void processAnswer(Response response, String answer) {
		Integer choiceNum = choiceName2Index.get(answer);
		if (choiceNum == null) {
			logger.info("ANSWER: '"+answer+"'"); 
			logger.info("CHOICES: "+choiceName2Index.keySet());
			if (falscheAuswahlText != null) {
				response.unknownAnswer(falscheAuswahlText);
			}
			else {
				response.unknownChoice(getPossibleAnswers(), answer);
			}
			return;
		}
		processChoice(response, choiceNum);
	}

	protected void processChoice(Response response, Integer choiceNum) {
		Choice choice = choices.get(choiceNum);
		response.addDescription(choice.getText());
		response.jumpToChapter(choice.getChapterRef());
	}

	public List<String> getPossibleAnswers() {
		List<String> result = new ArrayList<String>();
		for (Choice choice:choices) {
			result.add(choice.getName());
		}
		for (Choice choice:choices) {
			result.addAll(choice.getPhrases());
		}
		return result;
	}

	public List<String> getPossibleMainAnswers() {
		List<String> result = new ArrayList<String>();
		for (Choice choice:choices) {
			if (choice.getPhrases().isEmpty()) {
				result.add(choice.getName());
			}
			else {
				result.add(choice.getPhrases().get(0));
			}
		}
		return result;
	}

	
	@Override
	public boolean alwaysRedirects() {
		return true;
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		chapter.validateNotEmpty("Entscheidung ohne Auswahlmöglichkeiten", choices);
		for (Choice choice:choices) {
			choice.validate(soloData, chapter, this);
		}
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (choices != null) {
			for (Choice choice:choices) {
				choice.collectAllChapterReferences(collectedRefs);
			}
		}
	}

}
