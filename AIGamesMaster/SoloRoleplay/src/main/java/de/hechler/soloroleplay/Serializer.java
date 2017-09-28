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

import java.util.List;

public class Serializer {

	private static final String FIELDSEPERATOR = "|";
	private static final String ENTRYSEPERATOR = ",";
	
	private StringBuilder buffer;
	
	public Serializer() {
		this.buffer = new StringBuilder();
	}

	public Serializer addString(String s) {
		if (buffer.length() > 0) {
			buffer.append(FIELDSEPERATOR);
		}
		if (s != null) {
			buffer.append(s);
		}
		return this;
	}
	
	public Serializer addInt(int n) {
		addString(Integer.toString(n));
		return this;
	}

	public Serializer addLong(long l) {
		addString(Long.toString(l));
		return this;
	}

	public Serializer addBoolean(boolean b) {
		addString(Boolean.toString(b));
		return this;
	}

	
	public Serializer addStringList(List<String> list) {
		String seperator = (buffer.length() == 0) ? "" : FIELDSEPERATOR;
		if (list != null) {
			for (String entry:list) {
				buffer.append(seperator);
				seperator = ENTRYSEPERATOR;
				if (entry != null) {
					buffer.append(entry);
				}
			}			
		}	
		return this;
	}
	
	
	public String getResult() {
		return buffer.toString();
	}

	@Override
	public String toString() {
		return getResult();
	}

}
