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
package de.hechler.aigames.listeners;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.hechler.aigames.SimpleDBConnection;
import de.hechler.aigames.ai.DAOFactory;
import de.hechler.aigames.rest.BattleshipsRestService;
import de.hechler.aigames.rest.ConnectFourRestService;
import de.hechler.aigames.rest.SoloAdventureRestService;;

public class StartupShutdownListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		initLogging();
		checkRuntime();
		ConnectFourRestService.connectFourImpl.startup();
		BattleshipsRestService.battleshipsImpl.startup();
		SoloAdventureRestService.soloRoleplayImpl.startup();
	}
	
	private void initLogging() {
//		System.setProperty("java.util.logging.FileHandler.encoding", "ISO-8859-1");
//		System.setProperty("file.encoding", "ISO-8859-1");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ConnectFourRestService.connectFourImpl.shutdown();
		BattleshipsRestService.battleshipsImpl.shutdown();
		SoloAdventureRestService.soloRoleplayImpl.shutdown();
	}
	
	
	private void checkRuntime() {
		SimpleDBConnection dbconn = DAOFactory.initDB();
		if (dbconn.isActive()) {
			Connection connection = dbconn.getConnection();
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
