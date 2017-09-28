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
package de.hechler.aigames.ai.soloroleplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloRepository {

	private static SoloRepository instance;
	private static SoloRepository nextInstance;
	public static SoloRepository getInstance() {
		if (instance == null) {
			instance = new SoloRepository();
		}
		return instance;
	}
	public static synchronized SoloRepository getNextInstance() {
		if (nextInstance == null) {
			nextInstance = new SoloRepository();
		}
		return nextInstance;
	}
	public static synchronized void switchToNextInstance() {
		if (nextInstance != null) {
			instance = nextInstance;
			nextInstance = null;
		}
	}

	private List<SoloRoleplayData> solos;
	private Map<String, Integer> name2soloMap;

	private SoloRepository() {
		this.solos = new ArrayList<>();
		this.name2soloMap = new HashMap<>();
	}

	public synchronized boolean addSolo(SoloRoleplayData solo) {
		String name = TextUtil.makeReadable(solo.getShortName());
		if (name2soloMap.containsKey(name)) {
			return false;
		}
		name2soloMap.put(name, solos.size());
		solos.add(solo);
		return true;
	}
	
	public synchronized SoloRoleplayData getSolo(String name) {
		Integer index = name2soloMap.get(name);
		if (index == null) {
			return null;
		}
		return solos.get(index);
	}
	
	public synchronized List<String> getSoloNames() {
		List<String> result = new ArrayList<>();
		for (SoloRoleplayData solo:solos) {
			result.add(solo.getTitle());
		}
		return result;
	}

	
}
