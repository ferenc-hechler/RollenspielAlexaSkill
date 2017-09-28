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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

public class UserInput {

	private Scanner scanner;
	private String[] startLines;
	private int startPos;
	
	private boolean writeToSysOut;
	
	public UserInput(String initialInput, InputStream in) {
		this(initialInput, in, Charset.defaultCharset().name());
	}
	
	public UserInput(String initialInput, InputStream in, String charset) {
		if (initialInput != null) {
			startLines = initialInput.split("[|]");
			startPos = 0;
		}
		else {
			startLines = new String[]{};
			startPos = 0;
		}
		writeToSysOut = (in == System.in);
		scanner = (in == null) ? null : new Scanner(in, charset);
	}
	
	public String nextLine() {
		String result;
		if (startPos < startLines.length) {
			result = startLines[startPos];
			startPos += 1;
			System.out.println(" > "+result);
			System.out.println();
		}
		else {
			if (writeToSysOut) {
				System.out.print(" > ");
				System.out.flush();
			}
			result = scanner.nextLine();
			if (writeToSysOut) {
				System.out.println();
			}
		}
		return result;
	}

	
	public void close() {
		scanner.close();
	}
}
