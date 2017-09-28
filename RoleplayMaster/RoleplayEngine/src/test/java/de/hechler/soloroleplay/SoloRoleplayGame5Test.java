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
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.Response.QuestionType;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.ValidationException;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloRoleplayGame5Test {

	private String soloText;
	private SoloRoleplayData soloData; 
	
	@Before
	public void setup() throws ValidationException {
		soloText = TextUtil.readResource("beispiel5_solo.txt", StandardCharsets.ISO_8859_1);
		SoloRoleplayParser parser = new SoloRoleplayParser();
		soloData = parser.parse(soloText);
		soloData.validate();
	}
	
	
	@Test
	public void testExampleSolo5Output() {
		String expected = TextUtil.readResource("expected5_solotext_output.txt", StandardCharsets.ISO_8859_1);
		expected = TextUtil.makeEndl(expected);
		String actual = TextUtil.makeEndl(soloData.toString());
		System.out.println(soloData.toString());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExampleSolo5() {
		UserInput input = new UserInput(null, getClass().getResourceAsStream("/solo5_user_input.txt"), StandardCharsets.ISO_8859_1.name());
		StringBuilder result = new StringBuilder();
		
		try {
			SoloRoleplayGame game = new SoloRoleplayGame(soloData);
			result.append("Wilkommen zum Abenteuer '"+soloData.getTitle()+"' von "+soloData.getAuthor()).append(TextUtil.endl);
			System.out.println(result.toString());
			result.append(TextUtil.endl);
			boolean shortenTextActive = false;
			while (true) {
				Response response = game.executeFromLast(shortenTextActive);
				if (response.hasText()) {
					System.out.println(response.getText().toString());
					result.append(response.getText().toString()).append(TextUtil.endl);
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
				String line;
				line = input.nextLine();
				System.out.println(" > "+line);
				System.out.println();
				result.append(" > ").append(line).append(TextUtil.endl);
				result.append(TextUtil.endl);
				response = game.processAnswer(line, shortenTextActive);
				if (response.hasText()) {
					System.out.println(response.getText().toString());
					result.append(response.getText().toString()).append(TextUtil.endl);
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
			}
			String expected = TextUtil.readResource("expected5_solorun_output.txt", StandardCharsets.ISO_8859_1);
			expected = TextUtil.makeEndl(expected);
			String actual = TextUtil.makeEndl(result.toString());
			Assert.assertEquals(expected, actual);
		} catch (NoSuchElementException e) {
//			System.out.println(result.toString());
			Assert.fail("end of input reached at: "+result.toString());
		}
	}
}
