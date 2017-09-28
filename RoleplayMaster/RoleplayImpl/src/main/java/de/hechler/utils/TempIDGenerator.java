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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempIDGenerator<T> {

	private static final long CLEANUP_INTERVAL_MS = 15000;

	public static class TempIDData<T> {
		private String id;
		private String externalID;
		private long timeout;
		private T data;
		public TempIDData(String id, long timeout) {
			this.id = id;
			this.timeout = (timeout == 0) ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
			this.externalID = null;
			this.data = null;
		}
		public String getId() {
			return id;
		}
		public long getTimeout() {
			return timeout;
		}

		public void setExternalID(String externalID) {
			this.externalID = externalID;
		}
		public String getExternalID() {
			return externalID;
		}
		
		public void setData(T data) {
			this.data = data;
		}
		public T getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return "ID["+getId()+"]";
		}
		
	}

	private long defaultTimeout;
	private Map<String, TempIDData<T>> tempId2dataMap;
	
	public TempIDGenerator() {
		this(0);
	}
	
	public TempIDGenerator(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
		this.tempId2dataMap = new HashMap<>();
	}
	
	public synchronized TempIDData<T> createNewTempId() {
		return createNewTempId(defaultTimeout);
	}

	public synchronized TempIDData<T> createNewTempId(long timeout) {
		while (true) {
			String tempId = RandUtils.randomLetter();
			tempId = tempId + Integer.toString(1+RandUtils.randomInt(2*(tempId2dataMap.size()+4)));
			if (getTempId(tempId) == null) {
				TempIDData<T> result = new TempIDData<>(tempId, timeout);
				tempId2dataMap.put(tempId, result);
				return result;
			}
		}
	}

	public synchronized TempIDData<T> getTempId(String tempId) {
		cleanupTimeouts();
		return tempId2dataMap.get(tempId);
	}

	public synchronized TempIDData<T> getTempIdByExternalId(String externalId) {
		if (externalId == null) {
			return null;
		}
		for (TempIDData<T> data:tempId2dataMap.values()) {
			if (externalId.equals(data.getExternalID())) {
				return data;
			}
		}
		return null;
	}

	private long lastCleanup = 0;
	
	public synchronized void releaseTempId(String tempId) {
		tempId2dataMap.remove(tempId);
	}

	public synchronized void reset() {
		tempId2dataMap.clear();
	}
	
	private synchronized void cleanupTimeouts() {
		long now = System.currentTimeMillis(); 
		if (now - lastCleanup >= CLEANUP_INTERVAL_MS) {
			lastCleanup = now;
			List<String> removeIds = new ArrayList<>();
			for (TempIDData<T> idData : tempId2dataMap.values()) {
				if (idData.getTimeout() < now) {
					removeIds.add(idData.getId());
				}
			}
			for (String removeId : removeIds) {
				tempId2dataMap.remove(removeId);
			}
		}
	}
	
}
