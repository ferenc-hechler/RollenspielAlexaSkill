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
package de.hechler.aigames.api.move;

import de.hechler.aigames.api.Move;

public class SoloRoleplayMove extends Move {

	public String description;
	
	public SoloRoleplayMove(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "DESC["+shorten(description, 40)+"]";
	}

	private String shorten(String text, int len) {
		if ((text == null) || (text.length() <= len)) {
			return text;
		}
		if (len <= 5) {
			return text.substring(0, len);
		}
		int textLen = len - 3;
		int beginLen = (int) (textLen * 0.67); 
		int endLen = textLen - beginLen;
		return text.substring(0, beginLen) + "..." + text.substring(text.length()-endLen);
	}

	
	
}

