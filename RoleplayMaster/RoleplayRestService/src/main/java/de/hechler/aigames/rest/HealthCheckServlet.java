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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hechler.aigames.SimpleDBConnection;
import de.hechler.aigames.SimpleDBConnectionFactory;
import de.hechler.aigames.persist.DAOFactory;

//@WebServlet(urlPatterns = "/soloadv", loadOnStartup = 1)
public class HealthCheckServlet extends HttpServlet {
	
	/** the svuid. */
	private static final long serialVersionUID = 4472826308561137485L;
	
	private final static Logger logger = Logger.getLogger(HealthCheckServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    doPost(request, response);
	}
	 
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		
		try {
		    response.setCharacterEncoding("UTF-8");
		    response.setContentType("text/html; charset=UTF-8");
		    
			response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
	
//			logger.info("encoding : " + response.getCharacterEncoding());
		    PrintWriter writer = response.getWriter();

		    String ibookDbStatus = getDbStatus();
		    
		    String html = "<html><body><table border='1'>"
		    		+ "<tr><th>Object</th><th>Status</th></tr>"
		    		+ "<tr><td>Solo-DB</td><td>"+ibookDbStatus+"</td></tr>"
		    		+ "</table></body></html>";
	    	writer.println(html);
		    
		    boolean ok = "OK".equals(ibookDbStatus);
		    if (!ok) {
				response.setStatus(500);
		    }
	    }
		catch (RuntimeException | IOException e) {
			logger.log(Level.SEVERE, "RQ[HEALT] -> "+e.toString(), e);
			throw e;
		}
	}

	private String getDbStatus() {
		String result = "OK";
		try {
			SimpleDBConnection dbConn = SimpleDBConnectionFactory.getDBConn(DAOFactory.DB_NAME);
			if (dbConn == null) {
				result = "NOT INITIALIZED";
			}
			else if (!dbConn.isActive()) {
				result = "NOT ACTIVE";
			}
		}
		catch (Exception e) {
			result = e.toString();
		}
		return result;
	}

	

}
