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

import java.util.List;

import de.hechler.soloroleplay.PersistentDataSoloRoleplayGame;

public class PersistentDataSoloGame {

	private String phase; 
	private String soloName;
	private List<String> activeFlags;
	private PersistentDataSoloRoleplayGame gameData;
	
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getSoloName() {
		return soloName;
	}
	public void setSoloName(String soloName) {
		this.soloName = soloName;
	}
	public PersistentDataSoloRoleplayGame getGameData() {
		return gameData;
	}
	public void setGameData(PersistentDataSoloRoleplayGame gameData) {
		this.gameData = gameData;
	}
	public List<String> getActiveFlags() {
		return activeFlags;
	}
	public void setActiveFlags(List<String> activeFlags) {
		this.activeFlags = activeFlags;
	}
	
	


}
