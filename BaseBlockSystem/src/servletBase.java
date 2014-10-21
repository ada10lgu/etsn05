import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *  This class is the superclass for all servlets in the application. 
 *  It includes basic functionality required by many servlets, like for example a page head 
 *  written by all servlets, and the connection to the database. 
 *  
 *  This application requires a database.
 *  For username and password, see the constructor in this class.
 *  
 *  <p>The database can be created with the following SQL command: 
 *  mysql> create database base;
 *  <p>The required table can be created with created with:
 *  mysql> create table users(name varchar(10), password varchar(10), primary key (name));
 *  <p>The administrator can be added with:
 *  mysql> insert into users (name, password) values('admin', 'adminp'); 
 *  
 *  @author Martin Host
 *  @version 1.0 
 *  
 */
public class servletBase extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	// Define states
	protected static final int LOGIN_FALSE = 0;
	protected static final int LOGIN_TRUE = 1;	
	protected Connection conn = null;
	protected Access access;
	
	protected static final String ADMIN = "admin";
	protected static final String PROJECT_LEADER = "Project Leader";
	protected static final String t1 = "t1";
	protected static final String t2 = "t2";
	protected static final String t3 = "t3";
	
	/**
	 * Constructs a servlet and makes a connection to the database. 
	 * It also writes all user names on the console for test purpose. 
	 */
    public servletBase() {
    	try{	
    		Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1404?" +
            "user=puss1404&password=ptqp44ed");
			//conn = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1404test?" +
            //"user=puss1404test&password=j5jipsh1"); //för testarna
			access = new Access(conn);
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
    }
    
    /**
     * Checks if a user is logged in or not.
     * @param request The HTTP Servlet request (so that the session can be found)
     * @return true if the user is logged in, otherwise false.
     */
    protected boolean loggedIn(HttpServletRequest request) {
    	boolean isActive = false;
    	HttpSession session = request.getSession(true);
    	Object objectState = session.getAttribute("state");
    	int state = LOGIN_FALSE;
    	if (objectState != null) { 
    		state = (int) objectState; 
    		if(state == LOGIN_TRUE){
    			// See if user is Active
    			int userID;
    			Object userIDObject = session.getAttribute("userID");
    			if(userIDObject != null){		
    				userID = (int) session.getAttribute("userID");
    				isActive = access.updateLog(userID, session.getId());
    			}
    		} 
    	}else{
    		return false;
    	}
    	return (state == LOGIN_TRUE && isActive);
    }
    
    /**
     * Can be used to construct form elements.
     * @param par Input string
     * @return output string = "par" 
     */
    protected String formElement(String par) {
    	return '"' + par + '"';
    }
    
    
    /**
     * Constructs the header of all servlets. 
     * @return String with html code for the header. 
     */
    protected String getPageIntro() {
    	String intro = "<html>" +
                       "<head><title>New Puss System</title></head>"
                       + "<style>"
                       + "ul {list-style-type: none;"
                       + "}"
                       + ".menu{"
                       + "float: left;"
                       + "margin-right: 15px;"
                       + "height: 100%;"
                       + ""
                       + "}"
                       + ".floati{"
                       + "float:left;"
                       + "}"
                       + "</style>" +
                       "<body>";
    	
    	return intro;
    }
    
    /**
     * TODO//
     * Constructs the menu, but only returns the options the user is allowed to view. 
     * @return String with html for the menu.
     */
    protected String printMainMenu(HttpServletRequest request){
    	HttpSession session = request.getSession(true);
    	String role = (String) session.getAttribute("role");
    	String menu = "<div class='menu'><ul>";
    	if(role.equals(ADMIN)) {
    		menu+= "<li><a href='Administration'>Administration</a>";
	    	menu+="<ul>";
	    	menu+= "<li><a href='Administration'>Users</a></li>";
	    	menu+= "<li><a href='ProjectGroupAdmin'>Group</a></li>";
	    	menu+= "</ul>";
	    	menu+= "</li>";
    	}
    	if(role.equals(PROJECT_LEADER) || role.equals(ADMIN)){
    		menu+= "<li><a href='ProjectLeader'>Project Management</a>";
        	menu+= "<ul>";
        	menu+= "<li><a href='ProjectLeader'>Users</a></li>";
        	menu+= "<li><a href='ReportHandling'>Reports</a></li>";
        	menu+= "<li><a href='Statistics'>Statistics</a></li>";
        	menu+= "</ul>";
        	menu+= "</li>";
    	}    	
    	if(!role.equals(ADMIN)){
    		menu+= "<li><a href='TimeReporting?function=view'>Time Reports</a>";
        	menu+= "<ul>";
        	menu+= "<li><a href='TimeReporting?function=view'>View</a></li>";
        	menu+= "<li><a href='TimeReporting?function=update'>Update</a></li>";
        	menu+= "<li><a href='TimeReporting?function=new'>New</a></li>";
        	menu+= "<li><a href='TimeReporting?function=statistics'>Statistics</a></li>";
        	menu+= "</ul>";
        	menu+= "</li>";
        	menu+= "<li><a href='ChangePassword'>Change Password</a></li>";
    	}    	
    	menu+= "<li><a href='LogIn'>Logout</a></li>";
    	menu+= "</ul></div>";
    	return menu;
    }

}
