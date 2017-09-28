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
package de.hechler.aigames.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import de.hechler.aigames.ai.AIGame;
import de.hechler.aigames.ai.GameRepository;
import de.hechler.aigames.ai.GameRepository.GameState;
import de.hechler.aigames.ai.battleships.BattleshipsField;
import de.hechler.aigames.ai.battleships.BattleshipsGame;
import de.hechler.aigames.ai.battleships.BattleshipsImpl;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.NewGameResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.move.BattleshipsMove;
import de.hechler.aigames.api.move.BattleshipsMove.CellContent;
import de.hechler.aigames.api.move.BattleshipsMove.MoveType;
import de.hechler.utils.RandUtils;

//@WebServlet(urlPatterns = "/battlesh", loadOnStartup = 1)
public class BattleshipsRestService extends HttpServlet {
	
	/** the svuid */ private static final long serialVersionUID = 9165763530513604631L;

	private final static Logger logger = Logger.getLogger(BattleshipsRestService.class.getName());

	private static final int DEFAULT_AI_LEVEL = 3;

	private static boolean debugloggingEnabled = Boolean.getBoolean("cfrest.debugging");
	
	public static BattleshipsImpl battleshipsImpl = new BattleshipsImpl();

	public Gson gson;
	
	@Override
	public void init() throws ServletException {
		super.init();
		gson = new Gson();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    doPost(request, response);
	}
	 
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		try {
			String responseString;
			String gameId = normalizeGameId(request.getParameter("gameId"));
			String cmd = request.getParameter("cmd");
			String param1 = request.getParameter("param1");
			String param2 = request.getParameter("param2");
			String param3 = request.getParameter("param3");
			String param4 = request.getParameter("param4");
			
			if (gameId == null) {
				gameId = (String) request.getSession(true).getAttribute("gameId");
				if (gameId == null) {
					NewGameResult newGameResult= battleshipsImpl.createNewGame(DEFAULT_AI_LEVEL, true);
					gameId = newGameResult.gameId;
					request.getSession(true).setAttribute("gameId", gameId);
				}
			}

			switch(cmd) {
			case "enableDebugLogging": {
				responseString = enableDebugLogging(param1);
				break;
			}
			case "initTests": {
				responseString = initTests(param1);
				break;
			}
			case "createSessionlessGame": {
				responseString = createSessionlessGame(param1);
				break;
			}
			case "activateGame": {
				responseString = activateGame(gameId, param1);
				break;
			}
			case "newGame": {
				responseString = newGame(gameId);
				break;
			}
			case "closeGame": {
				responseString = closeGame(gameId);
				break;
			}
			case "clearSession": {
				responseString = clearSession(request.getSession());
				break;
			}
			case "getGameId": {
				responseString = getGameId(request.getSession(true), gameId);
				break;
			}
			case "hasChanges": {
				responseString = hasChanges(gameId, param1);
				break;
			}
			case "setPlayerNames": {
				responseString = setPlayerNames(gameId, param1, param2);
				break;
			}
			case "setAILevel": {
				responseString = setAILevel(gameId, param1);
				break;
			}
			case "doMove": {
				responseString = doMove(gameId, param1, param2, param3, param4);
				break;
			}
			case "doAIMove": {
				responseString = doAIMove(gameId);
				break;
			}
			case "getGameData": {
				responseString = getGameData(gameId);
				break;
			}
			case "getGameDataByUserId": {
				responseString = getGameDataByUserId(param1);
				break;
			}
			case "getGameParameter": {
				responseString = getGameParameter(gameId, param1);
				break;
			}
			case "changeGameParameter": {
				responseString = changeGameParameter(gameId, param1, param2);
				break;
			}
			default: {
				responseString = gson.toJson(GenericResult.genericUnknownCommandResult);
				response.setStatus(500);
				break;
			}
			}

			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType("application/json");
			
			response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			
		    PrintWriter writer = response.getWriter();
		    responseString = encode(responseString); 
	    	writer.println(responseString);
	    	if (debugloggingEnabled) {
	    		if (!(responseString.contains("S_NO_CHANGES") || cmd.equals("getGameId"))) {
	    			logger.info("RQ[cmd="+cmd+",gid="+gameId+",p1="+param1+",p2="+param2+",p3="+param3+",p4="+param4+"] -> "+responseString);
	    		}
	    	}
	    }
		catch (RuntimeException | IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw e;
		}
	}

	
	private String normalizeGameId(String gameId) {
		if (gameId == null) {
			return null;
		}
		return gameId.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]*", "");
	}

	/* ======== */
	/* COMMANDS */
	/* ======== */

	
	private String enableDebugLogging(String value) {
		debugloggingEnabled = Boolean.parseBoolean(value);
		return gson.toJson(GenericResult.genericOkResult);
	}

	private String initTests(String initParams) {
		if (initParams.matches("SEED[(]([0-9]+)[)]")) {
			long seed = Long.parseLong(initParams.replaceFirst("SEED[(]([0-9]+)[)]", "$1"));
			System.out.println("setPRNG("+seed+")");
			battleshipsImpl.shutdown();
			RandUtils.setPRNG(seed);
			BattleshipsField.testRandom = RandUtils.createPRNG(seed);
			BattleshipsGame.testRandom = RandUtils.createPRNG(seed);
			AIGame.testRandom = RandUtils.createPRNG(seed);
			GameRepository.testRandom = RandUtils.createPRNG(seed);
			String rand9999 = Integer.toString(RandUtils.randomInt(10000));
			return gson.toJson(new GetGameParameterResult(ResultCodeEnum.S_OK, rand9999));
		}
		return gson.toJson(GenericResult.genericInvalidParameterResult);
	}


	private String createSessionlessGame(String userId) {
		NewGameResult newGameResult= battleshipsImpl.createNewGame(DEFAULT_AI_LEVEL, true);
		String gameId = newGameResult.gameId;
		GenericResult activateResult = battleshipsImpl.activateGame(gameId, userId);
		if (activateResult.code != ResultCodeEnum.S_OK) {
			return gson.toJson(activateResult);
		}
		return gson.toJson(newGameResult);
	}

	private String activateGame(String gameId, String userId) {
		return gson.toJson(battleshipsImpl.activateGame(gameId, userId));
	}

	private String newGame(String gameId) {
		return gson.toJson(battleshipsImpl.restart(gameId)); 
	}

	private String closeGame(String gameId) {
		return gson.toJson(battleshipsImpl.closeGame(gameId));
	}

	private String clearSession(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return gson.toJson(GenericResult.genericOkResult);
	}
	
	private String getGameId(HttpSession session, String gameId) {
		NewGameResult result;
		GameState<BattleshipsGame> gameState = battleshipsImpl.findGameId(gameId);
		if (gameState != null) {
			if (gameState.isActive()) {
				result = new NewGameResult(ResultCodeEnum.S_ACTIVATED, gameId);
			}
			else {
				result = new NewGameResult(ResultCodeEnum.S_OK, gameId);
			}
		}
		else {
			result = battleshipsImpl.createNewGame(DEFAULT_AI_LEVEL, true);
			gameId = result.gameId;
			session.setAttribute("gameId", gameId);
		}
		return gson.toJson(result);
	}

	private String hasChanges(String gameId, String versionName) {
		try { 
			int version = Integer.parseInt(versionName);
			return gson.toJson(battleshipsImpl.hasChanges(gameId, version));
		}
		catch (NumberFormatException e) {
			return gson.toJson(GenericResult.genericInvalidParameterResult);
		}
	}

	private String setPlayerNames(String gameId, String player1Name, String player2Name) {
		return gson.toJson(battleshipsImpl.setPlayerNames(gameId, player1Name, player2Name));
	}

	private String setAILevel(String gameId, String aiLevelName) {
		try { 
			int aiLevel = Integer.parseInt(aiLevelName);
			return gson.toJson(battleshipsImpl.setAILevel(gameId, aiLevel));
		}
		catch (NumberFormatException e) {
			return gson.toJson(GenericResult.genericInvalidParameterResult);
		} 
	}

	private String doMove(String gameId, String moveTypeName, String rowName, String colName, String contentName) {
		try { 
			MoveType moveType = parseMoveType(moveTypeName);
			char row = (moveType == MoveType.QUERY) ? parseRow(rowName) : '?';
			int col = (moveType == MoveType.QUERY) ? Integer.parseInt(colName) : -1;
			CellContent cellContent = (moveType == MoveType.ANSWER) ? parseCellContent(contentName) : CellContent.UNKNOWN;
			return gson.toJson(battleshipsImpl.doMove(gameId, new BattleshipsMove(moveType, row, col, cellContent)));
		}
		catch (NumberFormatException e) {
			return gson.toJson(GenericResult.genericInvalidParameterResult);
		}
	}

	private char parseRow(String rowName) {
		if (rowName == null) {
			throw new NumberFormatException("rowName can not be null.");
		}
		String row = rowName.toUpperCase(Locale.ROOT).replaceAll("[^A-Z]*", "");
		if (row.length() != 1) {
			throw new NumberFormatException("rowName has to be a letter from 'A' to 'J'.");
		}
		char result = row.charAt(0);
		if ((result<'A') || (result>'J')) {
			throw new NumberFormatException("rowName has to be a letter from 'A' to 'J'.");
		}
		return result;
	}

	private MoveType parseMoveType(String moveTypeName) {
		if (moveTypeName == null) {
			throw new NumberFormatException("moveTypeName can not be null.");
		}
		if (moveTypeName.equals("Q")) {
			return MoveType.QUERY;
		}
		if (moveTypeName.equals("A")) {
			return MoveType.ANSWER;
		}
		throw new NumberFormatException("invalid moveTypeName, only 'Q' or 'A' is allowed.");
	}

	private CellContent parseCellContent(String contentName) {
		if ((contentName == null) || contentName.isEmpty()) {
			return CellContent.UNKNOWN;
		}
		if (contentName.equals("W")) {
			return CellContent.WATER;
		}
		if (contentName.equals("S")) {
			return CellContent.SHIP;
		}
		if (contentName.equals("X")) {
			return CellContent.SUNK;
		}
		throw new NumberFormatException("invalid cellContent, only 'W', 'S' or 'X' is allowed.");
	}

	private String doAIMove(String gameId) {
		return gson.toJson(battleshipsImpl.doAIMove(gameId));
	}

	private String getGameData(String gameId) {
		return gson.toJson(battleshipsImpl.getGameData(gameId));
	}

	private String getGameDataByUserId(String userId) {
		return gson.toJson(battleshipsImpl.getGameDataByUserId(userId));
	}

	private String getGameParameter(String gameId, String gpName) {
		return gson.toJson(battleshipsImpl.getGameParameter(gameId, gpName));
	}

	private String changeGameParameter(String gameId, String gpName, String gpValue) {
		return gson.toJson(battleshipsImpl.changeGameParameter(gameId, gpName, gpValue));
	}


	/* ======================= */
	/* INTERNAL HELPER METHODS */
	/* ======================= */
	

	private String encode(String text) {
		// TODO: look for encoding in REST Service.
		return text;
	}


}
