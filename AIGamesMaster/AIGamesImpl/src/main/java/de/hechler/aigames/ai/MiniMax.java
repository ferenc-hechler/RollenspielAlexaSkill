/*
 * Created on 11.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MiniMax {

	private final static int WINNER_CALC = 100000;
	
	/**
	 * return WINNER_CALC if PLAYER_1 has won, -WINNER_CALC if PLAYER_2 has won.
	 * @param field
	 * @param calcDepth
	 * @param player
	 * @return
	 */
	public static int calcPosition(IField field, int calcDepth, int player) {
		int result = 0;
		if (calcDepth <= 0)
				result = field.calcPosition();
		else {
			int bestMove  = -1;
			int bestValue = 0;
			int winner = field.checkForWinner();
			if (winner != 0) {
				result = winner * (WINNER_CALC + calcDepth*1000);
			}
			else {
				IField[] nextMoves = field.nextMoves(player);
				for (int i = 0; i < nextMoves.length; i++) {
					IField nextField = nextMoves[i];
					int move = nextField.getLastMove();
					int calcNextField = calcPosition(nextField, calcDepth-1, -player);
					int value = calcNextField * player; // value muss immer maximiert werden (player1 = 1, player2 = -1)
					// Daten des ersten moeglichen Zuges uebernehmen
					if (bestMove == -1) {
						bestMove = move;
						bestValue = value;
					}
					else { 
						// fuer alle weiteren Zuege nur wenn besser
						if (bestValue < value) {
							bestValue = value;
							bestMove = move;
						}
					}
				}
				result = bestValue * player;
			}
		}
		return result;
	}

	public static int selectBestMove(IField field, int calcDepth, int player) {
		int bestMove  = -1;
		int bestValue = 0;
		IField[] nextMoves = field.nextMoves(player);
		for (int i = 0; i < nextMoves.length; i++) {
			IField nextField = nextMoves[i];
			int move = nextField.getLastMove();
			int calcNextField = calcPosition(nextField, calcDepth-1, -player);
			int value = calcNextField * player; // value muss immer maximiert werden (player1 = 1, player2 = -1)
//			System.out.println("move: " + move + ", calc=" + calcNextField);
			// Daten des ersten moeglichen Zuges uebernehmen
			if (bestMove == -1) {
				bestMove = move;
				bestValue = value;
			}
			else { 
				// fuer alle weiteren Zuege nur wenn besser
				if (bestValue < value) {
					bestValue = value;
					bestMove = move;
				}
			}
		}
		return bestMove;
	}



}
