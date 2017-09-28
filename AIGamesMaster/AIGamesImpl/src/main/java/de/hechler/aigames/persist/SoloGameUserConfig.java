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
package de.hechler.aigames.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hechler.soloroleplay.util.TextUtil;

public class SoloGameUserConfig {

	public static enum UserConfigFlag {
		
		DO_NOT_WAIT_FOR_ANSWER("Diskutiermodus"),
		SHOW_PREPARED_ADVENTURES("Testspielanzeige"),
		SHORTEN_TEXTS("Schnelldurchlauf"),
		ALEXA_ROLLS_DICES("Alexawürfel"),
		USE_DEVELOP_REST_SERVICE("Entwicklerservice");
		
		private static final Map<String, UserConfigFlag> flagName2EnumMap = new HashMap<String, UserConfigFlag>();
	    static {
	        for (UserConfigFlag flag : UserConfigFlag.values()) {
	            flagName2EnumMap.put(TextUtil.normalizeForCompare(flag.flagName), flag);
	        }
	    }
	    
		public final String flagName;
		
		UserConfigFlag(String flagName) {
			this.flagName = flagName;
		}

		public static UserConfigFlag fromFlagName(String flagName) {
			String flagNorm = TextUtil.normalizeForCompare(flagName);
			return flagName2EnumMap.get(flagNorm);
		}
	}

	private Set<UserConfigFlag> activeFlags;
	
	public SoloGameUserConfig() {
		activeFlags = new HashSet<UserConfigFlag>(); 
	}
	
	public UserConfigFlag activateFlag(String role, String flagName) {
		return activateFlag(role, UserConfigFlag.fromFlagName(flagName));
	}
	
	public UserConfigFlag deactivateFlag(String flagName) {
		return deactivateFlag(UserConfigFlag.fromFlagName(flagName));
	}

	public UserConfigFlag activateFlag(String role, UserConfigFlag flag) {
		if (flag != null) {
			if (flag == UserConfigFlag.USE_DEVELOP_REST_SERVICE) {
				if (!("SYSTEM".equals(role) || "DEV".equals(role))) {
					return null;
				}
			}
			activeFlags.add(flag);
		}
		return flag;
	}
	
	public UserConfigFlag deactivateFlag(UserConfigFlag flag) {
		if (flag != null) {
			activeFlags.remove(flag);
		}
		return flag;
	}
	
	public boolean hasFlags() {
		return !activeFlags.isEmpty();
	}
	
	public boolean isFlagActive(UserConfigFlag flag) {
		return activeFlags.contains(flag);
	}
	
	public List<String> getActiveFlags() {
		if (!hasFlags()) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (UserConfigFlag activeFlag:activeFlags) {
			result.add(activeFlag.name());
		}
		Collections.sort(result);
		return result;
	}

	public void setActiveFlags(String role, List<String> activeFlagsList) {
		activeFlags.clear();
		if (activeFlagsList != null) {
			for (String activeFlag:activeFlagsList) {
				activateFlag(role, UserConfigFlag.valueOf(activeFlag));
			}
		}
	}
}
