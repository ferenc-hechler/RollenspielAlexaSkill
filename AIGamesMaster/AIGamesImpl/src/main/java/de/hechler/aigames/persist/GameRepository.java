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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.hechler.aigames.UserPersistDAO;
import de.hechler.aigames.UserPersistDAO.UserInfo;
import de.hechler.aigames.soloroleplay.SoloGame;
import de.hechler.utils.RandUtils;

public class GameRepository {

	public static Random testRandom;
	
	private static final int TIMEOUT_CHECK_INTERVAL_MS = 5000;
	private static final int AUTOSAVE_INTERVAL_MS = 10*1000;
	private static final int TIMEOUT_4H = 4*60*60*1000;

	public static class GameState {
		private String gameId;
		private long creationTimestamp;
		private long lastUpdate;
		private int version;
		private int persistedVersion;
		private String userId;
		private String role;
		private SoloGame game;
		private long timeoutMillis;
		private boolean active;
		public GameState(String gameId, SoloGame game, long timeoutMillis) {
			this.gameId = gameId;
			this.game = game;
			this.timeoutMillis = timeoutMillis;
			this.creationTimestamp = System.currentTimeMillis();
			this.lastUpdate = this.creationTimestamp;
			this.version = 0;
			this.persistedVersion = 0;
			this.active = false;
		}
		public String getGameId() {
			return gameId;
		}
		public SoloGame getGame() {
			return game;
		}
		public long getCreationTimestamp() {
			return creationTimestamp;
		}
		public long getLastUpdate() {
			return lastUpdate;
		}
		public long getTimeoutMillis() {
			return timeoutMillis;
		}
		public int getVersion() {
			return version;
		}
		public void update() { 
			lastUpdate = System.currentTimeMillis(); 
			version += 1; 
		}
		public boolean isExpired(long now) {
			boolean expired = now > lastUpdate + timeoutMillis;
			if (!expired && !isActive()) {
				expired = now > creationTimestamp + 60*1000;
			}
			return expired;
		}
		public boolean isAutosaveNeeded(long now) {
			if (version == persistedVersion) {
				return false;
			}
			if (now < lastUpdate + AUTOSAVE_INTERVAL_MS) {
				return false;
			}
			return true;
		}
		public void activate() {
			this.active = true;
			update();
		}
		public boolean isActive() {
			return active;
		}
		public void deactivate() {
			update();
			this.active = false;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUserId() {
			return userId;
		}
		public String getRole() {
			return role;
		}
		@Override public String toString() { return "GS["+getGameId()+"|"+getRole()+"]"; }
	
		public boolean isPersisted() {
			return version == persistedVersion;
		}
		public void ignoreTransientChanges() {
			persistedVersion = version;
		}
		
		public PersistentDataGameState getPersistentData() {
			PersistentDataGameState result = new PersistentDataGameState();
			result.setUserId(userId);
			result.setCreationTimestamp(creationTimestamp);
			result.setLastUpdate(lastUpdate);
			result.setVersion(version);
			result.setPersistentGameData(game.getPersistentGameData());
			return result;
		}
		
		public void restoreFromPersistentData(PersistentDataGameState persistentData) {
			this.userId = persistentData.getUserId();
			this.role = persistentData.getRole();
			this.creationTimestamp = persistentData.getCreationTimestamp();
			this.lastUpdate = System.currentTimeMillis();
			this.version = persistentData.getVersion();
			this.persistedVersion = this.version;
			this.game.restoreFromPersistentData(persistentData.getPersistentGameData());
		}

		
		
	}

	private Map<String, GameState> gameId2gameMap = new HashMap<String, GameState>();
	private Map<String, String> userId2gameIdMap = new HashMap<String, String>();
	
	private UserPersistDAO userPersistDAO;
	private Gson gson;
	
	
	public GameRepository() {
	}

	private UserPersistDAO getUserPersistDAO() {
		if (userPersistDAO == null) {
			userPersistDAO = DAOFactory.createUserPersistDAO();
		}
		return userPersistDAO;
	}
	
	private Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().create();
		}
		return gson;
	}

	public synchronized GameState getGameStateByGameIdNoFallback(String gameId) {
		checkTimeout();
		return gameId2gameMap.get(gameId);
	}

	public synchronized GameState getGameStateByGameId(String gameId) {
		GameState result = getGameStateByGameIdNoFallback(gameId);
		if (result == null) {
			result = getGamestateByPersistedGameId(gameId);
		}
		return result;
	}

	private GameState getGamestateByPersistedGameId(String gameId) {
		// HIERWEITER
//		GameState result;
//		String userId = "feri";
//		result = getGameStateByUserId(userId);
//		if (result == null) {
//			result = getPersistedGameState(userId, PersistentDataSoloGame.class);
//			if (result != null) {
//				result.activate();
//				changeGameId(result, gameId);
//			}
//		}
//		return result;
		return null;
	}

	public synchronized void forceSave(GameState gameState) {
		persistGame(gameState);
	}

	public synchronized void connectUser(String gameId, String userId) {
		if ((userId == null) || userId.isEmpty()) {
			return;
		}
		GameState gameState = getGameStateByGameIdNoFallback(gameId);
		if (gameState == null) {
			return;
		}
		gameState.setUserId(userId);
		userId2gameIdMap.put(userId, gameId);
	}

	public synchronized String getGameIdByUserId(String userId) {
		if ((userId == null) || userId.isEmpty()) {
			return null;
		}
		String gameId = userId2gameIdMap.get(userId);
		return gameId;
	}
	
	public synchronized GameState getGameStateByUserId(String userId) {
		if ((userId == null) || userId.isEmpty()) {
			return null;
		}
		String gameId = userId2gameIdMap.get(userId);
		if (gameId == null) {
			return null;
		}
		GameState gameState = getGameStateByGameId(gameId);
		if (gameState == null) {
			return null;
		}
		if (!userId.equals(gameState.getUserId())) {
			gameState = null;
		}
		if (gameState == null) {
			userId2gameIdMap.remove(userId);
		}
		return gameState;
	}

	public GameState getPersistedGameState(String userId) {
		GameState result = null;
		UserInfo userInfo = getUserPersistDAO().getUserInfo(userId);
		if (userInfo != null) {
			String jsonPersistentDataGameState = userInfo.getData();
			PersistentDataGameState persistentDataGameState = getGson().fromJson(jsonPersistentDataGameState, PersistentDataGameState.class);
			persistentDataGameState.setRole(userInfo.getRole());
			result = createNewGame();
			result.restoreFromPersistentData(persistentDataGameState);
		}
		return result;
	}

//	public GameState getPersistedGameStateOLD(String userId, final Class<? extends IPersistentGameData> pgdClass) {
//		GameState result = null;
//		String jsonPersistentDataGameState = getUserPersistDAO().getData(userId);
//		if (jsonPersistentDataGameState != null) {
//			Gson gson = new GsonBuilder().registerTypeAdapter(IPersistentGameData.class, new InstanceCreator<IPersistentGameData>() {
//				@Override
//				public IPersistentGameData createInstance(Type arg0) {
//					try {
//						return pgdClass.newInstance();
//					} catch (InstantiationException | IllegalAccessException e) {
//						throw new RuntimeException(e);
//					}
//				}
//			}).create();
//			PersistentDataGameState persistentDataGameState = gson.fromJson(jsonPersistentDataGameState, PersistentDataGameState.class);
//			result = createNewGame();
//			result.restoreFromPersistentData(persistentDataGameState);
//		}
//		return result;
//	}

	public synchronized GameState createNewGame() {
		return createNewGame(TIMEOUT_4H);
	}
	public synchronized GameState createNewGame(long timeoutMillis) {
		String gameId = generateGameId();
		SoloGame game;
		try {
			game = new SoloGame();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		GameState gameState = new GameState(gameId, game, timeoutMillis);
		gameId2gameMap.put(gameId, gameState);
		return gameState;
	}

	public synchronized void removeGame(String gameId) {
		GameState gameState = gameId2gameMap.get(gameId);
		if (gameState != null) {
			if (gameState.getUserId() != null) {
				userId2gameIdMap.remove(gameState.getUserId());
			}
			gameId2gameMap.remove(gameId);
			gameState.getGame().close();
		}
	}

	public synchronized String generateGameId() {
		while (true) {
			String gameId = RandUtils.randomLetter(testRandom);
			gameId = gameId + Integer.toString(1+RandUtils.randomInt(testRandom, 2*(gameId2gameMap.size()+4)));
			if (!gameId2gameMap.containsKey(gameId)) {
				return gameId;
			}
		}
	}
	
	public synchronized void close() {
		List<GameState> gameStates = new ArrayList<GameState>(gameId2gameMap.values());
		for (GameState gameState:gameStates) {
			persistGame(gameState);
			removeGame(gameState.gameId);
		}
	}

	private long nextTimeoutCheck = 0;
	
	private void checkTimeout() {
		if (System.currentTimeMillis() < nextTimeoutCheck) {
			return;
		}
		timeoutGameStates();
		nextTimeoutCheck = System.currentTimeMillis() + TIMEOUT_CHECK_INTERVAL_MS;
	}

	private synchronized void timeoutGameStates() {
		long now = System.currentTimeMillis();
		List<GameState> gameStates = new ArrayList<GameState>(gameId2gameMap.values());
		for (GameState gameState:gameStates) {
			if (gameState.isExpired(now)) {
				persistGame(gameState);
				removeGame(gameState.gameId);
			}
			else if (gameState.isAutosaveNeeded(now)) {
				persistGame(gameState);
			}
		}
	}

	private void persistGame(GameState gameState) {
		if (gameState.getUserId() != null) {
			if (gameState.persistedVersion != gameState.version) {
				PersistentDataGameState persistentData = gameState.getPersistentData();
				if (persistentData.getPersistentGameData() != null) {
					String jsonPersistData = getGson().toJson(persistentData);
					getUserPersistDAO().saveOrUpdate(gameState.getUserId(), jsonPersistData);
				}
				gameState.persistedVersion = gameState.version;
			}
		}
	}

	public void changeGameId(GameState gameState, String newGameId) {
		String oldGameId = gameState.getGameId();
		gameState.gameId = newGameId;
		gameId2gameMap.remove(oldGameId);
		gameId2gameMap.put(newGameId, gameState);
		String userId = gameState.getUserId();
		if (userId != null) {
			userId2gameIdMap.put(userId, newGameId);
		}
	}



}
