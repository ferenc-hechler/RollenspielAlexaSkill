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

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;

import de.hechler.utils.RandUtils;

public class BattleshipFieldTest {

	@Test
	public void testBattleshipField10() {
		RandUtils.setPRNG(1);
		BattleshipsField field = new BattleshipsField(2, 10);
		Gson gson = new Gson();
		String actual = gson.toJson(field.ownField);
		assertEquals("["
				+ "[1,2,2,2,1,2,1,2,2,2],"
				+ "[1,1,1,1,1,2,1,1,1,1],"
				+ "[1,1,1,1,1,2,1,2,1,2],"
				+ "[2,1,2,2,1,2,1,2,1,2],"
				+ "[2,1,1,1,1,1,1,1,1,2],"
				+ "[2,1,1,1,1,1,1,1,1,1],"
				+ "[2,1,1,1,1,1,1,1,1,1],"
				+ "[2,1,1,1,2,1,2,2,1,1],"
				+ "[1,1,1,1,2,1,1,1,1,1],"
				+ "[1,1,1,1,1,1,2,2,2,2]]", actual);

		for (int i=0; i<100; i++) {
			field = new BattleshipsField(2, 10);
		}
		field = new BattleshipsField(2, 10);
		actual = gson.toJson(field.ownField);
		assertEquals("["
				+ "[1,1,1,2,2,2,2,1,1,2],"
				+ "[2,1,1,1,1,1,1,1,1,2],"
				+ "[2,1,1,2,2,1,1,2,1,2],"
				+ "[2,1,1,1,1,1,1,2,1,2],"
				+ "[1,1,2,1,1,1,1,1,1,2],"
				+ "[2,1,2,1,1,1,1,1,1,1],"
				+ "[2,1,1,1,2,2,2,1,2,1],"
				+ "[2,1,1,1,1,1,1,1,2,1],"
				+ "[2,1,1,1,1,1,1,1,2,1],"
				+ "[1,1,1,1,2,2,1,1,1,1]]", actual);

		field = new BattleshipsField(2, 10, new int[]{1,0,0,2});
		actual = gson.toJson(field.ownField);
		assertEquals("["
				+ "[1,1,1,1,1,1,1,1,1,1],"
				+ "[1,1,1,1,1,1,1,1,1,1],"
				+ "[1,1,1,1,1,1,1,1,1,1],"
				+ "[1,1,1,1,1,1,1,1,1,1],"
				+ "[1,1,1,1,1,1,1,1,1,1],"
				+ "[1,1,1,2,1,1,1,1,2,1],"
				+ "[1,1,1,1,1,1,1,1,2,1],"
				+ "[1,1,1,1,1,1,1,1,2,1],"
				+ "[1,2,2,2,2,1,1,1,2,1],"
				+ "[1,1,1,1,1,1,1,1,1,1]]", actual);
	}

	@Test
	public void testBattleshipField4() {
		RandUtils.setPRNG(1);
		BattleshipsField field = new BattleshipsField(2, 4);
		Gson gson = new Gson();
		String actual = gson.toJson(field.ownField);
		assertEquals("["
				+ "[1,1,2,2],"
				+ "[1,1,1,1],"
				+ "[1,1,1,2],"
				+ "[1,1,1,2]]", actual);

		field = new BattleshipsField(2, 4, new int[]{0,0,2});
		actual = gson.toJson(field.ownField);
		assertEquals("["
				+ "[1,1,1,2],"
				+ "[1,2,1,2],"
				+ "[1,2,1,2],"
				+ "[1,2,1,1]]", actual);
	}

	@Test
	public void testBattleshipFields() {
		RandUtils.setPRNG(1);
		for (int size=1; size<=10; size++) {
			for (int i=0; i<100; i++) {
				new BattleshipsField(2, size);
			}
		}
	}
	
}
