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

import java.util.Random;
import java.util.Vector;

import de.hechler.aigames.ai.IField;
import de.hechler.aigames.api.fieldview.ConnectFourFieldView;
import de.hechler.utils.RandUtils;

public class ConnectFourField implements Cloneable, IField {

	public static Random testRandom = null;

	private final static int xsize = 7;
	private final static int ysize = 6;
	private int lastMove = -1;
	private int lastX = -1;
	private int lastY = -1;

	private int[][] fieldArray;
	
	
	public ConnectFourField() {
		fieldArray = new int[xsize][ysize];
	}
	
	private ConnectFourField(int[][] copyField) {
		this();
		for (int x=0; x<xsize; x++) {
			for (int y=0; y<ysize; y++) {
				fieldArray[x][y] = copyField[x][y];
			}	
		}
	}
	
	
	public ConnectFourField copy() {
		ConnectFourField result = new ConnectFourField(fieldArray);
		return result;
	}
	
	/**
	 * @param slot
	 * @param player
	 * @return false if slot is already filled
	 */
	public boolean setFieldArray(int slot, int player) {
		// HOT-FIX
		if (player == -1)
			player = 2;
		boolean moveOK = false;
		if ((slot >= 1) && (slot <= 7))
		{
			int x = slot - 1;
			for (int y=5; y>=0; y--)
			{
				if (fieldArray[x][y] == 0)
				{
					moveOK = true;
					fieldArray[x][y] = player;
					lastX = x;
					lastY = y;
					break;
				}
			}
		}
		if (moveOK)
			lastMove = slot;
		return moveOK;
	}
	
	public int checkForWinner () {
		int result;
		
		int value = calcPosition();
		if (value < -50000)
			result = IField.PLAYER_2;
		else if (value > 50000)
			result = IField.PLAYER_1;
        else 
        	result = 0;
    	return result;
	}	
	
	
	public int checkForEndGame() {
		int result;
		
		int value = calcPosition();
		if (value < -50000)
			result = 2;
		else if (value > 50000)
			result = 1;
        else if (hasFreeSlot())
        	result = 0;
        else 
        	result = -1;
    	return result;
	}	
	

	
	public int calcPosition() {
		int mark = 0;
		
		//waagrechte 4-er Kettten
        for (int x=0; x<=3; x++)
        {
                for (int y=0; y<=5; y++)
                {
                	mark += calc(fieldArray[x][y], fieldArray[x+1][y], fieldArray[x+2][y], fieldArray[x+3][y]);
                }
        }
        
        //senkrecht
        for (int x=0; x<=6; x++)
        {
                for (int y=0; y<=2; y++)
                {
                	mark += calc(fieldArray[x][y], fieldArray[x][y+1], fieldArray[x][y+2], fieldArray[x][y+3]);
                }
        }
        
        //diagonale
        for (int x=0; x<=3; x++)
        {
                for (int y=0; y<=2; y++)
                {
                	mark += calc(fieldArray[x][y], fieldArray[x+1][y+1], fieldArray[x+2][y+2], fieldArray[x+3][y+3]);
                }
        }
        for (int x=0; x<=3; x++)
        {
                for (int y=5; y>=3; y--)
                {
                	mark += calc(fieldArray[x][y], fieldArray[x+1][y-1], fieldArray[x+2][y-2], fieldArray[x+3][y-3]);
                }
        }
        return mark;
	}	
	
	public boolean hasFreeSlot() {
        for (int x=0; x<=6; x++)
        {
        	if (fieldArray[x][0] == 0) {
        		return true;
        	}
        }
        return false;
	}	
	
	
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int y = 0; y < ysize; y++) {
			for (int x = 0; x < xsize; x++) {
				int element = fieldArray[x][y];
				switch (element) {
				case 0:
					result.append(".");
					break;
				case 1:
					result.append("X");
					break;
				case 2:
					result.append("O");
					break;
				default:
					break;
				}
			}
			result.append("\r\n");
		}
		return result.toString();
	}
	
	public IField[] nextMoves(int player) {
		Vector<ConnectFourField> result = new Vector<ConnectFourField>();
		for (int i=1; i<=7; i++) {
			ConnectFourField copyField = this.copy();
			if (copyField.setFieldArray(i, player))
				result.add(copyField);
		}
		return (ConnectFourField[]) result.toArray(new ConnectFourField[result.size()]);
	}
	
	private static int[] weight={0,1,10,100,100000};

	/**
	 * statische Berwertungsfunktion.
	 * positiv, wenn besser fuer Spieler 1,
	 * negativ, wenn besser fuer Spieler 2,
	 * 0 fuer ausgeglichen
	 * @return
	 */
	public int calc(int a, int b, int c, int d) {
		int cntP1 = (a==1?1:0) + (b==1?1:0) + (c==1?1:0) + (d==1?1:0);
		int cntP2 = (a==2?1:0) + (b==2?1:0) + (c==2?1:0) + (d==2?1:0);
		
		int sum = 0;
		if ((cntP1 > 0) && (cntP2 > 0))
			sum = 0;
		else if (cntP1 > 0)
			sum = weight[cntP1];
		else 
			sum = -weight[cntP2];
		return sum;
	}

	public int compiMove() {
		return  RandUtils.randomInt(testRandom, 7) + 1;
	}

	public int getLastMove() {
		return lastMove;
	}

	public ConnectFourFieldView createExportField() {
		ConnectFourFieldView result = new ConnectFourFieldView();
		result.field = new int[ysize][xsize];
		for (int x=0; x<xsize; x++) {
			for (int y=0; y<ysize; y++) {
				result.field[y][x] = fieldArray[x][y];

			}
		}
		if ((lastX >= 0) && (lastY >= 0)) {
			result.field[lastY][lastX] += 2;
		}
		return result;
	}


//	public static void main(String[] args) {
//		int x;
//		Field first = new Field();
//		for (int k=1; k<=4; k++)
//		{
//			x = first.compiMove();
//			first.setFieldArray(x, 1);
//			x = first.compiMove();
//			first.setFieldArray(x, 2);
//		}
//		System.out.println("current field, rating=" + first.calc(Field.));
//		System.out.println(first.toString());
//		Field[] next = first.nextMoves(2);
//		for (int i = 0; i < next.length; i++) {
//			Field field = next[i];
//			System.out.println("move " + Integer.toString(i+1) + ", rating=" + field.calc());
//			System.out.println(field.toString());
//		}
//	}
}
	
		

