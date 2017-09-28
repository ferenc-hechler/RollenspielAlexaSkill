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
package de.hechler.soloroleplay;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.junit.*;

public class PersistentDataTest {

	@Test
	public void testSerialization() {
		PersistentDataSoloRoleplayGame data = new PersistentDataSoloRoleplayGame();
		data.setChapter("CHAPTER");
		data.setStep(7);
		List<String> history = new ArrayList<String>();
		history.add("CHAP1");
		history.add("CHAP2");
		history.add("CHAP3");
		data.setHistory(history);
		String actual = data.serialize();
		Assert.assertEquals("v2|CHAPTER|7|0|CHAP1,CHAP2,CHAP3", actual);
	}
	
	@Test
	public void testReuseDeserialization() {
		PersistentDataSoloRoleplayGame data = new PersistentDataSoloRoleplayGame();
		data.deserialize("CHAPTER|7|CHAP1,CHAP2,CHAP3");
		Assert.assertEquals("CHAPTER", data.getChapter());
		Assert.assertEquals(7, data.getStep());
		Assert.assertEquals("[CHAP1, CHAP2, CHAP3]", data.getHistory().toString());

		data.deserialize("XX|3|YY");
		Assert.assertEquals("XX", data.getChapter());
		Assert.assertEquals(3, data.getStep());
		Assert.assertEquals("[YY]", data.getHistory().toString());

	}
	
	@Test
	public void testCreateFromSerialized() {
		PersistentDataSoloRoleplayGame data = PersistentDataSoloRoleplayGame.createInstance("CHAPTER|7|CHAP1,CHAP2,CHAP3");
		Assert.assertEquals("CHAPTER", data.getChapter());
		Assert.assertEquals(7, data.getStep());
		Assert.assertEquals("[CHAP1, CHAP2, CHAP3]", data.getHistory().toString());
	}

}
