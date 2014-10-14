import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LogIn
 * 
 * A log-in page. 
 * 
 * The first thing that happens is that the user is logged out if he/she is logged in. 
 * Then the user is asked for name and password. 
 * If the user is logged in he/she is directed to the functionality page. 
 * 
 * @author Martin Host
 * @version 1.0
 * 
 */
@WebServlet("/LogIn")
public class LogIn extends servletBase {
	private int id;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogIn() {
		super();
	}

	/**
	 * Generates a form for login. 
	 * @return HTML code for the form
	 */
	protected String loginRequestForm() {
		String html = "<p>Please enter your name and password in order to log in:</p>";
		html += "<p> <form name=" + formElement("input");
		html += " method=" + formElement("post");
		html += "<p> Name: <input type=" + formElement("text") + " name=" + formElement("user") + '>'; 
		html += "<p> Password: <input type=" + formElement("password") + " name=" + formElement("password") + '>';
		html += "<p> Group: <input type=" + formElement("text") + " name=" + formElement("groupID") + '>';
		html += "<p> <input type=" + formElement("submit") + "value=" + formElement("Submit") + '>';
		html += "</form>";
		return html;
	}


	/**
	 * Checks with the database if the user should be accepted
	 * @param name The name of the user
	 * @param password The password of the user
	 * @return true if the user should be accepted
	 */
	private boolean checkUser(String name, String password, PrintWriter out) {

		boolean userOk = false;
		boolean userChecked = false;

		if (name != null && password != null) {
			try {
				Statement stmt = conn.createStatement();		    
				ResultSet rs = stmt.executeQuery("select * from users"); 
				while (rs.next() && !userChecked) {
					String nameSaved = rs.getString("username"); 
					String passwordSaved = rs.getString("password");
					int loggedIn = rs.getInt("is_logged_in");
					id = rs.getInt("ID");
					if (name.equals(nameSaved)) {
						/*if (loggedIn==1) {
				    		id=-1;
				    		out.println("<p>User was already logged in </p>");
				    		rs.close();
				    		stmt.close();
				    		return false;
				    	}*/
						userChecked = true;
						userOk = password.equals(passwordSaved);
					if(!userOk){
						out.println("<p>That was not a valid user name / password. </p>");
						}
					}
				}
			

				if (userOk) {
					stmt.executeUpdate("Update users SET is_logged_in=1 where ID=" + id);
				}
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("here");
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return userOk;
	}



	/**
	 * Implementation of all input to the servlet. All post-messages are forwarded to this method. 
	 * 
	 * First logout the user, then check if he/she has provided a username and a password. 
	 * If he/she has, it is checked with the database and if it matches then the session state is 
	 * changed to login, the username that is saved in the session is updated, and the user is 
	 * relocated to the functionality page. 
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the session
		HttpSession session = request.getSession(true);
		int state;

		PrintWriter out = response.getWriter();
		out.println(getPageIntro());

		if (loggedIn(request)) {
			session.setAttribute("state", LOGIN_FALSE);
			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("Update users SET is_logged_in=0 where ID=" + session.getAttribute("id"));
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			out.println("<p>You are now logged out</p>");
		}

		String name;
		String password;
		String groupID;

		name = request.getParameter("user"); // get the string that the user entered in the form
		password = request.getParameter("password"); // get the entered password
		groupID = request.getParameter("groupID"); // get the entered password
		boolean groupExists = false;
		if (groupID != null) {
			int ID = Integer.parseInt(groupID);
			groupExists = checkGroup(ID);
		}

		if (name != null && password != null && groupExists) {
			if (checkUser(name, password, out)) {
				state = LOGIN_TRUE;
				session.setAttribute("state", state);  // save the state in the session
				session.setAttribute("groupID", groupID); // save the groupID in the session
				session.setAttribute("name", name);  // save the name in the session
				session.setAttribute("id", id); // save the userID in the session
				access.logInUser(id, session.getId());
				response.sendRedirect("Start");
			}
			else {
				//prints error message in checkUser
				out.println(loginRequestForm());
			}
		} else { // name was null, probably because no form has been filled out yet. Display form.
			out.println(loginRequestForm());
		}
		out.println("</body></html>");
	}

	private boolean checkGroup(int iD2) {
		try {
			Statement stmt = conn.createStatement();		    
			ResultSet rs = stmt.executeQuery("select * from user_group"); 
			while (rs.next()) {
				int currentID = rs.getInt("group_ID");
				if (currentID == iD2) {
					return true;
				}
			}
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("here");
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return false;
	}

	/**
	 * All requests are forwarded to the doGet method. 
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
