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

import java.util.Random;

import de.hechler.aigames.api.FieldView;
import de.hechler.aigames.api.Move;
import de.hechler.utils.RandUtils;


public abstract class AIGame<FV extends FieldView, M extends Move> implements AIGameIF<FV, M> {

	public static Random testRandom;
	
	protected String player1Name;
	protected String player2Name;
	protected int aiLevel;
	protected boolean weak;
	protected int currentPlayer;
	protected String gamePhase;
	protected int winner;

	public AIGame() {
		this.player1Name = "Player-1";
		this.player2Name = "Player-2";
		this.aiLevel = 2;
		this.weak = false;
		initGame();
	}

	protected void initGame() {
		currentPlayer = 1;
		winner = 0;
		gamePhase = null;
		createField();
	}

	protected abstract void createField();


	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#setPlayerNames(java.lang.String, java.lang.String)
	 */
	@Override
	public void setPlayerNames(String player1Name, String player2Name) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
	}
	

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#setAILevel(int)
	 */
	@Override
	public void setAILevel(int aiLevel) {
		this.aiLevel = aiLevel;
	}
	
	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#setWeak(boolean)
	 */
	@Override
	public void setWeak(boolean weak) {
		this.weak = weak;
	}
	
	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#restartGame()
	 */
	@Override
	public void restartGame() {
		initGame();
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#changePlayer()
	 */
	@Override
	public void changePlayer() {
		currentPlayer = 3-currentPlayer;
	}
	

	public void setGamePhase(String gamePhase) {
		this.gamePhase = gamePhase;
	}
	
	public String getGamePhase() {
		return gamePhase;
	}

	int[] lowerAILevelBounds = {1,1,1,2,3,4,7};
	int[] upperAILevelBounds = {1,2,3,4,5,6,7};
	
	protected int getEffectiveAILevel() {
		if (!weak) {
			return aiLevel;
		}
		int upper = upperAILevelBounds[aiLevel];
		int lower = lowerAILevelBounds[aiLevel];
		if (upper == lower) {
			return upper;
		}
		return lower + RandUtils.randomInt(testRandom, upper-lower);
	}

	protected boolean hasWinner() {
		return winner != 0;
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#close()
	 */
	@Override
	public void close() {
		// nothing to clean up.
	}

	@Override
	public String toString() {
		return "G["+player1Name+":"+player2Name+"]";
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#getAILevel()
	 */
	@Override
	public int getAILevel() {
		return aiLevel;
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#getCurrentPlayer()
	 */
	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#getWinner()
	 */
	@Override
	public int getWinner() {
		return winner;
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#getPlayer1Name()
	 */
	@Override
	public String getPlayer1Name() {
		return player1Name;
	}

	/* (non-Javadoc)
	 * @see de.hechler.aigames.ai.IGame#getPlayer2Name()
	 */
	@Override
	public String getPlayer2Name() {
		return player2Name;
	}

	@Override
	public boolean switchPlayers() {
		changePlayer();
		return true;
	}


}
