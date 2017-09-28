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
package de.hechler.aigames.api;

public enum ResultCodeEnum {

	S_OK(0),
	S_ACTIVATED(1),
	S_CONTINUE(2),
	
	S_NO_CHANGES(10),
	S_CHANGES_EXIST(11),
	
	S_PLAYER_WINS(20),
	S_DRAW(21),
	S_AI_PLAYER_WINS(22),
	S_AI_DRAW(23),
	
	E_UNKNOWN_GAMEID(100),
	
	E_INVALID_RANGE(200),
	E_GAME_FINISHED(201),
	E_INVALID_MOVE(202),

	E_UNKNOWN_COMMAND(900),
	E_INVALID_PARAMETER(901),
	
	E_UNKNOWN_ERROR(1000);

	int code;
	
	ResultCodeEnum(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	
}
