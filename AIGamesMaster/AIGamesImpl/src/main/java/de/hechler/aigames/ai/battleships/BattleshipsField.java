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

import de.hechler.aigames.api.fieldview.BattleshipsFieldView;
import de.hechler.aigames.api.move.BattleshipsMove;
import de.hechler.utils.RandUtils;

public class BattleshipsField {

	public static Random testRandom = null;
	
//	public final static int DEFAULT_SIZE = 10;
//	public final static int[] DEFAULT_NUM_SHIPS = {0, 4, 3, 2, 1}; 
	public final static int DEFAULT_SIZE = 5;
	public final static int[][] DEFAULT_NUM_SHIPS = {
			{1},     					//  1 x 1
			{0, 1},						//  2 x 2
			{0, 1},						//  3 x 3
			{0, 2},						//  4 x 4
			{0, 2, 1},					//  5 x 5
			{0, 2, 2},					//  6 x 6
			{0, 3, 2, 1},				//  7 x 7
			{0, 4, 3, 1},				//  8 x 8
			{0, 4, 3, 2},				//  9 x 9
			{0, 4, 3, 2, 1}				// 10 x 10
	}; 
	
	public final static int UNKNOWN = BattleshipsMove.CellContent.UNKNOWN.getCode();
	public final static int WATER = BattleshipsMove.CellContent.WATER.getCode();
	public final static int SHIP = BattleshipsMove.CellContent.SHIP.getCode();
	public final static int SUNK = BattleshipsMove.CellContent.SUNK.getCode();
	public final static int NO_SHIP = BattleshipsMove.CellContent.NO_SHIP.getCode();
	public final static int INVALID = BattleshipsMove.CellContent.INVALID.getCode();

	public static class CellContentHolder {
		int cellContent;
	}

	int fieldSize;
	int[] numShips; 

	int aiPlayer;
	
	int[][] ownField;
	int[][] visibleField;
	int[][] otherField;

	private int lastAIQueryRow = -1;
	private int lastAIQueryCol = -1;
	private int lastAIQueryContent = INVALID;
	private int lastPlayerQueryRow = -1;
	private int lastPlayerQueryCol = -1;

	public BattleshipsField(int aiPlayer) {
		this(aiPlayer, DEFAULT_SIZE);
	}

	public BattleshipsField(int aiPlayer, int fieldSize) {
		this(aiPlayer, fieldSize, DEFAULT_NUM_SHIPS[fieldSize-1]);
	}

	public BattleshipsField(int aiPlayer, int fieldSize, int[] numShips) {
		this.aiPlayer = aiPlayer;
		this.fieldSize = fieldSize;
		this.numShips = numShips;
		this.otherField = new int[fieldSize][fieldSize];
		this.visibleField = new int[fieldSize][fieldSize];
		initOwnField();
	}

	private void initOwnField() {
		int maxCnt = 9999;
		while (true) {
			this.ownField = new int[fieldSize][fieldSize];
			fill(ownField, WATER);
			try {
				for (int n=0; n<numShips.length; n++) {
					int size = n+1;
					for (int cnt=0; cnt<numShips[n]; cnt++) {
						setOwnShip(size);
					}
				}
				replace(ownField, NO_SHIP, WATER);
				return;
			}
			catch (RuntimeException e) {
				if (maxCnt-- < 0) {
					throw e;
				}
			}
		}
	}

	
	
	private void replace(int[][] field, int oldContent, int newContent) {
		for (int row=0; row<fieldSize; row++) {
			for (int col=0; col<fieldSize; col++) {
				if (field[row][col] == oldContent) {
					field[row][col] = newContent;
				}
			}
		}
	}

	private void fill(int[][] field, int content) {
		for (int row=0; row<fieldSize; row++) {
			for (int col=0; col<fieldSize; col++) {
				field[row][col] = content;
			}
		}
	}

//	public boolean setField(int player, int row, int col, CellContentHolder content) {
//		if (player == aiPlayer) {
//			content.cellContent = setOwnField(row, col);
//			return (content.cellContent != INVALID); 
//		}
//		else {
//			return setOtherField(row, col, content.cellContent);
//		}
//	}

//	private boolean setOtherField(int row, int col, int content) {
//		int oldContent = getOtherField(row,col); 
//		if ((oldContent != UNKNOWN) && (oldContent != NO_SHIP)) {
//			return false;
//		}
//		if (content == SUNK) {
//			otherField[row][col] = SHIP;
//			sinkOtherShip(row, col);
//		}
//		else {
//			otherField[row][col] = content;
//		}
//		return true;
//	}

//	private void sinkOtherShip(int row, int col) {
//		sinkShip(otherField, row, col);
//	}

//	private void sinkOwnShip(int row, int col) {
//		sinkShip(ownField, row, col);
//	}

	private void sinkShip(int[][] field, int row, int col) {
		int c = getField(field, row, col); 
		if (c == SHIP) {
			field[row][col] = SUNK;
			for (int dr=-1; dr<=1; dr++) {
				for (int dc=-1; dc<=1; dc++) {
					sinkShip(field, row+dr, col+dc);
				}
			}
		}
		else if (c == UNKNOWN) {
			field[row][col] = NO_SHIP;
		}
	}

//	private int setOwnField(int row, int col) {
//		int c = getOwnField(row, col);
//		if (c == SUNK) {
//			visibleField[row][col] = SHIP;
//			sinkOwnShip(row, col);
//		}
//		else if (c != INVALID) {
//			visibleField[row][col] = c;
//		}
//		return c;
//	}

	private int getOtherField(int row, int col) {
		return getField(otherField, row, col);
	}
	private int getOwnField(int row, int col) {
		return getField(ownField, row, col);
	}
	private int getVisibleField(int row, int col) {
		return getField(visibleField, row, col);
	}
	private int getField(int[][] field, int row, int col) {
		if ((row<0) || (row>=fieldSize) || (col<0) || (col>=fieldSize)) {
			return INVALID;
		}
		return field[row][col];
	}

	public int checkForEndGame() {
		int sumShipLen = 0;
		for (int n=0; n<numShips.length; n++) {
			sumShipLen += numShips[n]*(n+1);
		}
		if (cntSunkShipLen(otherField) == sumShipLen) {
			return aiPlayer;
		}
		if (cntSunkShipLen(visibleField) == sumShipLen) {
			return 3-aiPlayer;
		}
		return 0;
	}

	public BattleshipsFieldView createExportField() {
		BattleshipsFieldView result = new BattleshipsFieldView();
		if (aiPlayer == 1) {
			result.p1Field = exportField(visibleField, lastPlayerQueryRow, lastPlayerQueryCol);
			result.p2Field = exportField(otherField, lastAIQueryRow, lastAIQueryCol);
		}
		else {
			result.p1Field = exportField(otherField, lastAIQueryRow, lastAIQueryCol);
			result.p2Field = exportField(visibleField, lastPlayerQueryRow, lastPlayerQueryCol);
		}
		result.numShips = numShips;
		return result;
	}
	
	
	private int[][] exportField(int[][] field, int lastRow, int lastCol) {
		int[][] result = new int[fieldSize][fieldSize];
		for (int row=0; row<fieldSize; row++) {
			for (int col=0; col<fieldSize; col++) {
				result[row][col] = field[row][col];
			}
		}
		if (lastRow != -1) {
			result[lastRow][lastCol] += 6;
		}
		return result;
	}

	private void setOwnShip(int size) {
		int maxcnt = 9999; 
		while (true) {
			if (maxcnt-- < 0) {
				throw new RuntimeException("could not set ships for fieldSize "+fieldSize+" ...");
			}
			boolean horizontal = RandUtils.randomBoolean(testRandom);
			if (horizontal) {
				int row = RandUtils.randomInt(testRandom, fieldSize);
				int col = RandUtils.randomInt(testRandom, fieldSize+1-size);
				for (int n=0; n<size; n++) {
					if (!isOwnFree(row, col+n)) {
						row = -1;
						break;
					}
				}
				if (row < 0) {
					continue;
				}
				for (int n=0; n<size; n++) {
					setOwnField(row-1, col+n, NO_SHIP);
					setOwnField(row,   col+n, SHIP);
					setOwnField(row+1, col+n, NO_SHIP);
				}
				setOwnField(row-1, col-1, NO_SHIP);
				setOwnField(row,   col-1, NO_SHIP);
				setOwnField(row+1, col-1, NO_SHIP);
				setOwnField(row-1, col+size, NO_SHIP);
				setOwnField(row,   col+size, NO_SHIP);
				setOwnField(row+1, col+size, NO_SHIP);
				break;
			}
			else {
				int row = RandUtils.randomInt(testRandom, fieldSize+1-size);
				int col = RandUtils.randomInt(testRandom, fieldSize);
				for (int n=0; n<size; n++) {
					if (!isOwnFree(row+n, col)) {
						row = -1;
						break;
					}
				}
				if (row < 0) {
					continue;
				}
				for (int n=0; n<size; n++) {
					setOwnField(row+n, col-1, NO_SHIP);
					setOwnField(row+n, col,   SHIP);
					setOwnField(row+n, col+1, NO_SHIP);
				}
				setOwnField(row-1, col-1, NO_SHIP);
				setOwnField(row-1, col,   NO_SHIP);
				setOwnField(row-1, col+1, NO_SHIP);
				setOwnField(row+size, col-1, NO_SHIP);
				setOwnField(row+size, col,   NO_SHIP);
				setOwnField(row+size, col+1, NO_SHIP);
				break;
			}
		}
	}

	private void setOwnField(int row, int col, int n) {
		if ((row<0) || (row>=fieldSize) || (col<0) || (col>=fieldSize)) {
			return;
		}
		ownField[row][col] = n;
	}

	private boolean isOwnFree(int row, int col) {
		if ((row<0) || (row>=fieldSize) || (col<0) || (col>=fieldSize)) {
			return false;
		}
		return ownField[row][col] == WATER;
	}

	public boolean checkRange(int row, int col) {
		if ((row<0) || (row>=fieldSize) || (col<0) || (col>=fieldSize)) {
			return false;
		}
		return true;
	}

	private int cntSunkShipLen(int[][] field) {
		return cntCellsWithContent(field, SUNK);
	}

	public int cntFreeOtherFields() {
		return cntCellsWithContent(otherField, UNKNOWN);
	}

	private int cntCellsWithContent(int[][] field, int content) {
		int result = 0;
		for (int row=0; row<fieldSize; row++) {
			for (int col=0; col<fieldSize; col++) {
				if (field[row][col] == content) {
					result += 1;
				}
			}
		}
		return result;
	}

	public int[] findNthFreeOtherField(int n) {
		return findNthContent(otherField, UNKNOWN, n);
	}

	public int[] findFirstShip() {
		return findNthContent(otherField, SHIP, 1);
	}

	public int[] findNthContent(int[][] field, int content, int n) {
		int cnt = 0;
		for (int row=0; row<fieldSize; row++) {
			for (int col=0; col<fieldSize; col++) {
				if (field[row][col] == content) {
					cnt += 1;
					if (n == cnt) {
						return new int[]{row,col};
					}
				}
			}
		}
		return null;
	}

	private boolean checkSunk(int row, int col) {
		if (!checkSunk(row, col, -1,  0)) {
			return false;
		}
		if (!checkSunk(row, col,  1,  0)) {
			return false;
		}
		if (!checkSunk(row, col,  0, -1)) {
			return false;
		}
		if (!checkSunk(row, col,  0,  1)) {
			return false;
		}
		return true;
	}

	private boolean checkSunk(int row, int col, int dRow, int dCol) {
		for (int i=1; i<fieldSize; i++) {
			int content = getOwnField(row+i*dRow, col+i*dCol);
			if (content != SHIP) {
				return true;
			}
			if (getVisibleField(row+i*dRow, col+i*dCol) != SHIP) {
				return false;
			}
		}
		return true;
	}

	
	public int query(int currentPlayer, int row, int col) {
		if (currentPlayer == aiPlayer) {
			return INVALID;
		}
		int content = getVisibleField(row, col);
		if (content != UNKNOWN) {
			if (content == NO_SHIP) {
				content = WATER;
			}
			return content;
		}
		content = getOwnField(row, col);
		visibleField[row][col] = content;
		if (content == SHIP) {
			if (checkSunk(row, col)) {
				sinkShip(visibleField, row, col);
				content = SUNK;
			}
		}
		lastPlayerQueryRow = row;
		lastPlayerQueryCol = col;
		return content;
	}
	
	public boolean answer(int currentPlayer, int content) {
		if (currentPlayer != aiPlayer) {
			return false;
		}
		int row = lastAIQueryRow;
		int col = lastAIQueryCol; 
		if (!checkRange(row, col)) {
			return false;
		}
		if (content == SUNK) {
			otherField[row][col] = SHIP;
			sinkShip(otherField, row, col);
		}
		else if ((content == WATER) || (content == SHIP)) {
			otherField[row][col] = content;
		}
		else {
			return false;
		}
		lastAIQueryContent = content;
		return true;
	}

	public int getLastAnswerContent() {
		return lastAIQueryContent;
	}
	
	private final static int[][] neighbours = {
			{-1,  0},
			{ 0, -1},
			{ 0,  1},
			{ 1,  0}
	};
	private final static int[][] verticalNeighbours = {
			{-1,  0},
			{ 1,  0}
	};
	private final static int[][] horizontalNeighbours = {
			{ 0, -1},
			{ 0,  1},
	};

	public int[] followShip(int row, int col) {
		int minRow = row - followShip(row, col, -1,  0);  
		int maxRow = row + followShip(row, col, +1,  0);  
		int minCol = col - followShip(row, col,  0, -1);  
		int maxCol = col + followShip(row, col,  0, +1);  
		if (minRow != maxRow) {
			return selectRandom(minRow, maxRow, minCol, maxCol, verticalNeighbours);
		}
		if (minCol != maxCol) {
			return selectRandom(minRow, maxRow, minCol, maxCol, horizontalNeighbours);
		}
		return selectRandom(minRow, maxRow, minCol, maxCol, neighbours);
	}

	private int[] selectRandom(int minRow, int maxRow, int minCol, int maxCol, int[][] dRow_dCol_List) {
		int start = RandUtils.randomInt(testRandom, dRow_dCol_List.length);
		for (int i=0; i<dRow_dCol_List.length; i++) {
			int[] dRow_dCol = dRow_dCol_List[start];
			if (getOtherField(minRow+dRow_dCol[0], minCol+dRow_dCol[1]) == UNKNOWN) {
				return new int[]{minRow+dRow_dCol[0], minCol+dRow_dCol[1]};
			}
			if (getOtherField(maxRow+dRow_dCol[0], minCol+dRow_dCol[1]) == UNKNOWN) {
				return new int[]{maxRow+dRow_dCol[0], minCol+dRow_dCol[1]};
			}
			if (getOtherField(minRow+dRow_dCol[0], maxCol+dRow_dCol[1]) == UNKNOWN) {
				return new int[]{minRow+dRow_dCol[0], maxCol+dRow_dCol[1]};
			}
			if (getOtherField(maxRow+dRow_dCol[0], maxCol+dRow_dCol[1]) == UNKNOWN) {
				return new int[]{maxRow+dRow_dCol[0], maxCol+dRow_dCol[1]};
			}
			start = start + 1;
			if (start >= dRow_dCol_List.length) {
				start = 0;
			}
		}
		return null;
	}

	private int followShip(int row, int col, int dRow, int dCol) {
		for (int i=1; i<=fieldSize; i++) {
			if (getOtherField(row+i*dRow, col+i*dCol) != SHIP) {
				return i-1;
			}
		}
		throw new RuntimeException("followShip failed!");
	}

	public void aiQuery(int row, int col) {
		lastAIQueryRow = row;
		lastAIQueryCol = col;
	}

	/**
	 * return last ai query if queried field is still unknown.
	 * Used to re-query the last ai move.
	 * @return
	 */
	public int[] unansweredAiQuery() {
		if (getOtherField(lastAIQueryRow, lastAIQueryCol) == UNKNOWN) {
			return new int[]{lastAIQueryRow, lastAIQueryCol};
		}
		return null;
	}


}
