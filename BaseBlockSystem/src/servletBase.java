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
	
	
	/**
	 * Constructs a servlet and makes a connection to the database. 
	 * It also writes all user names on the console for test purpose. 
	 */
    public servletBase() {
    	try{	
    		Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://vm26.cs.lth.se/puss1404?" +
            "user=puss1404&password=ptqp44ed");	
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
                       + "}"
                       + "</style>" +
                       "<body>";
    	
    	return intro;
    }
    
    /**
     * Constructs the menu, but only returns the options the user is allowed to view. 
     * @return String with html for the menu.
     */
    protected String printMainMenu(){
    	String menu = "<div class='menu'><ul>"
    				+ "<li><a href=" + formElement("Administration") + ">Administration</a>"
    				+ "<ul>"
    				+ "<li><a href=" + formElement("Administration") + ">Users</a></li>"
    				+ "<li><a href=" + formElement("ProjectGroupAdmin") + ">Group</a></li>"
    				+ "</ul>"
    				+ "</li>"
    				+ "<li><a href=" + formElement("ProjectLeader") + ">Project Management</a>"
    				+ "<ul>"
    				+ "<li><a href=" + formElement("ProjectLeader") + ">Users</a></li>"
    				+ "<li><a href='#'>Reports</a></li>"
    				+ "<li><a href='#'>Statistics</a></li>"
    				+ "</ul>"
    				+ "</li>"
    				+ "<li><a href='#'>Time Reports</a>"
    				+ "<ul>"
    				+ "<li><a href='#'>View</a></li>"
    				+ "<li><a href='#'>Update</a></li>"
    				+ "<li><a href='#'>New</a></li>"
    				+ "<li><a href='#'>Statistics</a></li>"
    				+ "</ul>"
    				+ "</li>"
    				+ "<li><a href='#'>Change Password</a></li>"
    				+ "<li><a href='LogIn'>Logout</a></li>"
    				+ "</ul></div>"
    			;
    	return menu;
    }

}
