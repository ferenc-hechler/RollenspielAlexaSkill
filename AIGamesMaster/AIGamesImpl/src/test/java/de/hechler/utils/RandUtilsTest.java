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
package de.hechler.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hechler.utils.RandUtils;

public class RandUtilsTest {

	@Test
	public void testLetters() {
		RandUtils.setPRNG(1);
		String actual = "";
		for (int i=0; i<100; i++) {
			actual += RandUtils.randomLetter();
		}
		assertEquals("LAPSYWPVVWIUCKAIPKLDJEBEIYMJZMGBJEVUNOHZXVKERBUYBHYFWXVLWTYIFZMDCIAWPONDVEZUMRFMIJJPNBYHULVNNVWQODKU", actual);
		RandUtils.setPRNG(2);
		actual = "";
		for (int i=0; i<100; i++) {
			actual += RandUtils.randomLetter();
		}
		assertEquals("NMCRILSYDLGXUCIUHQHKSDYPDTFTOBJACEWWKGZWXPZLMVKGQCUKPHHUYYSNKFTMLPDNFSNDDNFCRNAQYBGYQSSSTKJBIPKQHNPI", actual);
		RandUtils.setPRNG(1);
		actual = "";
		for (int i=0; i<100; i++) {
			actual += RandUtils.randomLetter();
		}
		assertEquals("LAPSYWPVVWIUCKAIPKLDJEBEIYMJZMGBJEVUNOHZXVKERBUYBHYFWXVLWTYIFZMDCIAWPONDVEZUMRFMIJJPNBYHULVNNVWQODKU", actual);
	}

	@Test
	public void testInt() {
		RandUtils.setPRNG(1);
		int actual;
		actual = RandUtils.randomInt(1000);
		assertEquals(323, actual);
		actual = RandUtils.randomInt(1000);
		assertEquals(176, actual);
		actual = RandUtils.randomInt(1000);
		assertEquals(695, actual);
		actual = RandUtils.randomInt(1000);
		assertEquals(626, actual);
	}
	
}
