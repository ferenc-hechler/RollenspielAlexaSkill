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

import java.util.Random;

public class RandUtils {

	private static Random rand = new Random();

	public static void setPRNG(long seed) {
		rand = new PRNG(seed);
	}

	public static Random createPRNG(long seed) {
		return new PRNG(seed);
	}

	
	public static int randomInt(int bound) {
		return rand.nextInt(bound);
	}
	public static int randomInt(Random customRand, int bound) {
		if (customRand == null) {
			return randomInt(bound);
		}
		return customRand.nextInt(bound);
	}
	

	public static String randomLetter() {
		char c = (char) ('A' + rand.nextInt(26));
		return Character.toString(c);
	}
	public static String randomLetter(Random customRand) {
		if (customRand == null) {
			return randomLetter();
		}
		char c = (char) ('A' + customRand.nextInt(26));
		return Character.toString(c);
	}
	
	public static boolean randomBoolean() {
		return rand.nextInt(2) == 0;
	}
	public static boolean randomBoolean(Random customRand) {
		if (customRand == null) {
			return randomBoolean();
		}
		return customRand.nextInt(2) == 0;
	}
	
}
