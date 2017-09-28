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

public class Condition {

	private String condName;
	private String condRefParam;
	private int condCnt;

	private String text;
	private String chapterRef;

	public Condition(String condName, String condRefParam, String condCntParam, String text, String chapterRef) {
		this.condName = condName;
		this.condRefParam = condRefParam;
		setCondCnt(condRefParam, condName, condCntParam);
		this.text = text;
		this.chapterRef = chapterRef;
	}

	private void setCondCnt(String condName, String condRefParam, String condCntParam) {
		if (condCntParam == null) {
			this.condCnt = 1;
		}
		else {
			try {
				condCnt = Integer.valueOf(condCntParam);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Ungueltige Anzahl '"+condCntParam+"' in "+condName+"("+condRefParam+","+condCntParam+")");
			}
		}
	}

	public String getCondName() {
		return condName;
	}
	public String getCondRefParam() {
		return condRefParam;
	}
	public int getCondCnt() {
		return condCnt;
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
		result.append(getCondName());
		if (condRefParam != null) {
			result.append("(").append(getCondRefParam());
			if (condCnt != 1) {
				result.append(",").append(Integer.toString(getCondCnt())).append(")");
			}
			result.append(")");
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

	public boolean isFulfilled(Response response) {
		switch (getCondName()) {
		case "BESUCHT": {
			return response.checkChapterVisited(getCondRefParam(), getCondCnt());
		}
		case "SONST": {
			return true;
		}
		default: {
			throw new RuntimeException("unknown condition '"+getCondName()+"'");
		}
		}
	}

	public boolean alwaysRedirects() {
		boolean result = "SONST".equals(getCondName());
		result = result && getChapterRef() != null;
		return result;
	}

	public void execute(Response response) {
		if (getText() != null) {
			response.addDescription(getText());
		}
		response.continueChapter(response, getChapterRef());
		
	}

	public void validate(SoloRoleplayData soloData, Chapter chapter, Rule rule) throws ValidationException {
		chapter.validateOptionalRef(getCondName(), soloData, getChapterRef());
		chapter.validateOptionalRef(getCondName(), soloData, getCondRefParam());
	}

	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (getChapterRef() != null) {
			collectedRefs.add(getChapterRef());
		}
		// getCondRefParam() is not a target chapter, so do not add to collectedRefs.
	}

	
}
