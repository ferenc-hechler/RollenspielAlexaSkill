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
import de.hechler.aigames.ai.IGame;
import de.hechler.aigames.ai.IPersistentGameData;
import de.hechler.aigames.ai.soloroleplay.SoloGameUserConfig.UserConfigFlag;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.move.SoloRoleplayMove;
import de.hechler.soloroleplay.SoloRoleplayGame;
import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloGame implements IGame {

	private final static Logger logger = Logger.getLogger(SoloGame.class.getName());

	public enum Phase {
		INIT, PLAYYESNO, PLAY, FINISHED
	}

	public enum RepeatRange {
		ALL, LAST, RECONNECT
	}
	
	private SoloRoleplayDAO soloDao;
	
	private Phase phase; 

	private List<String> visibleSoloNames;
	private String selectedSoloName;
	private SoloGameUserConfig userConfig;
	private SoloRoleplayGame game;
	
	public SoloGame() {
		this.soloDao = DAOFactory.createSoloRoleplayDAO();
		resetPhase();
	}

	
	private void resetPhase() {
		this.phase = Phase.INIT;
		this.visibleSoloNames = null;
		this.selectedSoloName = null;
		this.game = null;
	}
	
	public GenericResult getDescription(String owner) {
		String result = getDescriptionTextForPhase(owner);
		return new DoMoveResult<SoloRoleplayMove>(ResultCodeEnum.S_OK, new SoloRoleplayMove(result));
	}

	public GenericResult repeat(String owner, RepeatRange range) {
		String result;
		switch (range) {
		case ALL: 
			result = repeatDescriptionTextForPhase(owner);
			break;
		case LAST:
			result = repeatCurrentStepTextForPhase(owner);
			break;
		case RECONNECT:
			if (phase == Phase.INIT) {
				result = repeatDescriptionTextForPhase(owner);
			}
			else {
				result = "";
				if (phase == Phase.PLAY) {
					result = "Sage 'Wiederhole alles' um das letzte Kapitel zu wiederholen. ";
				}
				result += repeatCurrentStepTextForPhase(owner);
			}
			break;
		default:
			throw new RuntimeException("UNKNOWN RepeatRange '"+range+"'");
		}
		return new DoMoveResult<SoloRoleplayMove>(ResultCodeEnum.S_OK, new SoloRoleplayMove(result));
	}


	private String repeatCurrentStepTextForPhase(String owner) {
		int savedRiddleCount = 0;
		if (game != null) {
			savedRiddleCount = game.saveLastSubStepForRepeat();
		}
		String result = getCurrentStepTextForPhase(owner);
		if (game != null) {
			game.resetLastSubStepAfterRepeat(savedRiddleCount);
		}
		return result;
	}


	private String repeatDescriptionTextForPhase(String owner) {
		int savedRiddleCount = 0;
		int savedLastStep = 0;
		if (game != null) {
			savedRiddleCount = game.saveLastSubStepForRepeat();
			savedLastStep = game.saveLastStepForRepeat();
		}
		String result = getDescriptionTextForPhase(owner);
		if (game != null) {
			game.resetLastSubStepAfterRepeat(savedRiddleCount);
			game.resetLastStepAfterRepeat(savedLastStep);
		}
		return result;
	}


	public GenericResult answer(String owner, String answer) {
		String result;
		switch (phase) {
		case INIT:
			initVisibleSoloNames(owner);
			selectedSoloName = parseSoloName(answer);
			if (selectedSoloName != null) {
				SoloRoleplayData soloData = SoloRepository.getInstance().getSolo(selectedSoloName);
				result = "Kurzbeschreibung des Soloabenteuers '"+soloData.handlePhonems(selectedSoloName)+"': ";
				result = soloData.getPhonemDescription();
				result += " M"+TextUtil.UML_oe+"chtest Du dieses Abenteuer spielen? ";
				phase = Phase.PLAYYESNO;
			}
			else if (isListe(answer)) {
					result = textSoloNameList();
			}
			else { 
				result = 
						"Das habe ich leider nicht verstanden. "
					  + "Bitte sage die Nummer oder den Namen des Soloabenteuers, "+TextUtil.UML_ue+"ber das du mehr erfahren m"+TextUtil.UML_oe+"chtest. " 
					  + "Mit dem Kommando 'LISTE' z"+TextUtil.UML_ae+"hle ich dir nochmal die Titel aller aktuell verf"+TextUtil.UML_ue+"gbaren Abenteuer auf. ";
				break;
			}
			break;
		case PLAYYESNO: {
			if (isYes(answer)) {
				game = new SoloRoleplayGame(SoloRepository.getInstance().getSolo(selectedSoloName));
				phase = Phase.PLAY;
				result = getDescriptionTextForPhase(owner);
				break;
			}
			selectedSoloName = null;
			phase = Phase.INIT;
			result = getDescriptionTextForPhase(owner);
			break;
		}
		case PLAY:
			Response response = game.processAnswer(answer, isFlagActive(UserConfigFlag.SHORTEN_TEXTS));
			game.executeFromCurrent(response);
			ResultCodeEnum type = ResultCodeEnum.S_OK;
			if (game.isFinished()) {
				phase = Phase.FINISHED;
				type = ResultCodeEnum.S_PLAYER_WINS; 
			}
			return new DoMoveResult<SoloRoleplayMove>(type, new SoloRoleplayMove(response.getText().toString()));
		case FINISHED:
			restart();
			result = getDescriptionTextForPhase(owner);
			break;
		default:
			throw new RuntimeException("UNKNOWN PHASE "+phase);
		}
		return new DoMoveResult<SoloRoleplayMove>(ResultCodeEnum.S_OK, new SoloRoleplayMove(result));
	}

	
	public GenericResult directJump(String chapter, String stepString) {
		int step = 0;
		if ((stepString != null) && !stepString.trim().isEmpty()) {
			try {
				step = Integer.parseInt(stepString);
			} catch (NumberFormatException e) {
				return GenericResult.genericInvalidParameterResult;
			}
		}
		if ((chapter == null) || (chapter.toString().isEmpty())) {
			return GenericResult.genericInvalidParameterResult;
		}
		try {
			game.directJump(chapter, step);
			return GenericResult.genericOkResult;
		}
		catch (RuntimeException e) {
			return GenericResult.genericInvalidParameterResult;
		}
	}


	
	private String getDescriptionTextForPhase(String owner) {
		String result;
		switch (phase) {
		case INIT:
			readVisibleSolonames(owner);
			result = "Willkommen zum Rollenspiel Skill. ";
			result += textSoloCountInfo();
			result += textSoloNameList();
			result += "Bitte sage die Nummer oder den Namen des Soloabenteuers, "+TextUtil.UML_ue+"ber das du mehr erfahren m"+TextUtil.UML_oe+"chtest. ";
			break;
		case PLAYYESNO: {
			result = SoloRepository.getInstance().getSolo(selectedSoloName).getPhonemDescription();
			result += " M"+TextUtil.UML_oe+"chtest Du dieses Abenteuer spielen? ";
			break;
		}
		case PLAY:
			if (game == null) {
				resetPhase();
				return getDescriptionTextForPhase(owner);
			}
			Response response = game.executeFromLast(isFlagActive(UserConfigFlag.SHORTEN_TEXTS));
			result = response.getText().toString();
			break;
		case FINISHED:
			return "Das Soloabenteuer ist abgeschlossen. ";
		default:
			throw new RuntimeException("UNKNOWN PHASE "+phase);
		}
		return result;
	}

	
	private String getCurrentStepTextForPhase(String owner) {
		String result;
		switch (phase) {
		case INIT:
			readVisibleSolonames(owner);
			result = textSoloNameList();
			break;
		case PLAYYESNO: {
			result = SoloRepository.getInstance().getSolo(selectedSoloName).getPhonemDescription();
			result += " M"+TextUtil.UML_oe+"chtest Du dieses Abenteuer spielen? ";
			break;
		}
		case PLAY:
			if (game == null) {
				resetPhase();
				return getCurrentStepTextForPhase(owner);
			}
			Response response = new Response(game, isFlagActive(UserConfigFlag.SHORTEN_TEXTS));
			game.executeFromCurrent(response);
			result = response.getText().toString();
			break;
		case FINISHED:
			return "Das Soloabenteuer ist abgeschlossen. ";
		default:
			throw new RuntimeException("UNKNOWN PHASE "+phase);
		}
		return result;
	}
	
	
	
	private void readVisibleSolonames(String owner) {
		visibleSoloNames = soloDao.getSoloNamesInStatus("PUBLISHED");
		if (isFlagActive(UserConfigFlag.SHOW_PREPARED_ADVENTURES)) {
			List<String> preparedSoloNames = soloDao.getSoloNamesInStatus("PREPARED");
			for (String preparedSoloName:preparedSoloNames) {
				if (!visibleSoloNames.contains(preparedSoloName)) {
					visibleSoloNames.add(0, preparedSoloName);
				}
			}
		}
		List<String> ownerSoloNames = soloDao.getSoloNamesFromOwner(owner);
		for (String ownerSoloName:ownerSoloNames) {
			if (!visibleSoloNames.contains(ownerSoloName)) {
				visibleSoloNames.add(0, ownerSoloName);
			}
		}
	}


	private String textSoloCountInfo() {
		return "Aktuell kannst du zwischen " + Integer.toString(visibleSoloNames.size()) + " Soloabenteuern ausw"+TextUtil.UML_ae+"hlen. ";
	}

	private String textSoloNameList() {
		StringBuilder result = new StringBuilder();
		for (int n=1; n<=visibleSoloNames.size(); n++) {
			String soloName = visibleSoloNames.get(n-1);
			result.append(Integer.toString(n)).append("; '").append(soloName).append("'; ");
		}
		return result.toString();
	}



	@Override
	public void close() {
		restart();
		soloDao = null;
	}


	public GenericResult restart() {
		resetPhase();
		return GenericResult.genericOkResult;
	}

	public boolean isFinished() {
		return phase == Phase.FINISHED;
	}

	
	
	
	
	
	private boolean isYes(String answer) {
		String comp = TextUtil.normalizeForCompare(answer); 
		switch (comp) {
		case "JA":
		case "OKAY":
		case "OK":
			return true;
		}
		return false;
	}

	private boolean isListe(String answer) {
		String comp = TextUtil.normalizeForCompare(answer); 
		switch (comp) {
		case "LISTE":
			return true;
		}
		return false;
	}


	private String parseSoloName(String selectedSolo) {
		int n = word2Int(selectedSolo);
		if (n != -1) {
			if ((n < 1) || (n > visibleSoloNames.size())) {
				return null;
			}
			return TextUtil.makeReadable(visibleSoloNames.get(n-1));
		}
		String comp = TextUtil.normalizeForCompare(selectedSolo);
		for (int i=0; i<visibleSoloNames.size(); i++) {
			String visibleSoloName = TextUtil.makeReadable(visibleSoloNames.get(i));
			if (comp.equals(TextUtil.normalizeForCompare(visibleSoloName))) {
				return visibleSoloName;
			}
		}
		return null;
	}


	private void initVisibleSoloNames(String owner) {
		if (visibleSoloNames == null) {
			readVisibleSolonames(owner);
		}
	}


	private int word2Int(String answer) {
		int result = -1;
		if (answer.matches("^\\d+$")) {
			return Integer.parseInt(answer);
		}
		String comp = TextUtil.normalizeForCompare(answer);
		switch (comp) {
		case "EINS": 
		case "ERSTE": 
		case "ERSTES": 
		case "ERSTER": 
			result = 1;
			break;
		case "ZWEI": 
		case "ZWO": 
		case "ZWEITE": 
		case "ZWEITES": 
		case "ZWEITER": 
			result = 2;
			break;
		case "DREI": 
		case "DRITTES": 
		case "DRITTER": 
			result = 3;
			break;
		case "VIER": 
		case "VIERTE": 
		case "VIERTES": 
		case "VIERTER": 
			result = 4;
			break;
		case "F"+TextUtil.UML_UE+"NF": 
		case "F"+TextUtil.UML_UE+"NFTE": 
		case "F"+TextUtil.UML_UE+"NFTES": 
		case "F"+TextUtil.UML_UE+"NFTER": 
			result = 5;
			break;
		case "SECHS": 
		case "SECHSTE": 
		case "SECHSTES": 
		case "SECHSTER": 
			result = 6;
			break;
		case "SIEBEN": 
		case "SIEBTE": 
		case "SIEBTES": 
		case "SIEBTER": 
			result = 7;
			break;
		case "ACHT": 
		case "ACHTE": 
		case "ACHTES": 
		case "ACHTER": 
			result = 8;
			break;
		case "NEUN": 
		case "NEUNTE": 
		case "NEUNTES": 
		case "NEUNTER": 
			result = 9;
			break;
		case "ZEHN": 
		case "ZEHNTE": 
		case "ZEHNTES": 
		case "ZEHNTER": 
			result = 10;
			break;
		case "ELF": 
		case "ELFTE": 
		case "ELFTES": 
		case "ELFTER": 
			result = 11;
			break;
		case "ZW"+TextUtil.UML_OE+"LF": 
		case "ZW"+TextUtil.UML_OE+"LFTE": 
		case "ZW"+TextUtil.UML_OE+"LFTES": 
		case "ZW"+TextUtil.UML_OE+"LFTER": 
			result = 12;
			break;
		}
		return result;
	}


	public GenericResult activateFlag(String role, String flagName) {
		if (userConfig == null) {
			userConfig = new SoloGameUserConfig();
		}
		UserConfigFlag flag = userConfig.activateFlag(role, flagName);
		if (flag == null) {
			return GenericResult.genericInvalidParameterResult;
		}
		return new GetGameParameterResult(ResultCodeEnum.S_OK, flag.name());
	}

	public GenericResult deactivateFlag(String flagName) {
		if (userConfig == null) {
			userConfig = new SoloGameUserConfig();
		}
		UserConfigFlag flag = userConfig.deactivateFlag(flagName);
		if (flag == null) {
			return GenericResult.genericInvalidParameterResult;
		}
		return new GetGameParameterResult(ResultCodeEnum.S_OK, flag.name());
	}

	public boolean isFlagActive(UserConfigFlag flag) {
		if (userConfig == null) {
			return false;
		}
		return userConfig.isFlagActive(flag);
	}


	public List<String> getActivateFlags() {
		if (userConfig == null) {
			return null;
		}
		return userConfig.getActiveFlags();
	}



	@Override
	public PersistentDataSoloGame getPersistentGameData() {
		PersistentDataSoloGame result = new PersistentDataSoloGame();
		result.setPhase(phase.name());
		result.setSoloName(selectedSoloName);
		if ((userConfig != null)) {
			result.setActiveFlags(userConfig.getActiveFlags());
		}
		if (game != null) {
			result.setGameData(game.getPersistentData());
		}
		return result; 
	}
	
	@Override
	public void restoreFromPersistentData(IPersistentGameData persistentDataObj) {
		try {
			PersistentDataSoloGame persistentData = (PersistentDataSoloGame) persistentDataObj;
			this.phase = Phase.valueOf(persistentData.getPhase());
			this.selectedSoloName = persistentData.getSoloName();
			if (persistentData.getActiveFlags() == null) {
				this.userConfig = null;
			}
			else {
				this.userConfig = new SoloGameUserConfig();
				this.userConfig.setActiveFlags("SYSTEM", persistentData.getActiveFlags());
			}
			if (persistentData.getGameData() == null) {
				game = null;
			}
			else {
				SoloRoleplayData soloData = SoloRepository.getInstance().getSolo(selectedSoloName);
				if (soloData == null) {
					game = null;
				}
				else {
					game = new SoloRoleplayGame(soloData); 
					game.restoreFromPersistentData(persistentData.getGameData());
				}
			}
		}
		catch (RuntimeException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			resetPhase();
		}
	}


	
}
