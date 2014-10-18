package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class PussTest {
	protected static Connection conn = null;	
	
	public static final String TOMCAT_PATH = "tomcat/apache-tomcat-7.0.55/bin/";
	public static final String STARTUP_SHELL = "startup.sh";
	public static final String SHUTDOWN_SHELL = "startup.sh";
	
	public static final String START_URL = "http://localhost:8080/BaseBlockSystem/Start";
	public static final String LOGIN_URL = "http://localhost:8080/BaseBlockSystem/LogIn";
	public static final String ADMINISTRATION_URL = "http://localhost:8080/BaseBlockSystem/Administration";
	public static final String LOGIN_T3 = "91";
	
	@BeforeClass
	public static void initiateServerAndDB() {
		startServer();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1404?" + "user=puss1404&password=ptqp44ed");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@AfterClass
	public static void tearDown() {
		shutDownServer();
	}
	
	protected ResultSet sendSQLCommand(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(query);		
	}
	
	protected int addUser(String username, String password, int is_admin) throws SQLException {
		String query = "insert into users (username, password, is_admin) values ('" + username + "', '" + password + "', " + is_admin +");";
		sendSQLCommand(query);
		query = "select id from users where username='" + username + "';";
		ResultSet rs = sendSQLCommand(query);
		return rs.getInt(0);
	}
	
	protected void deleteUser(String username) throws SQLException {
		String query = "delete from users where username  = '" + username + "';";
		sendSQLCommand(query);
	}
	
	protected void addGroup(String name) throws SQLException {
		String query = "insert into groups (name) values ('" + name + "');";
		sendSQLCommand(query);
	}
	
	protected void clearSessions() throws SQLException {
		String query = "delete from log;";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}
	
	protected void assignGroup(String username, String group) throws SQLException {
		String query = "";
	}
	
	protected static void startServer() {
		try {
			Process process = Runtime.getRuntime().exec(TOMCAT_PATH + STARTUP_SHELL);
			System.out.println("server started");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static void shutDownServer() {
		try {
			Process process = Runtime.getRuntime().exec(TOMCAT_PATH + SHUTDOWN_SHELL);
			System.out.println("server stopped");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
