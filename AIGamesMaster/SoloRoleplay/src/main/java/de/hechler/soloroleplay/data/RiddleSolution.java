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

public class RiddleSolution {

	private String solutionType;
	private String solutionWord;
	
	private String text;
	private String chapterRef;

	public RiddleSolution(String solutionType, String solutionWord, String text, String chapterRef) {
		this.solutionType = solutionType;
		this.solutionWord = solutionWord;
		this.text = text;
		this.chapterRef = chapterRef;
	}

	public String getSolutionType() {
		return solutionType;
	}
	public String getSolutionWord() {
		return solutionWord;
	}
	public String getText() {
		return text;
	}
	public String getChapterRef() {
		return chapterRef;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getSolutionType());
		if (getSolutionWord() != null) {
			result.append("(").append(getSolutionWord()).append(")");
		}
		result.append(":");
		if (getText() != null) {
			result.append(" ").append(getText());
		}
		if (getChapterRef() != null) {
			result.append(" [").append(getChapterRef()).append("]");
		}
		result.append(TextUtil.endl);
		return result.toString();
	}

	public boolean isAnswer() {
		return "ANTWORT".equals(solutionType);
	}
	
	public boolean isFulfilled(Response response, String answer) {
		switch (getSolutionType()) {
		case "ANTWORT": {
			return TextUtil.normalizeForCompare(solutionWord).equals(TextUtil.normalizeForCompare(answer));
		}
		case "FEHLVERSUCHE": {
			String rcWord = Integer.toString(response.getSubStepCount()+1);
			return rcWord.equals(solutionWord);
		}
		case "AUFGEBEN": {
			return "AUFGEBEN".equals(answer);
		}
		default: {
			throw new RuntimeException("unknown condition '"+getSolutionType()+"'");
		}
		}
	}

	public void processAnswer(Response response) {
		if (getText() != null) {
			response.addDescription(getText());
			if (!isAnswer()) {
				response.incrementSubStepCount();
			}
		}
		if (getChapterRef() != null) {
			response.jumpToChapter(getChapterRef());
		}
		
	}

	public void validate() {
		if (solutionType == null) {
			throw new RuntimeException("'ANTWORT', 'FEHLVERSUCHE' oder 'AUFGEBEN' fehlt.");
		}
		if (solutionType.equals("ANTWORT")) {
			if (solutionWord == null) {
				throw new RuntimeException("'ANTWORT' ohne L"+TextUtil.UML_oe+"sungswort.");
			}
		}
		else if (solutionType.equals("FEHLVERSUCHE")) {
			if (solutionWord == null) {
				throw new RuntimeException("'FEHLVERSUCHE' ohne Anzahl");
			}
			if (!solutionWord.matches("[0-9]+")) {
				throw new RuntimeException("Ungueltige Anzahl '"+solutionWord+"' in 'FEHLVERSUCHE'.");
			}
		}
		else if (solutionType.equals("AUFGEBEN")) {
			if (solutionWord != null) {
				throw new RuntimeException("Ungueltige Anzahl '"+solutionWord+"' in 'FEHLVERSUCHE'.");
			}
		}
		else {
			throw new RuntimeException("Ungueltiges Schluesselwort '"+solutionType+"'. 'ANTWORT' oder 'FEHLVERSUCHE' erwartet.");
		}
	}

	public void validate(SoloRoleplayData soloData, Chapter chapter, Riddle riddle) throws ValidationException {
		String solInfo = solutionType + ( solutionWord==null ? "" : "("+solutionWord+")" );
		chapter.validateOptionalRef(solInfo, soloData, getChapterRef());
	}

	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (getChapterRef() != null) {
			collectedRefs.add(getChapterRef());
		}
	}
	
}
