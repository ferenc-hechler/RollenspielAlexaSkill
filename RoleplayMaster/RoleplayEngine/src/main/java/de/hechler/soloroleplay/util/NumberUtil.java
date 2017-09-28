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

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {

	public static String num2decStr(double num, int digits) {
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMinimumFractionDigits(digits);
		nf.setMaximumFractionDigits(digits);
        String result = nf.format(num);		
		return result;
	}

	public static String num2str(int num, int minLength) {
		String result = Integer.toString(num);
		int len = result.length();
		if (len < minLength) {
			result = "000000000000".substring(0, minLength-len);
		}
		return result;
	}
	
}
