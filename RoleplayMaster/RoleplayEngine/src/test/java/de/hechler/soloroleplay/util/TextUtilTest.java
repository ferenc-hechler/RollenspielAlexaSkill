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
package de.hechler.soloroleplay.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextUtilTest {

	@Test
	public void testUmlaut() {
		String hoehle_lowerCase = "h"+TextUtil.UML_oe+"hle";
		String hoehle_upCase = "H"+TextUtil.UML_OE+"HLE";
		String normAnswer = TextUtil.normalizeAnswer(hoehle_lowerCase);
		assertEquals(hoehle_upCase, normAnswer);
		String normComp = TextUtil.normalizeForCompare(hoehle_lowerCase);
		assertEquals(hoehle_upCase, normComp);
	}

	@Test
	public void testPos2Line() {
		String text = 
				"abcd\n" + 
				"efgh\n" +
				"\n" +
				"ijkl\n";
		assertEquals(-1, TextUtil.pos2line(text, -1));
		assertEquals(1, TextUtil.pos2line(text, 0));
		assertEquals(1, TextUtil.pos2line(text, 3));
		assertEquals(1, TextUtil.pos2line(text, 4));
		assertEquals(2, TextUtil.pos2line(text, 5));
		assertEquals(2, TextUtil.pos2line(text, 9));
		assertEquals(3, TextUtil.pos2line(text, 10));
		assertEquals(4, TextUtil.pos2line(text, 11));
		assertEquals(4, TextUtil.pos2line(text, 15));
		assertEquals(-1, TextUtil.pos2line(text, 16));
	}
	
	@Test
	public void testLine2Pos() {
		String text = 
				"abcd\n" + 
				"efgh\n" +
				"\n" +
				"ijkl\n";
		assertEquals(-1, TextUtil.line2pos(text, 0));
		assertEquals(0, TextUtil.line2pos(text, 1));
		assertEquals(5, TextUtil.line2pos(text, 2));
		assertEquals(10, TextUtil.line2pos(text, 3));
		assertEquals(11, TextUtil.line2pos(text, 4));
		assertEquals(-1, TextUtil.line2pos(text, 5));
	}
	
}
