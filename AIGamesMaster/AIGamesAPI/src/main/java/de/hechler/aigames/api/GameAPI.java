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

public interface GameAPI<FV extends FieldView, M extends Move> {

	public NewGameResult createNewGame(int aiLevel);
	public NewGameResult createNewGame(int aiLevel, boolean weak);

	public GenericResult setPlayerNames(String gameId, String playerName1, String playerName2);
	public GenericResult setAILevel(String gameId, int aiLevel);
	public GenericResult hasChanges(String gameId, int lastChange);
	public GetGameDataResult<FV> getGameData(String gameId);
	
	public GenericResult activateGame(String gameId, String userId);
	public GetGameDataResult<FV> getGameDataByUserId(String userId);
	
	public DoMoveResult<M> doMove(String gameId, M move);
	public DoMoveResult<M> doAIMove(String gameId);

	public GenericResult switchPlayers(String gameId);
	public GenericResult changeGameParameter(String gameId, String gpName, String gpValue);
	public GetGameParameterResult getGameParameter(String gameId, String paramName);

	public GenericResult restart(String gameId);

	public GenericResult closeGame(String gameId);

}
