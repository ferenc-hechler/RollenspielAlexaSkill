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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import de.hechler.aigames.ai.soloroleplay.SoloGame.RepeatRange;
import de.hechler.aigames.ai.soloroleplay.SoloRoleplayImpl;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.SoloConnectResult;
import de.hechler.soloroleplay.util.TextUtil;

//@WebServlet(urlPatterns = "/soloadv", loadOnStartup = 1)
public class SoloAdventureRestService extends HttpServlet {
	
	private static final String RAETSEL = "R"+TextUtil.UML_AE+"TSEL";

	/** the svuid */
	private static final long serialVersionUID = 805917845614037115L;

	private final static Logger logger = Logger.getLogger(SoloAdventureRestService.class.getName());

	private static boolean debugloggingEnabled = Boolean.getBoolean("cfrest.debugging");
	
	public static SoloRoleplayImpl soloRoleplayImpl = new SoloRoleplayImpl();

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
		String cmd = null;
		String gameId = null;
		String param1 = null;
		String param2 = null;
		String param3 = null;
		String param4 = null;
		String responseString;
		
		try {
		    response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json; charset=UTF-8");
		    
			response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
	
//			logger.info("encoding : " + response.getCharacterEncoding());
		    PrintWriter writer = response.getWriter();
			
			gameId = normalizeGameId(request.getParameter("gameId"));
			cmd = request.getParameter("cmd");
			param1 = request.getParameter("param1");
			param2 = request.getParameter("param2");
			param3 = request.getParameter("param3");
			param4 = request.getParameter("param4");
			
			switch(cmd) {
			case "initTests": {
				responseString = initTests();
				break;
			}
			case "enableDebugLogging": {
				responseString = enableDebugLogging(param1);
				break;
			}
			case "activateFlag": {
				responseString = activateFlag(gameId, param1);
				break;
			}
			case "deactivateFlag": {
				responseString = deactivateFlag(gameId, param1);
				break;
			}
			case "directJump": {
				responseString = directJump(gameId, param1, param2);
				break;
			}
			case "connect": {
				responseString = connect(param1);
				break;
			}
			case "forceConnect": {
				responseString = forceConnect(param1);
				break;
			}
			case "upload": {
				responseString = importUpload(param1, param2);
				break;
			}
			case "restart": {
				responseString = restart(gameId);
				break;
			}
			case "getDescription": {
				responseString = getDescription(gameId);
				break;
			}
			case "answer": {
				responseString = answer(gameId, param1);
				break;
			}
			case "repeat": {
				responseString = repeat(gameId, param1);
				break;
			}
			default: {
				responseString = gson.toJson(GenericResult.genericUnknownCommandResult);
				response.setStatus(500);
				break;
			}
			}


		    responseString = TextUtil.encodeRest(responseString); 
	    	writer.println(responseString);
	    	
	    	if (debugloggingEnabled) {
	    		if (!(responseString.contains("S_NO_CHANGES") || cmd.equals("getGameId"))) {
	    			logger.info("RQ[cmd="+cmd+",gid="+gameId+",p1="+param1+",p2="+param2+",p3="+param3+",p4="+param4+"] -> "+responseString);
	    		}
	    	}
	    }
		catch (RuntimeException | IOException e) {
			logger.log(Level.SEVERE, "RQ[cmd="+cmd+",gid="+gameId+",p1="+param1+",p2="+param2+",p3="+param3+",p4="+param4+"] -> "+e.toString(), e);
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

	
	private String initTests() {
		return gson.toJson(GenericResult.genericOkResult);
	}

	private String enableDebugLogging(String value) {
		debugloggingEnabled = Boolean.parseBoolean(value);
		return gson.toJson(GenericResult.genericOkResult);
	}

	private String activateFlag(String gameId, String flagName) {
		GenericResult result= soloRoleplayImpl.activateFlag(gameId, flagName);
		return gson.toJson(result);
	}
	private String deactivateFlag(String gameId, String flagName) {
		GenericResult result= soloRoleplayImpl.deactivateFlag(gameId, flagName);
		return gson.toJson(result);
	}
	
	private String directJump(String gameId, String chapter, String step) {
		GenericResult result= soloRoleplayImpl.directJump(gameId, chapter, step);
		return gson.toJson(result);
	}


 
	private String connect(String userId) {
		SoloConnectResult soloConnectResult= soloRoleplayImpl.connect(userId);
		return gson.toJson(soloConnectResult);
	}

	private String forceConnect(String userId) {
		SoloConnectResult soloConnectResult= soloRoleplayImpl.forceConnect(userId);
		return gson.toJson(soloConnectResult);
	}

	private String importUpload(String userId, String uploadId) {
		GenericResult result= soloRoleplayImpl.importUpload(userId, normalizeGameId(uploadId));
		return gson.toJson(result);
	}

	
	private String getDescription(String gameId) {
		GenericResult result = soloRoleplayImpl.getDescription(gameId);
		return gson.toJson(result);
	}

	
	private String repeat(String gameId, String part) {
		RepeatRange range = getRepeatRange(part);
		GenericResult result = soloRoleplayImpl.repeat(gameId, range);
		return gson.toJson(result);
	}

	
	private RepeatRange getRepeatRange(String part) {
		if (part == null) {
			return RepeatRange.RECONNECT;
		}
		String normPart = TextUtil.normalizeAnswer(part);
		switch (normPart) {
		case "ALLES":
			return RepeatRange.ALL;
		case "RECONNECT":
			return RepeatRange.RECONNECT;
		case "LETZTES":
		case RAETSEL:
		case "GEGNER":
		case "PROBE":
		case "KAMPF":
		case "FRAGE":
			return RepeatRange.LAST;
		}
		return RepeatRange.RECONNECT;
	}

	private String answer(String gameId, String answer) {
		String answerNorm = TextUtil.normalizeAnswer(answer); 
		GenericResult result = soloRoleplayImpl.answer(gameId, answerNorm);
		return gson.toJson(result);
	}

	private String restart(String gameId) {
		GenericResult result = soloRoleplayImpl.restart(gameId);
		return gson.toJson(result);
	}

	

	/* ================== */
	/* startup / shutdown */
	/* ================== */
	

	public void startup() {
		TextUtil.logChars("STARTUP", TextUtil.UML_aeoeueAEOEUESZ);
		soloRoleplayImpl.startup();
	}
	public void shutdown() {
		soloRoleplayImpl.shutdown();
	}



	

}
