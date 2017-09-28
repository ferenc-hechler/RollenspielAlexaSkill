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
package de.hechler.aigames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.hechler.aigames.SimpleDBConnection;
import de.hechler.aigames.SimpleDBConnectionFactory;
import de.hechler.aigames.UserPersistDAO;

public class UserPersistDAOTest {

	@Before
	public void setup() {
//		System.setProperty("calcbox-conf", "localconf");
		SimpleDBConnection dbConn = SimpleDBConnectionFactory.initDBConn("junit", "org.h2.Driver", "jdbc:h2:~/junit", "", "");
		SimpleDBConnectionFactory.createH2TablesOnNeed(dbConn.getConnection());
	}
	
	@Test
	public void testUserDataPersistence() {
		UserPersistDAO userPersistDAO = new UserPersistDAO("junit");

		userPersistDAO.delete("JUnit-USER-1");
		userPersistDAO.delete("JUnit-USER-2");

		String data1 = userPersistDAO.getData("JUnit-USER-1");
		assertNull(data1);
		String data2 = userPersistDAO.getData("JUnit-USER-2");
		assertNull(data2);
		
		userPersistDAO.saveOrUpdate("JUnit-USER-1", "Testdata for JUnit-USER-1.");
		data1 = userPersistDAO.getData("JUnit-USER-1");
		assertEquals("Testdata for JUnit-USER-1.", data1);
		data2 = userPersistDAO.getData("JUnit-USER-2");
		assertNull(data2);
		
		userPersistDAO.saveOrUpdate("JUnit-USER-1", "Updated Testdata for JUnit-USER-1.");
		userPersistDAO.saveOrUpdate("JUnit-USER-2", "Testdata for JUnit-USER-2.");
		data1 = userPersistDAO.getData("JUnit-USER-1");
		assertEquals("Updated Testdata for JUnit-USER-1.", data1);
		data2 = userPersistDAO.getData("JUnit-USER-2");
		assertEquals("Testdata for JUnit-USER-2.", data2);
		
		userPersistDAO.delete("JUnit-USER-1");
		userPersistDAO.delete("JUnit-USER-2");

		data1 = userPersistDAO.getData("JUnit-USER-1");
		assertNull(data1);
		data2 = userPersistDAO.getData("JUnit-USER-2");
		assertNull(data2);

	}
	


}
