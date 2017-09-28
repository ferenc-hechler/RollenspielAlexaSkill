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
package de.hechler.soloroleplay.parser;

import java.util.List;

import de.hechler.soloroleplay.parser.SoloRoleplayParser.Token;
import de.hechler.soloroleplay.parser.SoloRoleplayParser.TokenType;

public class TokenStream {

	private List<Token> tokens;
	private int currentIndex;
	
	public TokenStream(List<Token> tokens) {
		this.tokens = tokens;
		currentIndex = 0;
	}
	
	public Token nextToken() {
		if (currentIndex == -1) {
			return Token.EOFTOKEN;
		}
		Token result = tokens.get(currentIndex);
		currentIndex += 1;
		if (currentIndex >= tokens.size()) {
			currentIndex = -1;
		}
		return result;
	}
	
	public Token nextToken(TokenType type) {
		if (currentIndex == -1) {
			if (type == TokenType.EOF) {
				return null;
			}
			return Token.EOFTOKEN;
		}
		Token result = tokens.get(currentIndex);
		if (!result.isType(type)) {
			return null;
		}
		currentIndex += 1;
		if (currentIndex >= tokens.size()) {
			currentIndex = -1;
		}
		return result;
	}
	
	public void backToPreviousToken() {
		if (currentIndex > 0) {
			currentIndex -= 1;
		}
	}

	public Token peekToken() {
		if (currentIndex == -1) {
			return Token.EOFTOKEN;
		}
		Token result = tokens.get(currentIndex);
		return result;
	}

}
