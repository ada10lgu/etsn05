package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public abstract class PussTest {
	protected static Connection conn = null;
	protected static WebClient webClient = null;
	
	public static final String TOMCAT_PATH = "tomcat/apache-tomcat-7.0.55/bin/";
	public static final String STARTUP_SHELL = "startup.sh";
	public static final String SHUTDOWN_SHELL = "shutdown.sh";
	
	public static final String GROUP_ADMIN_URL = "http://localhost:8080/BaseBlockSystem/ProjectGroupAdmin";
	public static final String START_URL = "http://localhost:8080/BaseBlockSystem/Start";
	public static final String LOGIN_URL = "http://localhost:8080/BaseBlockSystem/LogIn";
	public static final String ADMINISTRATION_URL = "http://localhost:8080/BaseBlockSystem/Administration";
	public static final String TIMEREPORTING_URL = "http://localhost:8080/BaseBlockSystem/TimeReporting";
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
		//shutDownServer();
		try {
			webClient.closeAllWindows();
		} catch(Exception e) {
//			e.printStackTrace();
		}
	}
	
	@After
	public void clearSessions() throws SQLException {
		String query = "delete from log;";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}
	
	protected void sendSQLCommand(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);		
	}
	
	protected ResultSet sendSQLQuery(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(query);
	}
	
	protected void clearDataBase() {
		//TODO implementera när det finns en test DB.
	}
	
	protected int addUser(String username, String password, int is_admin) throws SQLException {
		String query = "insert into users (username, password, is_admin) values ('" + username + "', '" + password + "', " + is_admin +");";
		sendSQLCommand(query);
		query = "select id from users where username='" + username + "';";
		ResultSet rs = sendSQLQuery(query);
		rs.next();
		return rs.getInt(1);
	}
	
	protected void deleteUser(String username) throws SQLException {
		String query = "select id from users where username = '" + username + "';";
		ResultSet rs = sendSQLQuery(query);
		rs.next();
		int userId = rs.getInt(1);
		query = "delete from user_group where user_id = " + userId + ";";
		sendSQLCommand(query);
		
		query = "delete from log where user_id = " + userId + ";";
		sendSQLCommand(query);
		
		query = "delete from users where username  = '" + username + "';";
		sendSQLCommand(query);
		
	}
	
	protected void deleteGroup(String groupName) throws SQLException {
		String query = "select id from groups where name = '" + groupName + "';";
		ResultSet rs = sendSQLQuery(query);
		if(rs.next()) {
			int groupId = rs.getInt(1);
			
			query = "delete from user_group where group_id = " + groupId + ";";
			sendSQLCommand(query);
			
			query = "delete from groups where name = '" + groupName + "';";
			sendSQLCommand(query);
		}			
	}
	
	protected int addGroup(String groupName) throws SQLException {
		String query = "insert into groups (name) values ('" + groupName + "');";
		sendSQLCommand(query);
		query = "select id from groups where name ='" + groupName + "';";
		ResultSet rs = sendSQLQuery(query);
		rs.next();
		return rs.getInt(1);
	}
	
	protected void assignGroup(int userId, int groupId, String role) throws SQLException {
		String query = "insert into user_group (user_id, group_id, role) values (" + userId + ", " + groupId + ", '" + role + "');";
		sendSQLCommand(query);

	}
	
	protected static void startServer() {
		try {
			Process process = Runtime.getRuntime().exec(TOMCAT_PATH + STARTUP_SHELL);
			System.out.println("server started");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected HtmlPage login(String username, String password, String group) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient = new WebClient();

	    // Get the first page
	    final HtmlPage page1 = webClient.getPage(LOGIN_URL);

	    // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    final HtmlForm form = page1.getFormByName("input");

	    final HtmlSubmitInput button = form.getInputByValue("Submit");
	    final HtmlTextInput userField = form.getInputByName("user");
	    final HtmlPasswordInput passwordField = form.getInputByName("password");

	    // Change the value of the text field
	    userField.setValueAttribute(username);
	    passwordField.setValueAttribute(password);
	    if(group != null) {
	    	final HtmlSelect groupList = form.getSelectByName("groupID");
	    	groupList.setSelectedAttribute(groupList.getOptionByText(group), true);
	    }
	    //	    groupList.setSelectedAttribute(LOGIN_T3, true);

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();

//	    assertEquals("jonatan could not log in", START_URL, page2.getUrl().toString());

//	    HtmlAnchor logout = page2.getAnchorByHref("LogIn");
//	    logout.click();
//	    webClient.closeAllWindows();
	    return page2;
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
