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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleDBConnection {

	private final static Logger logger = Logger.getLogger(SimpleDBConnection.class.getName());

	private static final String SEED = SimpleDBConnection.class.getSimpleName()+"."+"getNewConnection()";
	
	private String driverClassName;
	private String jdbcUrl;
	private String user;
	private String password;

	// just the simplest possibility to manage connections, all share one instance :(
	private Connection cachedConnection;


	public SimpleDBConnection(String driverClassName, String jdbcUrl, String user, String passwordEnc) {
		try {
			this.driverClassName = driverClassName;
			this.jdbcUrl = jdbcUrl;
			this.user = user;
			this.password = decrypt(passwordEnc);
			Class<?> clazz = Class.forName(this.driverClassName);
			logger.info("DRIVER="+clazz);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	
	private String decrypt(String passwordEnc) {
		if (passwordEnc == null) {
			return null;
		}
		if (passwordEnc.isEmpty()) {
			return passwordEnc;
		}
		return SimpleCrypto.decrypt(SEED, passwordEnc);
	}


	public boolean isActive() {
		if (user.equals("NODB")) {
			return false;
		}
		return true;
	}

	private Connection getNewConnection() {
		logger.info("getting connection");
		if (user.equals("NODB")) {
			return null;
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl, user, password);
			logger.info("CONN="+conn+"\n");

		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.toString(), ex);
			throw new RuntimeException(ex);
		} 

		return conn;	
	}

	
	public synchronized Connection getConnection() {
		Connection result = cachedConnection; 
		if (result != null) {
			try {
				if (!result.isValid(2)) {
					logger.log(Level.WARNING, "closing invalid connection"); 
					result = null;
				}
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "connection became invalid: "+e.toString(), e); 
				result = null;
			}
		}
		if (result == null) {
			result = getNewConnection();
			cachedConnection = result;
		}
		return result;
	}

	public synchronized void releaseConnection(Connection conn) {
		// currently no pooling, so nothing to release.
	}

	public synchronized void fail(Connection conn) {
		if (conn == cachedConnection) {
			closeConnection();
		}
	}

	public synchronized void closeConnection() {
		Connection conn = cachedConnection;
		cachedConnection = null;
		if (conn != null) {
			try { 
				conn.close(); 
			} 
			catch (SQLException e)	{ 
				logger.log(Level.WARNING, "close connection failed: "+e.toString(), e); 
			}
		}
	}

	public synchronized void shutdown() {
		closeConnection();
	}

	
	public static void main(String[] args) {
		System.out.println(SimpleCrypto.encrypt(SEED, "FHJo7K1fo5EVeTKW"));
	}
	
}
