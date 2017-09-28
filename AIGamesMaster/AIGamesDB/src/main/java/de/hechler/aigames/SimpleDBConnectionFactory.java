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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class SimpleDBConnectionFactory {

	private final static Logger logger = Logger.getLogger(SimpleDBConnectionFactory.class.getName());

	private static Map<String, SimpleDBConnection> activeDBConnections = new HashMap<>();
	
	public static synchronized SimpleDBConnection getDBConn(String name) {
		SimpleDBConnection result = activeDBConnections.get(name);
		return result;
	}

	public static synchronized SimpleDBConnection initDBConn(String name) {
		String prefix = ((name==null)||name.isEmpty()) ? "" : name+".";   
		Properties dbProps = getSysProps(prefix);
		if (dbProps == null) {
			dbProps = getFileProps(name);
		}
		if (dbProps == null) {
			throw new RuntimeException("DB PROPERTIES NOT FOUND FOR DB NAME '"+name+"'!");
		}
		return initDBConn(name, dbProps);
	}
	
	public static synchronized SimpleDBConnection initDBConn(String name, Properties dbProps) {
		String driverClassName = dbProps.getProperty("db.driverClassName");
		String jdbcUrl = dbProps.getProperty("db.jdbcUrl");
		String user = dbProps.getProperty("db.user");
		String passwordEnc = dbProps.getProperty("db.password.enc");
		return initDBConn(name, driverClassName, jdbcUrl, user, passwordEnc);
	}

	public static synchronized SimpleDBConnection initDBConn(String name, String driverClassName, String jdbcUrl, String user, String passwordEnc) {
		SimpleDBConnection result = activeDBConnections.get(name);
		if (result == null) {
			logger.info(name+";"+driverClassName+";"+jdbcUrl+";"+user+";****");
			result = new SimpleDBConnection(driverClassName, jdbcUrl, user, passwordEnc);
			activeDBConnections.put(name, result);
		}
		return result;
	}

	public synchronized static void shutdown() {
		for (SimpleDBConnection dbc:activeDBConnections.values()) {
			dbc.shutdown();
		}
		activeDBConnections.clear();
	}
	
	public synchronized static void shutdown(String dbName) {
		SimpleDBConnection dbc = activeDBConnections.remove(dbName);
		if (dbc != null) {
			dbc.shutdown();
		}
	}
	
	public static void createH2TablesOnNeed(Connection conn) {
		try {
			Statement stmt = conn.createStatement(); 
//			String dropQ = "DROP TABLE IF EXISTS SOLOROLEPLAY_X";
//          stmt.executeUpdate(dropQ);
			String createH2Q = ""
					+ "CREATE TABLE IF NOT EXISTS SOLOROLEPLAY "
					+ "(" 
					+ "  ID INT AUTO_INCREMENT PRIMARY KEY," 
					+ "  NAME VARCHAR(255) NOT NULL UNIQUE," 
					+ "  STATUS VARCHAR(20) NOT NULL,"
					+ "  OWNER VARCHAR(255) NOT NULL,"
					+ "  SOLOTEXT MEDIUMTEXT NOT NULL," 
					+ "  CREATIONDATE DATETIME, " 
					+ "  LASTUPDATE DATETIME" 
					+ ")";
            stmt.executeUpdate(createH2Q);
            
//			String dropUserpersistQ = "DROP TABLE IF EXISTS USERPERSIST";
//          stmt.executeUpdate(dropUserpersistQ);
			String createUserpersistH2Q = ""
					+ "CREATE TABLE IF NOT EXISTS USERPERSIST (" 
					+ "  ID INT AUTO_INCREMENT PRIMARY KEY," 
					+ "  USERID VARCHAR(255) NOT NULL UNIQUE," 
					+ "  ROLE VARCHAR(32),"
					+ "  DATA TEXT NOT NULL," 
					+ "  CREATIONDATE DATETIME," 
					+ "  PERSISTTIME DATETIME NOT NULL" 
					+ ")";
            stmt.executeUpdate(createUserpersistH2Q);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


	public static void createMySQLTablesOnNeed(Connection conn) {
		try {
			Statement stmt = conn.createStatement(); 
//			String dropQ = "DROP TABLE IF EXISTS SOLOROLEPLAY_X";
//          stmt.executeUpdate(dropQ);
			String createMySQLQ = ""
					+ "CREATE TABLE IF NOT EXISTS SOLOROLEPLAY "
					+ "(" 
					+ "  ID INT AUTO_INCREMENT PRIMARY KEY," 
					+ "  NAME VARCHAR(255) NOT NULL UNIQUE," 
					+ "  STATUS VARCHAR(20) NOT NULL,"
					+ "  OWNER VARCHAR(255) NOT NULL,"
					+ "  SOLOTEXT MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL," 
					+ "  CREATIONDATE DATETIME, " 
					+ "  LASTUPDATE DATETIME" 
					+ ")";
            stmt.executeUpdate(createMySQLQ);
            
//			String dropUserpersistQ = "DROP TABLE IF EXISTS USERPERSIST";
//          stmt.executeUpdate(dropUserpersistQ);
			String createUserpersistMySQLQ = ""
					+ "CREATE TABLE IF NOT EXISTS USERPERSIST (" 
					+ "  ID INT AUTO_INCREMENT PRIMARY KEY," 
					+ "  USERID VARCHAR(255) NOT NULL UNIQUE," 
					+ "  ROLE VARCHAR(32),"
					+ "  DATA TEXT NOT NULL," 
					+ "  CREATIONDATE DATETIME," 
					+ "  PERSISTTIME DATETIME NOT NULL" 
					+ ")";
            stmt.executeUpdate(createUserpersistMySQLQ);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	public static Properties getSysProps(String prefix) {
		String driverClassName = System.getProperty(prefix+"db.driverClassName");
		if ((driverClassName == null) || driverClassName.isEmpty()) {
			logger.info("SYS PROP '"+prefix+"db.driverClassName' NOT FOUND FOR DB WITH PREFIX '"+prefix+"'.");
			return null;
		}
		logger.info("using sys props '"+prefix+"db.driverClassName' for db "+prefix);
		String jdbcUrl = System.getProperty(prefix+"db.jdbcUrl");
		String user = System.getProperty(prefix+"db.user");
		String passwordEnc = System.getProperty(prefix+"db.password.enc");
		Properties result = new Properties();
		result.setProperty("db.driverClassName", driverClassName);
		result.setProperty("db.jdbcUrl", jdbcUrl);
		result.setProperty("db.user", user);
		result.setProperty("db.password.enc", passwordEnc);
		return result;
	}

	public static Properties getFileProps(String dbname) {
		String prefix = ((dbname==null)||dbname.isEmpty()) ? "" : (dbname+"-");
		Properties result = new Properties();
		String configFile = System.getProperty(dbname+"-conf", "/etc/"+prefix+"db.properties");
		File dbPropsFile = Paths.get(configFile).toFile().getAbsoluteFile();
		if (!dbPropsFile.exists()) {
			logger.info("PROPS FILE '"+dbPropsFile.getAbsolutePath()+"' NOT FOUND FOR DB WITH NAME '"+dbname+"'.");
			return null;
		}
		logger.info("using '"+dbPropsFile.getAbsolutePath()+"' for db "+dbname+".");
		try {
			result.load(new FileInputStream(dbPropsFile));
		} catch (IOException e) {
			throw new RuntimeException("could not read db properties for "+dbname+" from "+dbPropsFile.getAbsolutePath());
		}
		return result;
	}

	
}
