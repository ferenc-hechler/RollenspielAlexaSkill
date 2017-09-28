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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hechler.aigames.SoloRoleplayDAO;
import de.hechler.aigames.ai.DAOFactory;
import de.hechler.aigames.ai.GameRepository;
import de.hechler.aigames.ai.GameRepository.GameState;
import de.hechler.aigames.ai.soloroleplay.SoloGame.RepeatRange;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.SoloConnectResult;
import de.hechler.soloroleplay.SoloRoleplayGame;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.ValidationException;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;
import de.hechler.utils.TempIDGenerator;
import de.hechler.utils.TempIDGenerator.TempIDData;


public class SoloRoleplayImpl {

	private final static Logger logger = Logger.getLogger(SoloRoleplayImpl.class.getName());

	private static GameRepository<SoloGame> gameRepository = new GameRepository<SoloGame>(SoloGame.class);

	public SoloConnectResult connect(String userId) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByUserId(userId);
		boolean gsPersisted = (gameState == null) ? true : gameState.isPersisted();
		if (gameState == null) {
			gameState = gameRepository.getPersistedGameState(userId, PersistentDataSoloGame.class);
			if (gameState != null) {
				gameState.activate();
				gameRepository.connectUser(gameState.getGameId(), userId);
			}
		}
		if (gameState == null) {
			gameState = gameRepository.createNewGame();
			gameState.activate();
			gameRepository.connectUser(gameState.getGameId(), userId);
		}
		if (gameState.getGame().isFinished()) {
			gameState.getGame().restart();
		}
		gameState.update();
		if (gsPersisted) {
			gameState.ignoreTransientChanges();
		}
		return new SoloConnectResult(ResultCodeEnum.S_OK, gameState.getGameId(), gameState.getGame().getActivateFlags());
	}

	public SoloConnectResult forceConnect(String userId) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByUserId(userId);
		if (gameState != null) {
			gameRepository.removeGame(gameState.getGameId());
		}
		gameState = gameRepository.getPersistedGameState(userId, PersistentDataSoloGame.class);
		if (gameState != null) {
			gameState.activate();
			gameRepository.connectUser(gameState.getGameId(), userId);
		}
		if (gameState == null) {
			gameState = gameRepository.createNewGame();
			gameState.activate();
			gameRepository.connectUser(gameState.getGameId(), userId);
		}
		if (gameState.getGame().isFinished()) {
			gameState.getGame().restart();
		}
		gameState.update();
		gameState.ignoreTransientChanges();
		return new SoloConnectResult(ResultCodeEnum.S_OK, gameState.getGameId(), gameState.getGame().getActivateFlags());
	}

	public GenericResult getDescription(String gameId) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().getDescription(gameState.getUserId());
		gameState.update();
		return result;
	}
	
	public GenericResult repeat(String gameId, RepeatRange range) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().repeat(gameState.getUserId(), range);
		gameState.update();
		return result;
	}
	
	public GenericResult answer(String gameId, String text) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().answer(gameState.getUserId(), text);
		gameState.update();
		return result;
	}

	public GenericResult activateFlag(String gameId, String flagName) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().activateFlag(gameState.getRole(), flagName);
		gameState.update();
		gameRepository.forceSave(gameState);
		return result;
	}

	public GenericResult deactivateFlag(String gameId, String flagName) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().deactivateFlag(flagName);
		gameState.update();
		gameRepository.forceSave(gameState);
		return result;
	}

	public GenericResult directJump(String gameId, String chapter, String step) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().directJump(chapter, step);
		gameState.update();
		return result;
	}

	public GenericResult restart(String gameId) {
		GameState<SoloGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		GenericResult result = gameState.getGame().restart();
		gameState.update();
		return result;
	}

	public void startup() {
		SoloRepository soloRepo = SoloRepository.getInstance();
		readAllSolosFromDB(soloRepo);
	}

	private void readAllSolosFromDB(SoloRepository soloRepo) {
		SoloRoleplayParser parser = new SoloRoleplayParser();
		
		SoloRoleplayDAO soloDao = DAOFactory.createSoloRoleplayDAO();
		if (soloDao.isActive()) {
			List<String> allSoloNames = soloDao.getAllSoloNames();
			for (String soloName:allSoloNames) {
				String soloText = soloDao.getSoloTextWithName(soloName);
				try {
					SoloRoleplayData soloData = parser.parse(soloText);
					soloData.validate();
					soloData.setShortName(soloName);
					soloRepo.addSolo(soloData);
					logger.log(Level.INFO, "successfully parsed solo adventure '"+soloName+"': size "+soloText.length()+" Bytes.");
				}
				catch (ValidationException e) {
					logger.log(Level.SEVERE, "error validating solo adventure '"+soloName+"': "+e.getMessage());
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "error parsing solo adventure '"+soloName+"': "+e.toString(), e);
				}
			}
		}
	}
	
	public static String validate(String soloText) {
		SoloRoleplayParser parser = new SoloRoleplayParser();
		try {
			SoloRoleplayData soloData = parser.parse(soloText);
			soloData.validate();
			String msg = "Das Soloabenteuer '" + soloData.getTitle() + "' von "+soloData.getAuthor()+" wurde erfolgreich validiert."; 
			logger.log(Level.INFO, msg);
			return msg;
		}
		catch (Exception e) {
			String msg = "Fehler beim Validieren: "+e.getMessage(); 
			logger.log(Level.INFO, msg);
			return msg;
		}
	}

	public static String[] audioValidate(String audioSoloText) {
		try {
			String soloText = processAudio(audioSoloText);
			String validationResult = validate(soloText);
			return new String[]{validationResult, soloText};
		}
		catch (Exception e) {
			String msg = "Fehler beim Audio-Validieren: "+e.toString(); 
			logger.log(Level.INFO, msg);
			return new String[]{msg, ""};
		}
	}

	private static String processAudio(String audioSoloText) {
		AudioProcessor audioProcessor = new AudioProcessor();
		String result = audioProcessor.processAudio(audioSoloText);
		return result;
	}

	public static SoloRoleplayGame createGame(String soloText) {
		SoloRoleplayParser parser = new SoloRoleplayParser();
		try {
			SoloRoleplayData soloData = parser.parse(soloText);
			soloData.validate();
			return new SoloRoleplayGame(soloData);
		}
		catch (ValidationException e) {
			throw new RuntimeException("VALIDIERUNGSFEHLER: "+e.getMessage());
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR parsing solo: "+e.toString(), e);
		}
	}

	
	public static SoloRoleplayGame startGame(String soloName) {
		try {
			SoloRoleplayData soloData = SoloRepository.getInstance().getSolo(soloName);
			return new SoloRoleplayGame(soloData);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR getting solo with name '"+soloName+"': "+e.toString());
		}
	}

	
	private static TempIDGenerator<String> roleplayTextStore = new TempIDGenerator<>();

	public static String upload(String soloText) {
		TempIDData<String> idData = roleplayTextStore.createNewTempId(5*60*1000);
		idData.setData(soloText);
		return idData.getId();
	}
	
	public GenericResult importUpload(String userId, String uploadId) {
		try {
			TempIDData<String> idData = roleplayTextStore.getTempId(uploadId);
			if (idData == null) {
				return GenericResult.genericUnknownGameId;
			}
			String soloText = idData.getData();
			SoloRoleplayDAO soloDao = DAOFactory.createSoloRoleplayDAO();
			if (!soloDao.isActive()) {
				return GenericResult.genericUnknownErrorResult;
			}
			SoloRoleplayParser parser = new SoloRoleplayParser();
			SoloRoleplayData soloData = parser.parse(soloText);
			soloData.validate();
			soloDao.deleteSoloFromOwnerInStatus(userId, "INWORK");
			String soloName = soloData.handlePhonems(soloData.getTitle());
			if ((soloName == null) || soloName.trim().isEmpty()) {
				soloName = "Unbenanntes Soloabenteuer";
			}
			if (soloDao.getSoloTextWithName(soloName) != null) {
				int cnt = 2;
				while (soloDao.getSoloTextWithName(soloName+" "+cnt) != null) {
					cnt += 1;
				}
				soloName = soloName + " " + cnt;
			}
			soloDao.insertSoloText(soloName, "INWORK", userId, soloText);
			SoloRepository soloRepo = SoloRepository.getNextInstance();
			readAllSolosFromDB(soloRepo);
			SoloRepository.switchToNextInstance();
			roleplayTextStore.releaseTempId(uploadId);
			return new GetGameParameterResult(ResultCodeEnum.S_OK, soloName);
		} catch (ValidationException e) {
			return new GetGameParameterResult(ResultCodeEnum.E_INVALID_PARAMETER, e.getMessage());
		}
	}

	public void shutdown() {
		gameRepository.close();
		roleplayTextStore.reset();
		DAOFactory.shutdown();
	}
	
	public static List<String> getPublishedSoloNames() { 
		SoloRoleplayDAO soloDao = DAOFactory.createSoloRoleplayDAO();
		List<String> result = soloDao.getSoloNamesInStatus("PUBLISHED");
		return result;
	}


}
