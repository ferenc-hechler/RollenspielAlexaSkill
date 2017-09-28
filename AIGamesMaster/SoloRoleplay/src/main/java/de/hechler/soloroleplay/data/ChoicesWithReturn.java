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

import java.util.List;

import de.hechler.soloroleplay.util.TextUtil;

public class ChoicesWithReturn extends Decision {

	private String sageWeiterText;
	
	public ChoicesWithReturn(String question, String sageWeiterText, String auswahlText, String falscheAuswahlText) {
		super(question, auswahlText, falscheAuswahlText);
		this.sageWeiterText = sageWeiterText;
	}
	
	@Override
	public String getQuestion() {
		String result = super.getQuestion();
		if (sageWeiterText != null) {
			result += sageWeiterText;
		}
		else {
			result += " Sage 'weiter', wenn Du weitermachen möchtest. ";
		}
		return result;
	}
	
	@Override
	public List<String> getPossibleMainAnswers() {
		List<String> result = super.getPossibleMainAnswers();
		result.add("WEITER");
		return result;
	}
	
	@Override
	public void executeStep(Response response) {
		response.setReturnChapterAndStep();
		super.executeStep(response);
	}
	
	@Override
	public void processAnswer(Response response, String answer) {
		if ("WEITER".equals(answer)) {
			response.continueNextStep(response);
		}
		else {
			super.processAnswer(response, answer);
		}
	}
	
	
	@Override
	public boolean alwaysRedirects() {
		return false;
	}
	
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("AUSWAHLMITR"+TextUtil.UML_UE+"CKSPRUNG:").append(TextUtil.endl);
		if (sageWeiterText != null) {
			result.append("SAGEWEITERTEXT: ").append(sageWeiterText).append(TextUtil.endl);
		}
		if (auswahlText != null) {
			result.append("AUSWAHLTEXT: ").append(auswahlText).append(TextUtil.endl);
		}
		if (falscheAuswahlText != null) {
			result.append("FALSCHEAUSWAHLTEXT: ").append(falscheAuswahlText).append(TextUtil.endl);
		}
		result.append(question).append(TextUtil.endl);
		for (Choice choice:choices) {
			result.append(choice.toString());
		}
		return result.toString();
	}


}
