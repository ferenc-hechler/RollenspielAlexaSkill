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

import de.hechler.soloroleplay.util.TextUtil;

public class SpeechOutput {

	public void out(String text) {
		String lbText = lineBreak(text, 80);
		System.out.println(lbText);
	}

	private String lineBreak(String text, int maxlineLength) {
		StringBuilder result = new StringBuilder();
		String[] words = text.split(" +");
		int lineLength = 0;
		for (String word: words) {
			if (word.trim().isEmpty()) {
				continue;
			}
			if (word.indexOf('\n') != -1) {
				result.append(word);
				lineLength = word.length();
			}
			else if (lineLength + word.length() < maxlineLength) {
				if (lineLength > 0) {
					result.append(" ");
				}
				result.append(word);
				lineLength += word.length()+1;
			}
			else {
				result.append(TextUtil.endl).append(word);
				lineLength = word.length();
			}
		}
		return result.toString();
	}
	
}
