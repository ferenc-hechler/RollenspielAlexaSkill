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

import de.hechler.soloroleplay.util.ListUtil;
import de.hechler.soloroleplay.util.TextUtil;

public class Action extends Step {

	private String actionText;
	private String sageOkText;
	private String falscheAuswahlText;
	
	public Action(String actionText, String sageOkText, String falscheAuswahlText) {
		this.actionText = actionText;
		this.sageOkText = sageOkText;
		this.falscheAuswahlText = falscheAuswahlText;
	}

	public String getActionText() {
		if (sageOkText == null) {
			return "Sage OK, wenn du folgendes erledigt hast: "+actionText;
		}
		return sageOkText + " " + actionText;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("AKTION:").append(TextUtil.endl);
		if (sageOkText != null) {
			result.append("SAGEOKTEXT: ").append(sageOkText).append(TextUtil.endl);
		}
		if (falscheAuswahlText != null) {
			result.append("FALSCHEAUSWAHLTEXT: ").append(falscheAuswahlText).append(TextUtil.endl);
		}
		result.append(actionText).append(TextUtil.endl).append(TextUtil.endl);
		return result.toString();
	}

	@Override
	public void executeStep(Response response) {
		response.addDescription(getActionText());
		response.setConfirmationType();
	}

	@Override
	public void processAnswer(Response response, String answer) {
		if (answer.equals("OK") || answer.equals("OKAY")  || answer.equals("O K") || answer.equals("JA")) {
			response.stepFinished();
			return;
		}
		if (falscheAuswahlText != null) {
			response.unknownAnswer(falscheAuswahlText);
		}
		else {
			response.unknownChoice(ListUtil.toList("OK"), answer);
		}
	}

	@Override
	public boolean alwaysRedirects() {
		return false;
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		chapter.validateNotEmpty("Aktion ohne Text", actionText);
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
		return;
	}

	
}
