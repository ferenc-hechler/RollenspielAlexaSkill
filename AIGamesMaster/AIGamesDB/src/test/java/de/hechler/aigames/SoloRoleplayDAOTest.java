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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.hechler.aigames.SimpleDBConnection;
import de.hechler.aigames.SimpleDBConnectionFactory;
import de.hechler.aigames.SoloRoleplayDAO;

public class SoloRoleplayDAOTest {

	@Before
	public void setup() {
//		System.setProperty("calcbox-conf", "localconf");
		SimpleDBConnection dbConn = SimpleDBConnectionFactory.initDBConn("junit", "org.h2.Driver", "jdbc:h2:~/junit", "", "");
		SimpleDBConnectionFactory.createH2TablesOnNeed(dbConn.getConnection());
	}
	
	@Test
	public void showSoloNames() {
		SoloRoleplayDAO soloDao = new SoloRoleplayDAO("junit");

		List<String> soloNames;

		soloDao.deleteSolo("JUnit-Test-1");
		soloDao.deleteSolo("JUnit-Test-2");
		soloDao.deleteSolo("JUnit-Test-3");
		soloDao.deleteSolo("JUnit-Test-4");

		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitA");
		System.out.println("OwnerJUnitA: " +  soloNames);
		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitB");
		System.out.println("OwnerJUnitB: " +  soloNames);

		soloNames = soloDao.getSoloNamesInStatus("INTEST");
		System.out.println("INTEST: " +  soloNames);
		soloNames = soloDao.getSoloNamesInStatus("TESTPUB");
		System.out.println("TESTPUB: " +  soloNames);

		soloDao.insertSoloText("JUnit-Test-1", "INTEST", "OwnerJUnitA", "das ist jetzt nur ein kurzer text1!");
		soloDao.insertSoloText("JUnit-Test-2", "INTEST", "OwnerJUnitB", "das ist jetzt nur ein kurzer text2!");
		soloDao.insertSoloText("JUnit-Test-3", "INTEST", "OwnerJUnitA", "das ist jetzt nur ein kurzer text3!");
		soloDao.insertSoloText("JUnit-Test-4", "TESTPUB", "OwnerJUnitB", "das ist jetzt nur ein kurzer text4!");
		
		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitA");
		System.out.println("OwnerJUnitA: " +  soloNames);
		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitB");
		System.out.println("OwnerJUnitB: " +  soloNames);

		soloNames = soloDao.getSoloNamesInStatus("INTEST");
		System.out.println("INTEST: " +  soloNames);
		soloNames = soloDao.getSoloNamesInStatus("TESTPUB");
		System.out.println("TESTPUB: " +  soloNames);

		soloDao.updateSoloStatus("JUnit-Test-3", "TESTPUB");
		soloDao.updateSoloText("JUnit-Test-2", "geaenderter Text von: "+soloDao.getSoloTextWithName("JUnit-Test-2"));

		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitA");
		System.out.println("OwnerJUnitA: " +  soloNames);
		soloNames = soloDao.getSoloNamesFromOwner("OwnerJUnitB");
		System.out.println("OwnerJUnitB: " +  soloNames);

		soloNames = soloDao.getSoloNamesInStatus("INTEST");
		System.out.println("INTEST: " +  soloNames);
		soloNames = soloDao.getSoloNamesInStatus("TESTPUB");
		System.out.println("TESTPUB: " +  soloNames);

		soloDao.deleteSolo("JUnit-Test-1");
		soloDao.deleteSolo("JUnit-Test-2");
		soloDao.deleteSolo("JUnit-Test-3");
		soloDao.deleteSolo("JUnit-Test-4");

	}
	

}
