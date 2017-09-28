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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoloRoleplayDAO {

	private final static Logger logger = Logger.getLogger(SoloRoleplayDAO.class.getName());
	
	private SimpleDBConnection dbconn;
	
	public SoloRoleplayDAO(String dbName) {
		this(SimpleDBConnectionFactory.getDBConn(dbName));
	}
	
	public SoloRoleplayDAO(SimpleDBConnection dbconn) {
		this.dbconn = dbconn;
	}
	
	public List<String> getSoloNamesInStatus(String statusFilter) {
		List<String> result = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT NAME FROM SOLOROLEPLAY WHERE STATUS = ? ORDER BY ID");
			pst.setString(1, statusFilter);
			rs = pst.executeQuery();
			while (rs.next()) {
				String name = rs.getString("NAME");
				result.add(name);
			}
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return result;	
	}

	public List<String> getAllSoloNames() {
		List<String> result = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT NAME FROM SOLOROLEPLAY ORDER BY ID");
			rs = pst.executeQuery();
			while (rs.next()) {
				String name = rs.getString("NAME");
				result.add(name);
			}
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return result;	
	}

	public List<String> getSoloNamesFromOwner(String owner) {
		List<String> result = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT NAME FROM SOLOROLEPLAY WHERE OWNER = ? ORDER BY ID");
			pst.setString(1, owner);
			rs = pst.executeQuery();
			while (rs.next()) {
				String name = rs.getString("NAME");
				result.add(name);
			}
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return result;	
	}

	public String getSoloTextWithName(String soloName) {
		String result = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT SOLOTEXT FROM SOLOROLEPLAY WHERE NAME = ?");
			pst.setString(1, soloName);
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getString("SOLOTEXT");
			}
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return result;	
	}

	private int getNextID() {
		int result = 1;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT MAX(ID)+1 FROM SOLOROLEPLAY");
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return result;	
	}
	

	public int insertSoloText(String soloName, String status, String owner, String soloText) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int nextId = getNextID();
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("INSERT INTO SOLOROLEPLAY(ID, NAME, STATUS, OWNER, SOLOTEXT, CREATIONDATE, LASTUPDATE) VALUES(?,?,?,?,?,?,?)");
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			pst.setInt(1, nextId);
			pst.setString(2, soloName);
			pst.setString(3, status);
			pst.setString(4, owner);
			pst.setString(5, soloText);
			pst.setTimestamp(6, now);
			pst.setTimestamp(7, now);
			cnt = pst.executeUpdate();
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return cnt;
	}

	public int updateSoloStatus(String soloName, String newStatus) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("UPDATE SOLOROLEPLAY SET STATUS = ?, LASTUPDATE = ? WHERE NAME = ?");
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			pst.setString(1, newStatus);
			pst.setTimestamp(2, now);
			pst.setString(3, soloName);
			cnt = pst.executeUpdate();
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return cnt;
	}

	public int updateSoloText(String soloName, String soloText) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("UPDATE SOLOROLEPLAY SET SOLOTEXT = ?, LASTUPDATE = ? WHERE NAME = ?");
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			pst.setString(1, soloText);
			pst.setTimestamp(2, now);
			pst.setString(3, soloName);
			cnt = pst.executeUpdate();
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return cnt;
	}

	public int deleteSolo(String soloName) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("DELETE FROM SOLOROLEPLAY WHERE NAME = ?");
			pst.setString(1, soloName);
			cnt = pst.executeUpdate();
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return cnt;
	}

	public boolean isActive() {
		return dbconn.isActive();
	}

	public int deleteSoloFromOwnerInStatus(String owner, String status) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("DELETE FROM SOLOROLEPLAY WHERE OWNER = ? AND STATUS = ?");
			pst.setString(1, owner);
			pst.setString(2, status);
			cnt = pst.executeUpdate();
		} catch (RuntimeException e) {
			dbconn.fail(conn);
			throw e;
		} catch (SQLException e) {
			dbconn.fail(conn);
			throw new RuntimeException(e);
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e)		{ logger.log(Level.SEVERE, e.toString(), e); }
			try { if (pst != null) pst.close(); } catch (SQLException e) 	{ logger.log(Level.SEVERE, e.toString(), e); }
			dbconn.releaseConnection(conn);
		}
		return cnt;
	}

	
}
