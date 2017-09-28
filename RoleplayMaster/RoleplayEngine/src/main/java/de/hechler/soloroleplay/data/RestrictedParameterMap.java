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
package de.hechler.soloroleplay.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hechler.soloroleplay.util.TextUtil;

public class RestrictedParameterMap {

	public static class Parameter {
		private String name;
		private String value;
		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
	}
	
	private Set<String> allowedParameterNamesSet;
	private List<Parameter> parameters;
	private Map<String, Integer> name2ParamIndex;
	
	public RestrictedParameterMap(String[] allowedParameterNames) {
		allowedParameterNamesSet = new HashSet<String>();
		for (String allowedParameterName:allowedParameterNames) {
			allowedParameterNamesSet.add(allowedParameterName);
		}
		parameters = new ArrayList<Parameter>();
		name2ParamIndex = new HashMap<String, Integer>();
	}

	public void addParameter(String name, String value) {
		if (!allowedParameterNamesSet.contains(name)) {
			throw new RuntimeException("parameter name '"+name+"' not allowed!");
		}
		if (name2ParamIndex.containsKey(name)) {
			throw new RuntimeException("duplicate parameter name '"+name+"'!");
		}
		name2ParamIndex.put(name, parameters.size());
		parameters.add(new Parameter(name, value));
	}

	public boolean isAllowed(String name) {
		return allowedParameterNamesSet.contains(name);
	}
	
	public String getValue(String name) {
		Integer index = name2ParamIndex.get(name);
		if (index == null) {
			return null;
		}
		return parameters.get(index).getValue();
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (Parameter parameter:parameters) {
			result.append(parameter.getName()).append(": ").append(parameter.value).append(TextUtil.endl);
		}
		return result.toString();
	}

}
