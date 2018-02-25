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
package de.hechler.soloroleplay;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.Response.QuestionType;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.ValidationException;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;
import de.hechler.soloroleplay.util.TextUtil;

public class ValidationTest {

	@Test
	public void missingChapterBEGINN() {
		validateSolo("validation_01_solo.txt", "Es muss ein Kapitel mit dem Namen 'BEGINN' geben");
	}

	@Test
	public void missingChapterENDE() {
		validateSolo("validation_02_solo.txt", "Es muss ein Kapitel mit dem Namen 'ENDE' geben");
	}

	@Test
	public void unknownChapterReferenceWEITER() {
		validateSolo("validation_03_solo.txt", "WEITER: Unbekannte Refernez [2] in Kapitel 'BEGINN'.");
	}

	@Test
	public void unknownChapterReferenceJANEIN() {
		validateSolo("validation_04_solo.txt", "Auswahl 'JA': Unbekannte Referenz [1] in Kapitel 'BEGINN'.");
	}

	@Test
	public void unfinishedChapter() {
		validateSolo("validation_10_solo.txt", "Das Kapitel 'BEGINN' endet ohne Weiterleitung!");
	}

	@Test
	public void unfinishedChapterWEITER() {
		validateSolo("validation_11_solo.txt", "Das Kapitel 'BEGINN' endet ohne Weiterleitung!");
	}
	

	@Test
	public void unreachableChapter() {
		validateSolo("validation_20_solo.txt", "Das Kapitel '1' kann nicht erreicht werden.");
	}

	@Test
	public void validateBeispiel() {
		validateSolo("beispiel_solo.txt", "OK");
	}

	@Test
	public void validateBeispiel2() {
		validateSolo("beispiel2_solo.txt", "OK");
	}

	@Test
	public void validateOK() {
		validateSolo("der_sagenhafte_schatz_des_drachen_mordor_solo.txt", "OK");
		validateSolo("von_raeubern_und_herolden_solo.txt", "OK");
		validateSolo("der_hauptmann_von_punin.txt", "OK");
		validateSolo("ruecksprung_beispiel.txt", "OK");
		validateSolo("goblinraub_solo.txt", "OK");
		validateSolo("tolkienraetsel_solo.txt", "OK");
		validateSolo("kennenlernabenteuer_solo.txt", "OK");
	}

	
	
	
	
	private void validateSolo(String validationName, String expectedMessage) {
		try {
			String soloText = TextUtil.readResource(validationName, StandardCharsets.ISO_8859_1);
			SoloRoleplayParser parser = new SoloRoleplayParser();
			SoloRoleplayData soloData = parser.parse(soloText);
			soloData.validate();
			Assert.assertEquals("OK", expectedMessage);
		} catch (ValidationException e) {
			Assert.assertEquals(expectedMessage, e.getMessage());
		}
	}

	protected String play(SoloRoleplayData soloData, String inputText) {
		UserInput input = new UserInput(inputText, null);
		StringBuilder result = new StringBuilder();
		SoloRoleplayGame game = new SoloRoleplayGame(soloData);
		result.append("Starte Soloabenteuer '"+soloData.getTitle()+"' von "+soloData.getAuthor()).append(TextUtil.endl);
		result.append(TextUtil.endl);
		boolean shortenTextActive = false;
		while (true) {
			Response response = game.executeFromLast(shortenTextActive);
			result.append(response.getText().toString()).append(TextUtil.endl);
			if (response.isType(QuestionType.FINISHED)) {
				break;
			}
			if (response.isType(QuestionType.FINISHED)) {
				break;
			}
			String line = input.nextLine();
			result.append(" > ").append(line).append(TextUtil.endl);
			result.append(TextUtil.endl);
			response = game.processAnswer(line, shortenTextActive);
			if (response.hasText()) {
				result.append(response.getText().toString()).append(TextUtil.endl);
			}
			if (response.isType(QuestionType.FINISHED)) {
				break;
			}
		}
		String resultText = TextUtil.makeEndl(result.toString());
		return resultText;
	}

}
