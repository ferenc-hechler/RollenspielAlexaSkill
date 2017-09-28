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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import de.hechler.aigames.SimpleDBConnection;
import de.hechler.aigames.SimpleDBConnectionFactory;

public class DBConnectionTest {

	private final static Logger logger = Logger.getLogger(DBConnectionTest.class.getName());

	@Before
	public void setup() {
//		System.setProperty("calcbox-conf", "localconf");
		SimpleDBConnection dbConn = SimpleDBConnectionFactory.initDBConn("junit", "org.h2.Driver", "jdbc:h2:~/junit", "", "");
		SimpleDBConnectionFactory.createH2TablesOnNeed(dbConn.getConnection());
	}
	
	@Test
	public void showDBData() {
		logger.info("showDBData");
		StringBuffer result = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = SimpleDBConnectionFactory.getDBConn("junit").getConnection();
			st = conn.createStatement();
			logger.info("ST="+st);
			rs = st.executeQuery("SELECT * FROM SOLOROLEPLAY");
			logger.info("RS"+rs);
			result.append("<p><table>\n");
			result.append("<tr><td>ID</td><td>name</td><td>status</td><td>text</td></tr>\n");
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String status = rs.getString("status");
				String solotext = rs.getString("solotext");
				result.append("<td>" + id + "</td>");
				result.append("<td>" + name + "</td>");
				result.append("<td>" + status+ "</td></tr>");
				result.append("<td>" + solotext+ "</td></tr>");
				
			}
			result.append("</table></p>\n");
		} catch (RuntimeException e) {
			throw e;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (st != null) st.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		System.out.println(result.toString());	
	}

	
	
//
//	// Prepare a statement to insert a record
//	String sql = "INSERT INTO TermsAndConditions (name,description,ownerID)  VALUES  (?,?,?)";
//	PreparedStatement pstmt = connection.prepareStatement(sql);
//
//	// Set the values
//	pstmt.setString(1, "bar condtions");
//	pstmt.setString(2, "Don't be stealin my stuff");
//	pstmt.setString(3, "2");
//
//	// Insert the row
//	pstmt.executeUpdate();
	
}
