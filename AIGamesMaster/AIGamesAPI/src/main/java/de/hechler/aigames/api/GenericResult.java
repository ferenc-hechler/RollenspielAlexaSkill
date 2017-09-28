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

public class GenericResult {

	public final static GenericResult genericOkResult = new GenericResult(ResultCodeEnum.S_OK);
	public final static GenericResult genericNoChangeResult = new GenericResult(ResultCodeEnum.S_NO_CHANGES);
	public final static GenericResult genericChangesExistResult = new GenericResult(ResultCodeEnum.S_CHANGES_EXIST);
	public final static GenericResult genericUnknownGameId = new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
	public final static GenericResult genericInvalidParameterResult = new GenericResult(ResultCodeEnum.E_INVALID_PARAMETER);
	public final static GenericResult genericUnknownCommandResult = new GenericResult(ResultCodeEnum.E_UNKNOWN_COMMAND);
	public final static GenericResult genericUnknownErrorResult = new GenericResult(ResultCodeEnum.E_UNKNOWN_ERROR);
	public final static GenericResult genericGameAlreadyFinished = new GenericResult(ResultCodeEnum.E_GAME_FINISHED);
	
	public ResultCodeEnum code;

	public GenericResult(ResultCodeEnum code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return "R["+code+"]";
	}

}
