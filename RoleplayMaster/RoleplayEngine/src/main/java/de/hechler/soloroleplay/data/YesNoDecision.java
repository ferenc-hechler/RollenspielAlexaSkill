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

import de.hechler.soloroleplay.util.TextUtil;

public class YesNoDecision extends Decision {

	public YesNoDecision(String question, String falscheAuswahlText) {
		super(question, null, falscheAuswahlText);
	}

	@Override
	protected String getDecisionMainSentence() {
		return null;
	}

	
	public void validate() {
		if ((choiceName2Index.size() == 2) && choiceName2Index.containsKey("JA") && choiceName2Index.containsKey("NEIN")) {
			return;
		}
		throw new RuntimeException("YesNoDecision must only have a 'yes' and a 'no' choice: "+choiceName2Index.keySet().toString());
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("JANEINENTSCHEIDUNG:").append(TextUtil.endl);
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
