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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.Response.QuestionType;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;

public class SoloRoleplayMain {

//	private final static String START_LINES = "JA|X|A|X|OK|JA|BIER|OK|JA|X|FEHLGESCHLAGEN|FEHLGESCHLAGEN|BESTANDEN|GEWONNEN|OK"; 
//	private final static String START_LINES = "JA|X"; 
	private final static String START_LINES = null; 
	
	public static void main(String[] args) {
		try {
//			String inputFilename = "RUN/soloabenteuer.txt";
//			String inputFilename = "input/beispiel_solo.txt";
			String inputFilename = "input/beispiel2_solo.txt";
//			String inputFilename = "input/goblinraub_solo.txt";
			if (args.length > 0) {
				inputFilename = args[0];
			}
			Path p = Paths.get(inputFilename);
			if (!Files.exists(p)) {
				System.err.println("Could not find input file '"+p.toString()+"'");
			}
			String text = new String(Files.readAllBytes(Paths.get(inputFilename)));
			SoloRoleplayParser parser = new SoloRoleplayParser();
			SoloRoleplayData soloRoleplay = parser.parse(text);
			soloRoleplay.validate();
//			System.out.println(soloRoleplay.toString());
			
			UserInput input = new UserInput(START_LINES, System.in);
			SpeechOutput out = new SpeechOutput();
			
			boolean shortenTextActive = false;
			
			SoloRoleplayGame game = new SoloRoleplayGame(soloRoleplay);
			out.out("Wilkommen zum Abenteuer '"+soloRoleplay.getTitle()+"' von "+soloRoleplay.getAuthor());
			out.out("");
			while (true) {
				Response response = game.executeFromLast(shortenTextActive);
				if (response.hasText()) {
					out.out(response.getText().toString());
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
				String line = input.nextLine();
				response = game.processAnswer(line, shortenTextActive);
				if (response.hasText()) {
					out.out(response.getText().toString());
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
			}

			input.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
