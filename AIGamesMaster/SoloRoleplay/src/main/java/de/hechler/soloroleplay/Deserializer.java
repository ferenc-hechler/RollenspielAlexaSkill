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

public class Deserializer {

	private static final String FIELDSEPERATOR = "|";
	private static final String ENTRYSEPERATOR = ",";
	
	private String[] fields;
	private int nextField; 
	
	public Deserializer(String serializedData) {
		this.fields = serializedData.split("\\"+FIELDSEPERATOR);
		this.nextField = 0;
	}

	public String nextString() {
		String result = fields[nextField];
		nextField += 1;
		return result;
	}
	
	public int nextInt() {
		return Integer.parseInt(nextString());
	}
	
	public long nextLong() {
		return Long.parseLong(nextString());
	}
	
	public boolean nextBoolean() {
		return Boolean.parseBoolean(nextString());
	}
	
	public List<String> nextStringList() {
		List<String> result = new ArrayList<String>();
		String[] list = nextString().split("\\"+ENTRYSEPERATOR);
		for (String entry:list) {
			result.add(entry);
		}
		return result;
	}
	
	public boolean hasNext() {
		return nextField < fields.length;
	}
	
}
