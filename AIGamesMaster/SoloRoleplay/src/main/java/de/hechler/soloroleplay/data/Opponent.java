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
import java.util.List;

import de.hechler.soloroleplay.util.TextUtil;

public class Opponent {

	public enum OpponentType {
		FRIEND, FOE
	}
	
	private RestrictedParameterMap parameters = new RestrictedParameterMap(new String[]
			{"WAFFE", "AT", "PA", "TP", "DK", "LEP", "AP", "RS", "AUP", "GS", "MR", "MU", "KK", "GE", "CH", "IN", "WS", "KO"}
	);

	private List<String> additions = new ArrayList<String>();
	
	private OpponentType type;
	private String name;
	private String gegnerText;
	
	public Opponent(OpponentType type, String name, String gegnerText) {
		this.type = type;
		this.name = name;
		this.gegnerText = gegnerText;
	}

	public String getName() {
		return name;
	}
	
	public OpponentType getType() {
		return type;
	}

	public String getTypeName() {
		return type == OpponentType.FRIEND ? "Freund" : "Gegner";
	}

	public boolean knowsParameter(String name) {
		return parameters.isAllowed(name);
	}
	
	public void addParameter(String name, String value) {
		parameters.addParameter(name, value);
	}

	public String getParameter(String paramName) {
		return parameters.getValue(paramName);
	}

	public void addAddition(String addition) {
		additions.add(addition);
	}



	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getTypeName().toUpperCase()).append(": ").append(name).append(TextUtil.endl);
		if (gegnerText != null) {
			result.append("TEXT: ").append(gegnerText).append(TextUtil.endl);
		}
		result.append(parameters);
		for (String addition:additions) {
			result.append("ZUSATZ: ").append(addition).append(TextUtil.endl);
		}
		return result.toString();
	}

	public void describe(Response response) {
		if (gegnerText != null) {
			response.addDescription(gegnerText);
		}
		else {
			response.addDescription(getTypeName()).addDescription(getName()+":");
			describeParameter(response, "MU", "Mut"); 
			describeParameter(response, "WAFFE", "Waffe"); 
			describeParameter(response, "DK", "Distanzklasse"); 
			describeParameter(response, "TP", "Trefferpunkte"); 
			describeParameter(response, "AT", "Attacke"); 
			describeParameter(response, "PA", "Parade"); 
			describeParameter(response, "RS", "R"+TextUtil.UML_ue+"stungsschutz"); 
			describeParameter(response, "LEP", "Lebenspunkte"); 
			describeParameter(response, "AP", "Astralpunkte"); 
			describeParameter(response, "AUP", "Ausdauerpunkte"); 
			describeParameter(response, "GS", "Geschwindigkeit");
			describeParameter(response, "MR", "Magieresistenz");
			describeParameter(response, "KK", "K"+TextUtil.UML_oe+"rperkraft");
			describeParameter(response, "GE", "Geschicklichkeit");
			describeParameter(response, "CH", "Charisma");
			describeParameter(response, "IN", "Intelligenz");
			describeParameter(response, "WS", "Weisheit");
			describeParameter(response, "KO", "Konstitution");
			for (String addition:additions) {
				response.addDescription(";").addDescription(TextUtil.handleMinus(addition));
			}
		}
		response.addDescription(";");
		
	}

	private void describeParameter(Response response, String paramName, String plaintextName) {
		String value = getParameter(paramName);
		if ((value != null) && !value.trim().isEmpty()) {
			response.addDescription(";").addDescription(plaintextName).addDescription(TextUtil.handleMinus(value));
		}
	}

}
