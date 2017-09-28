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
package de.hechler.aigames.ai.battleships;

import java.util.Random;

import de.hechler.aigames.ai.AIGame;
import de.hechler.aigames.ai.IPersistentGameData;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.BattleshipsFieldView;
import de.hechler.aigames.api.move.BattleshipsMove;
import de.hechler.aigames.api.move.BattleshipsMove.CellContent;
import de.hechler.aigames.api.move.BattleshipsMove.MoveType;
import de.hechler.utils.RandUtils;


public class BattleshipsGame extends AIGame<BattleshipsFieldView, BattleshipsMove> {
	
	public static final String GAME_PHASE_FINISHED = "F";
	public static final String GAME_PHASE_QUERY = "Q";
	public static final String GAME_PHASE_ANSWER = "A";
	
	protected BattleshipsField field;
	
	public static Random testRandom;

	@Override
	protected void initGame() {
		super.initGame();
		setGamePhase(GAME_PHASE_QUERY);
	}

	@Override
	public DoMoveResult<BattleshipsMove> doMove(BattleshipsMove move) {
		BattleshipsMove returnMove = null;
		if (hasWinner()) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_GAME_FINISHED);
		}
		boolean change = false;
		if (move.type == MoveType.QUERY) {
			if (!field.checkRange(move.getRowNum(), move.getColNum())) {
				return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_INVALID_RANGE);
			}
			int contentCode = field.query(currentPlayer, move.getRowNum(), move.getColNum());
			CellContent content = CellContent.fromCode(contentCode);
			if (content == CellContent.INVALID) {
				return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_INVALID_MOVE);
			}
			returnMove = new BattleshipsMove(MoveType.ANSWER, move.row, move.col, content);
			if (content == CellContent.WATER) {
				change = true;
			}
		}
		else if (move.type == MoveType.ANSWER) {
			boolean moveOK = field.answer(currentPlayer, move.content.getCode());
			if (!moveOK) {
				return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_INVALID_MOVE);
			}
			if (move.content == CellContent.WATER) {
				change = true;
			}
		}
		else {
			throw new RuntimeException("UNKNOWN TYPE "+move.type);
		}
		winner = field.checkForEndGame();
		if (winner != 0) {
			setGamePhase(GAME_PHASE_FINISHED);
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.S_PLAYER_WINS, returnMove);
		}
		setGamePhase(GAME_PHASE_QUERY);
		if (!change) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.S_CONTINUE, returnMove);
		}
		changePlayer();
		return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.S_OK, returnMove);
	}
	
	@Override
	public DoMoveResult<BattleshipsMove> doAIMove() {
		if (hasWinner()) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_GAME_FINISHED);
		}
		BattleshipsMove aiMove = calcAIMove();
		if (aiMove == null) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_INVALID_MOVE);
		}
		setGamePhase(GAME_PHASE_ANSWER);
		return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.S_OK, aiMove);
	}
	
	@Override
	public BattleshipsMove calcAIMove() {
		if (currentPlayer != field.aiPlayer) {
			return null;
		}
		int[] row_col = field.unansweredAiQuery();
		if (row_col != null) {
			return BattleshipsMove.createQueryFromNum(row_col[0], row_col[1]);
		}
		int[] firstShip_row_col = field.findFirstShip();
		if (firstShip_row_col != null) {
			row_col = field.followShip(firstShip_row_col[0], firstShip_row_col[1]);
			if (row_col == null) {
				return null;
			}
			field.aiQuery(row_col[0], row_col[1]);
			return BattleshipsMove.createQueryFromNum(row_col[0], row_col[1]);
		}
		int numFree = field.cntFreeOtherFields();
		if (numFree == 0) {
			return null;
		}
		int selected = RandUtils.randomInt(testRandom, numFree)+1;
		row_col = field.findNthFreeOtherField(selected);
		field.aiQuery(row_col[0], row_col[1]);
		return BattleshipsMove.createQueryFromNum(row_col[0], row_col[1]);
	} 
	
	
	@Override
	public BattleshipsFieldView getField() {
		return field.createExportField();
	}

	@Override
	protected void createField() {
		if (field != null) {
			field = new BattleshipsField(field.aiPlayer, field.fieldSize, field.numShips);
		}
		else {
			field = new BattleshipsField(2);
		}
	}

	public GetGameParameterResult getGameParameter(String gpName) {
		if (BattleshipsMove.FIELD_SIZE_PARAMETER.equals(gpName)) {
			return new GetGameParameterResult(ResultCodeEnum.S_OK, Integer.toString(field.fieldSize));
		}
		return new GetGameParameterResult(ResultCodeEnum.E_INVALID_PARAMETER);
	}

	public boolean changeGameParameter(String gpName, String gpValue) {
		if (BattleshipsMove.FIELD_SIZE_PARAMETER.equals(gpName)) {
			try {
				int newSize = Integer.parseInt(gpValue);
				if ((newSize < 2) || (newSize > 10)) {
					return false;
				}
				if (newSize == field.fieldSize) {
					return true;
				}
				field = new BattleshipsField(2, newSize);
				return true;
			}
			catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
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
