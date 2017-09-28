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
package de.hechler.aigames.ai;

import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.FieldView;
import de.hechler.aigames.api.Move;

public interface AIGameIF<FV extends FieldView, M extends Move> extends IGame {

	void setPlayerNames(String player1Name, String player2Name);

	void setAILevel(int aiLevel);

	void setWeak(boolean weak);

	void restartGame();

	void changePlayer();

	void close();

	int getAILevel();

	int getCurrentPlayer();

	int getWinner();

	String getPlayer1Name();

	String getPlayer2Name();

	FV getField();

	M calcAIMove();

	DoMoveResult<M> doAIMove();

	DoMoveResult<M> doMove(M move);

	boolean switchPlayers();
}