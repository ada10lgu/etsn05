package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import sun.misc.Cleaner;

import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
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
	
	public static final String BASIC_URL = "http://localhost:8080/BaseBlockSystem/";
	
	public static final String GROUP_HANDLING = "GroupHandling", GROUP_HANDLING_URL = BASIC_URL + GROUP_HANDLING;
	public static final String GROUP_ADMIN = "ProjectGroupAdmin", GROUP_ADMIN_URL = BASIC_URL + GROUP_ADMIN;

	public static final String START = "Start", START_URL = BASIC_URL + START;
	public static final String LOGIN = "LogIn", LOGIN_URL = BASIC_URL + LOGIN;
	public static final String ADMINISTRATION = "Administration", ADMINISTRATION_URL = BASIC_URL + ADMINISTRATION;

	public static final String TIMEREPORTING = "TimeReporting?function=view", TIMEREPORTING_URL = BASIC_URL + TIMEREPORTING;
	public static final String TIMEREPORTING_UPDATE = "TimeReporting?function=update", TIMEREPORTING_URL_UPDATE = BASIC_URL + TIMEREPORTING_UPDATE;
	public static final String TIMEREPORTING_NEW = "TimeReporting?function=new", TIMEREPORTING_URL_NEW = BASIC_URL + TIMEREPORTING_NEW;
	public static final String TIMEREPORTING_STATISTICS = "TimeReporting?function=statistics", TIMEREPORTING_URL_STATISTICS = BASIC_URL + TIMEREPORTING_STATISTICS;
	
	public static final String PROJECT_LEADER = "ProjectLeader", PROJECT_LEADER_URL = BASIC_URL + PROJECT_LEADER;
	public static final String REPORT_HANDLING = "ReportHandling", REPORT_HANDLING_URL = BASIC_URL + REPORT_HANDLING;
	public static final String CHANGE_PASSWORD = "ChangePassword", CHANGE_PASSWORD_URL = BASIC_URL + CHANGE_PASSWORD;
	public static final String STATISTICS = "Statistics", STATISTICS_URL = BASIC_URL + STATISTICS;
	
	public static final String ADMIN_USERNAME = "admin";
	public static final String ADMIN_PASSWORD = "adminpw";
	public static final String ADMIN_GROUP = null;
	
	@BeforeClass
	public static void initiateServerAndDB() {
		restartServer();
		webClient = new WebClient();
		webClient.setConfirmHandler(new ConfirmHandler() {public boolean handleConfirm(Page page, String message) {return true;}});
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1404test?" +
		            "user=puss1404test&password=j5jipsh1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		shutDownServer();
		try {
			webClient.closeAllWindows();
		} catch(Exception e) {
//			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp() throws SQLException{
		clearDatabase();
	}
	
	@After
	public void clearDatabase() throws SQLException {
		String query = "delete from report_times;";
		sendSQLCommand(query);
		query = "delete from reports;";
		sendSQLCommand(query);
		query = "delete from user_group;";
		sendSQLCommand(query);
		query = "delete from groups;";
		sendSQLCommand(query);
		query = "delete from log;";
		sendSQLCommand(query);
		query = "delete from users;";
		sendSQLCommand(query);
		query = "insert into users(id, username, password, is_admin) values (1, 'admin', 'adminpw', 1);";
		sendSQLCommand(query);
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
	
	protected HtmlPage getPageByAnchor(HtmlPage page, String anchor) throws IOException {
		HtmlAnchor htmlAnchor = page.getAnchorByHref(anchor);
		return page = htmlAnchor.click();
	}
	
	protected int addUser(String username, String password, int is_admin) throws SQLException {
		String query = "insert into users (username, password, is_admin) values ('" + username + "', '" + password + "', " + is_admin +");";
		sendSQLCommand(query);
		return getUserId(username);
	}
	
	protected int getUserId(String username) throws SQLException {
		String query = "select id from users where username='" + username + "';";
		ResultSet rs = sendSQLQuery(query);
		rs.next();
		return rs.getInt(1);
	}
	
	protected void deleteUser(String username) throws SQLException {
		ResultSet rs = getUserByName(username);
		if (rs.next()) {
			int userId = rs.getInt(1);
			String query = "delete from report_times where user_id = " + userId + ";";
			sendSQLCommand(query);
			
			query = "delete from log where user_id = " + userId + ";";
			sendSQLCommand(query);
			
			query = "delete from users where username = '" + username + "';";
			sendSQLCommand(query);
		}
	}
	
	protected void deleteGroup(String groupName) throws SQLException {
		ResultSet rs = getGroupByName(groupName);
		if(rs.next()) {
			int groupId = rs.getInt(1);
			
			String query = "delete from user_group where group_id = " + groupId + ";";
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
	


	protected ResultSet getUserByName(String username) throws SQLException {
		String query = "select id from users where username = '" + username + "';";
		return sendSQLQuery(query);
	}
	
	private ResultSet getGroupByName(String groupName) throws SQLException {
		String query = "select id from groups where name = '" + groupName + "';";
		return sendSQLQuery(query);
	}
	
	protected void addUserToGroup(String username, String groupName, String role) throws SQLException {
		ResultSet userRS = getUserByName(username);
		userRS.next();
		ResultSet groupRS = getGroupByName(groupName);
		groupRS.next();
		String query = "insert into user_group (user_id, group_id, role) values (" + userRS.getInt(1) + ", " + groupRS.getInt(1) + ", '" + role +"');";
		sendSQLCommand(query);
	}
	
	protected int assignGroup(int userId, int groupId, String role) throws SQLException {
		String query = "insert into user_group (user_id, group_id, role) values (" + userId + ", " + groupId + ", '" + role + "');";
		sendSQLCommand(query);
		query = "select id from user_group where user_id ='" + userId + "';";
		ResultSet rs = sendSQLQuery(query);
		rs.next();
		return rs.getInt(1);
	}
	
	protected ResultSet signedReports(int userid) throws SQLException {
		String query = "select signed from reports where user_group_id ='" + userid + "';";
		ResultSet rs = sendSQLQuery(query);
		return rs;
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
		

	    // Get the first page
	    HtmlPage page1 = webClient.getPage(LOGIN_URL);

	    // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    HtmlForm form = page1.getFormByName("input");

	    HtmlSubmitInput button = form.getInputByValue("Submit");
	    HtmlTextInput userField = form.getInputByName("user");
	    HtmlPasswordInput passwordField = form.getInputByName("password");

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
	
	protected HtmlPage switchPage(HtmlPage page, String anchor) throws IOException{
		HtmlAnchor anchorPage = page.getAnchorByHref(anchor);
		final HtmlPage newPage = anchorPage.click();
		return newPage;
	}
	
	protected static void restartServer(){
		try {
			System.out.print("Server Restarting....");
			Process process = Runtime.getRuntime().exec(TOMCAT_PATH + SHUTDOWN_SHELL);
			Process process2 = Runtime.getRuntime().exec(TOMCAT_PATH + STARTUP_SHELL);
			TimeUnit.SECONDS.sleep(2);
			System.out.println(" Done!");
		}catch (Exception e){
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
