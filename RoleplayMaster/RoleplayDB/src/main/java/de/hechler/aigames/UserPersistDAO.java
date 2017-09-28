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
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserPersistDAO {

	private final static Logger logger = Logger.getLogger(UserPersistDAO.class.getName());
	
	public static class UserInfo {
		private String userId;
		private String role;
		private String data;
		public UserInfo(String userId, String role, String data) {
			super();
			this.userId = userId;
			this.role = role;
			this.data = data;
		}
		public String getUserId() {
			return userId;
		}
		public String getRole() {
			return role;
		}
		public String getData() {
			return data;
		}
	}

	private SimpleDBConnection dbconn;
	
	public UserPersistDAO(String dbName) {
		this(SimpleDBConnectionFactory.getDBConn(dbName));
	}
	
	public UserPersistDAO(SimpleDBConnection dbconn) {
		this.dbconn = dbconn;
	}
	
	public synchronized UserInfo getUserInfo(String userId) {
		UserInfo result = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT ROLE, DATA FROM USERPERSIST WHERE USERID = ?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				String role = rs.getString(1);
				String data = rs.getString(2);
				result = new UserInfo(userId, role, data);
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


	public synchronized String getData(String userId) {
		String result = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT DATA FROM USERPERSIST WHERE USERID = ?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
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

	public synchronized String getRole(String userId) {
		String result = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT ROLE FROM USERPERSIST WHERE USERID = ?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
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

	public synchronized int saveOrUpdate(String userId, String data) {
		int cnt = update(userId, data);
		if (cnt == 0) {
			cnt = save(userId, data);
		}
		return cnt;
	}

	private int save(String userId, String data) {
		int cnt;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int nextId = getNextID();
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("INSERT INTO USERPERSIST(ID, USERID, ROLE, DATA, CREATIONDATE, PERSISTTIME) VALUES(?,?,?,?,?,?)");
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			String role = "USER";
			pst.setInt(1, nextId);
			pst.setString(2, userId);
			pst.setString(3, role);
			pst.setString(4, data);
			pst.setTimestamp(5, now);
			pst.setTimestamp(6, now);
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

	private int update(String userId, String data) {
		int cnt;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("UPDATE USERPERSIST SET DATA = ?, PERSISTTIME = ? WHERE USERID = ?");
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			pst.setString(1, data);
			pst.setTimestamp(2, now);
			pst.setString(3, userId);
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

	public synchronized int delete(String userId) {
		int cnt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("DELETE FROM USERPERSIST WHERE USERID = ?");
			pst.setString(1, userId);
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

	
	
	private int getNextID() {
		int result = 1;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dbconn.getConnection();
			pst = conn.prepareStatement("SELECT MAX(ID)+1 FROM USERPERSIST");
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

}
