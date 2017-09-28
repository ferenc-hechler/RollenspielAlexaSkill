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

public class Challenge extends Decision{

	private String macheProbeText;
	private String sageErgebnisText;
	
	public Challenge(String question, String macheProbeText, String sageErgebnisText, String falscheAuswahlText) {
		super(question, null, falscheAuswahlText);
		this.macheProbeText = macheProbeText;
		this.sageErgebnisText = sageErgebnisText;
	}
	
	@Override
	public String getQuestion() {
		if (macheProbeText != null) {
			return macheProbeText + "\""+TextUtil.handleMinus(super.getQuestion())+"\";";
		}
		return "Mache eine Probe auf  \""+TextUtil.handleMinus(super.getQuestion())+"\";";
	}

	@Override
	protected String getDecisionMainSentence() {
		if (sageErgebnisText != null) {
			return TextUtil.endl + sageErgebnisText + createDecisionMainList();
		}
		return TextUtil.endl + "Sage mir dann das Ergebnis: " + createDecisionMainList();
	}


	
	public void validate() {
		if ((choiceName2Index.size() == 2) && choiceName2Index.containsKey("BESTANDEN") && choiceName2Index.containsKey("FEHLGESCHLAGEN")) {
			return;
		}
		throw new RuntimeException("Challenge must only have a 'success' and a 'fail' choice: "+choiceName2Index.keySet().toString());
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("PROBE:").append(TextUtil.endl);
		if (macheProbeText != null) {
			result.append("MACHEPROBETEXT: ").append(macheProbeText).append(TextUtil.endl);
		}
		if (sageErgebnisText != null) {
			result.append("SAGEERGEBNISTEXT: ").append(sageErgebnisText).append(TextUtil.endl);
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
