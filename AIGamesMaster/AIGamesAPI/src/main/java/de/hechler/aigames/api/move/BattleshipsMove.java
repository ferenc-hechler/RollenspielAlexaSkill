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
package de.hechler.aigames.api.move;

import de.hechler.aigames.api.Move;

public class BattleshipsMove extends Move {

	public final static String FIELD_SIZE_PARAMETER = "FIELDSIZE";
	
	public static enum MoveType {QUERY, ANSWER};
	public static enum CellContent {
		UNKNOWN(0), WATER(1), SHIP(2), SUNK(3), NO_SHIP(4), INVALID(5);
		public final int code;
		private CellContent(int code){
			this.code=code;
		}
		public int getCode() {
			return code;
		}
		public static CellContent fromCode(int code) {
			return values()[code];
		}
	};
	
	public MoveType type;
	public char row;
	public int col;
	public CellContent content;
	public BattleshipsMove(MoveType type, char row, int col) {
		this(type, row, col, CellContent.UNKNOWN);
	}
	public BattleshipsMove(MoveType type, char row, int col, CellContent content) {
		this.type = type;
		this.row = row;
		this.col = col;
		this.content = content;
	}
	
	@Override
	public String toString() {
		return type+"("+row+","+col+"="+content+")";
	}
	public static BattleshipsMove createQueryFromNum(int rowNum, int colNum) {
		return new BattleshipsMove(MoveType.QUERY, (char) ('A'+rowNum), 1+colNum);
	}
	public int getRowNum() {
		return row-'A';
	}
	public int getColNum() {
		return col-1;
	}
	
}

