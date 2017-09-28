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
import java.util.Arrays;
import java.util.List;

import de.hechler.soloroleplay.data.Opponent.OpponentType;
import de.hechler.soloroleplay.util.TextUtil;

public class Fight extends Decision {

	private List<Opponent> opponents;
	
	private String kampfIntroText;
	private String notiereDatenText;
	private String datenWiederholenText;
	private String sageErgebnisText;
	
	public Fight(String kampfIntroText, String notiereDatenText, String datenWiederholenText, String sageErgebnisText, String falscheAuswahlText) {
		super(null, null, falscheAuswahlText);
		this.kampfIntroText = kampfIntroText;
		this.notiereDatenText = notiereDatenText;
		this.datenWiederholenText = datenWiederholenText;
		this.sageErgebnisText = sageErgebnisText;
		opponents = new ArrayList<Opponent>();
	}

	public void addOpponent(Opponent opponent) {
		opponents.add(opponent);
	}
	
	@Override
	protected String getDecisionMainSentence() {
		if (sageErgebnisText != null) {
			return TextUtil.endl + sageErgebnisText;
		}
		return TextUtil.endl + "Sage mir dann das Ergebnis des Kampfes: " + createDecisionMainList();
	}
	
	@Override
	public void validate() {
		if (opponents.size() == 0) {
			throw new RuntimeException("there are no opponents in the fight!");
		}
		if ((choiceName2Index.size() == 2) && choiceName2Index.containsKey("GEWONNEN") && choiceName2Index.containsKey("VERLOREN")) {
			return;
		}
		if ((choiceName2Index.size() == 3) && choiceName2Index.containsKey("GEWONNEN") && choiceName2Index.containsKey("VERLOREN") && choiceName2Index.containsKey("ABGEBROCHEN")) {
			return;
		}
		throw new RuntimeException("Kampf darf nur eine 'GEWONNEN', 'VERLOREN' und eine optionale 'ABGEBROCHEN' Auswahl haben: "+choiceName2Index.keySet().toString());
	}

	
	@Override
	public void executeStep(Response response) {
		int subStep = response.getSubStepCount();
		if (subStep == 0) {
			tellFightInfo(response);
			subStep = response.incrementSubStepCount();
			
		}
		if (subStep <= opponents.size()) {
			tellOpponentData(response, subStep);
			if (datenWiederholenText != null) {
				response.addDescription(datenWiederholenText);
			}
			else {
				response.addDescription("Möchtest Du die Daten nochmal gesagt bekommen?");
			}
		}
		else {
			super.executeStep(response);
		}
	}
	
	@Override
	public void processAnswer(Response response, String answer) {
		int subStep = response.getSubStepCount();
		if (subStep <= opponents.size()) {
			processRepeatOpponentYesNo(response, answer);
		}
		else {
			super.processAnswer(response, answer);
		}
	}

	private void processRepeatOpponentYesNo(Response response, String answer) {
		if ("JA".equals(answer)) {
			//
		}
		else if ("NEIN".equals(answer)) {
			response.incrementSubStepCount();
		}
		else {
			if (falscheAuswahlText != null) {
				response.unknownAnswer(falscheAuswahlText);
			}
			else {
				response.unknownChoice(Arrays.asList("JA", "NEIN"), answer);
			}
		}
	}

	private void tellOpponentData(Response response, int opponentNum) {
		Opponent opponent = opponents.get(opponentNum-1);
		opponent.describe(response);
	}

	private void tellFightInfo(Response response) {
		int cntFriends = countOpponents(OpponentType.FRIEND);
		int cntFoes = countOpponents(OpponentType.FOE);
		if (kampfIntroText != null) {
			response.addDescription(kampfIntroText);
		}
		else {
			String opponentInfo = describeCount(cntFoes,"einen", "Gegner","Gegner") + " " + (cntFriends>0 ? " mit Unterstützung von "+describeCount(cntFriends,"einem", "Freund","Freunden") : "");
			response.addDescription("Es findet ein Kampf gegen "+opponentInfo+" statt.");
		}
		if (response.getCurrentStepHelp() == 0) {
			if (cntFoes + cntFriends > 1) {
				if (notiereDatenText != null) {
					response.addDescription(notiereDatenText);
				}
				else {
					response.addDescription("Notiere Dir die Daten der Gegner, sage 'Wiederhole alles', wenn ich nochmal die Daten aller Gegner aufzählen soll.");
				}
			}
			response.setCurrentStepHelp(1); 
		}
	}
	
	private String describeCount(int cnt, String oneWord, String singularWord, String pluralWord) {
		if (cnt == 1) {
			return oneWord + " " + singularWord;
		}
		return cnt + " " + pluralWord;
	}

	private int countOpponents(OpponentType type) {
		int result = 0;
		for (Opponent opponent:opponents) {
			if (opponent.getType() == type) {
				result += 1;
			}
		}
		return result;
	}

	@Override
	protected void processChoice(Response response, Integer choiceNum) {
		response.resetCurrentStepHelp();
		super.processChoice(response, choiceNum);
	}

	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("KAMPF:").append(TextUtil.endl);
		if (kampfIntroText != null) {
			result.append("KAMPFINTROTEXT: ").append(kampfIntroText).append(TextUtil.endl);
		}
		if (notiereDatenText != null) {
			result.append("NOTIEREDATENTEXT: ").append(notiereDatenText).append(TextUtil.endl);
		}
		if (datenWiederholenText != null) {
			result.append("DATENWIEDERHOLENTEXT: ").append(datenWiederholenText).append(TextUtil.endl);
		}
		if (sageErgebnisText != null) {
			result.append("SAGEERGEBNISTEXT: ").append(sageErgebnisText).append(TextUtil.endl);
		}
		if (falscheAuswahlText != null) {
			result.append("FALSCHEAUSWAHLTEXT: ").append(falscheAuswahlText).append(TextUtil.endl);
		}
		for (Opponent opponent:opponents) {
			result.append(opponent.toString()).append(TextUtil.endl);
		}
		for (Choice choice:choices) {
			result.append(choice.toString());
		}
		return result.toString();
	}

	
}
