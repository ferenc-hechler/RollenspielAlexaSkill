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

public class Continue extends Step {

	private String continueText;
	private String continueRef;
	
	public Continue(String continueText, String continueRef) {
		this.continueText = continueText;
		this.continueRef = continueRef;
	}

	public String getContinueText() {
		return continueText;
	}
	public String getContinueRef() {
		return continueRef;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("WEITER:");
		if (getContinueRef() != null) {
			if (getContinueText() != null) {
				result.append(" ").append(getContinueText());
			}
			result.append(" [").append(getContinueRef()).append("]").append(TextUtil.endl);
		}
		else {
			result.append(TextUtil.endl);
			if (getContinueText() != null) {
				result.append(getContinueText()).append(TextUtil.endl);
			}
			result.append(TextUtil.endl);
		}
		return result.toString();
	}

	@Override
	public void executeStep(Response response) {
		if (getContinueText() != null) {
			response.addDescription(getContinueText());
		}
		if (getContinueRef() != null) {
			response.continueChapter(response, getContinueRef());
		}
		else {
			response.continueNextStep(response);
		}
	}

	@Override
	public void processAnswer(Response result, String answer) {
		throw new RuntimeException("NO ANSWER EXPECTED at "+toString());
	}
	

	@Override
	public boolean alwaysRedirects() {
		return getContinueRef() != null;
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		chapter.validateOptionalRef("WEITER", soloData, getContinueRef());
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (getContinueRef() != null) {
			collectedRefs.add(getContinueRef());
		}
	}
	
}
