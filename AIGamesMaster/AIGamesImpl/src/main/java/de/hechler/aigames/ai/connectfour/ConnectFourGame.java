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
package de.hechler.aigames.ai.connectfour;

import de.hechler.aigames.ai.AIGame;
import de.hechler.aigames.ai.IField;
import de.hechler.aigames.ai.IPersistentGameData;
import de.hechler.aigames.ai.MiniMax;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.ConnectFourFieldView;
import de.hechler.aigames.api.move.ConnectFourMove;


public class ConnectFourGame extends AIGame<ConnectFourFieldView, ConnectFourMove> {
	
	protected ConnectFourField field;
	
	@Override
	public DoMoveResult<ConnectFourMove> doMove(ConnectFourMove move) {
		if ((move.slot < 1) || (move.slot > 7)) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_INVALID_RANGE);
		}
		if (hasWinner()) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_GAME_FINISHED);
		}
		boolean moveOK = field.setFieldArray(move.slot, currentPlayer);
		if (!moveOK) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_INVALID_MOVE);
		}
		winner = field.checkForEndGame();
		if (winner == -1) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_DRAW);
		} else  if (winner != 0) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_PLAYER_WINS);
		}
		changePlayer();
		return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_OK);
	}
	
	@Override
	public DoMoveResult<ConnectFourMove> doAIMove() {
		if (hasWinner()) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_GAME_FINISHED);
		}
		ConnectFourMove aiMove = calcAIMove();
		field.setFieldArray(aiMove.slot, currentPlayer);
		winner = field.checkForEndGame();
		if (winner == -1) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_AI_DRAW, aiMove);
		} else  if (winner != 0) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_AI_PLAYER_WINS, aiMove);
		}
		changePlayer();
		return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.S_OK, aiMove);
	}
	
	@Override
	public ConnectFourMove calcAIMove() {
		int xPlayer = ((currentPlayer==1) ? IField.PLAYER_1 : IField.PLAYER_2 );
//		int calc = MiniMax.calcPosition(field, 0, xPlayer);
//		System.out.println("Calc(0) = " + calc);
		int best = MiniMax.selectBestMove(field, getEffectiveAILevel(), xPlayer);
//		System.out.println("bestMove("+aiLevel+") = " + best);
		return new ConnectFourMove(best);
	} 
	
	
	@Override
	public ConnectFourFieldView getField() {
		return field.createExportField();
	}

	@Override
	protected void createField() {
		field = new ConnectFourField();
	}

	@Override
	public IPersistentGameData getPersistentGameData() {
		return null;
	}

	@Override
	public void restoreFromPersistentData(IPersistentGameData persistentData) {
		throw new RuntimeException("PERSISTENTDATA NOT SUPPORTED.");
	}

}
