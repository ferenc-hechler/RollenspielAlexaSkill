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

/**
 * https://en.wikipedia.org/wiki/Linear_congruential_generator#Sample_Python_Code
 * 
 * Source: Java's java.util.Random
 * m (multiplier): 2**48 	
 * a (increment): 25214903917 (0x5DEECE66D)
 * c: 11 	
 * output bits of seed in rand(): bits 47...16
 * 
 * recursion: x(n+1) = a*x(n)+c mod m
 * 
 * copied from java sources to create junit tests with assured random numbers.
 * 
 * @author feri
 */
public class PRNG extends Random {
    /** the svuid. */ private static final long serialVersionUID = -2621127706758733816L;

	private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

	private long x;
	

	public PRNG() {
		this(System.currentTimeMillis());
	}
	
	public PRNG(long seed) {
		setSeed(seed);
	}

	public void setSeed(long seed) {
		x = (seed ^ multiplier) & mask;
	}
	
	
	public int nextInt() {
		x = (x * multiplier + addend) & mask;
		int result = (int) ((x>>>16) & 0xFFFFFFFF);
		return result;
	}

	public int nextInt(int bound) {
		return (nextInt() & 0x7FFFFFFF) % bound;
	}

	
	public static void main(String[] args) {
		Random rand = new Random(0);
		PRNG prng = new PRNG(0);
		for (int i=0; i<10; i++) {
			System.out.println(i+":"+Integer.toHexString(rand.nextInt()));
			System.out.println(i+":"+Integer.toHexString(prng.nextInt()));
		}
	}


}


