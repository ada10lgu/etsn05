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
 * The first thing that happens is that the user is logged out if he/she is
 * logged in. Then the user is asked for name and password. If the user is
 * logged in he/she is directed to the functionality page.
 * 
 * @author Martin Host
 * @version 1.0
 * 
 */
@WebServlet("/LogIn")
public class LogIn extends servletBase {
	private HttpSession session;
	static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogIn() {
		super();
	}

	/**
	 * Generates a form for login.
	 * 
	 * @return HTML code for the form
	 */
	protected String loginRequestForm() {
		String html = "<p>Please enter your name and password in order to log in:</p>";
		html += "<p> <form name=" + formElement("input");
		html += " method=" + formElement("post");
		html += "<p> Name: <input type=" + formElement("text") + " name="
				+ formElement("user") + '>';
		html += "<p> Password: <input type=" + formElement("password")
				+ " name=" + formElement("password") + '>';
		html += "<br><select name='groupID'>";
		html += "<option value='0' selected='true'>Select a group</option>";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from groups");
			while (rs.next()) {
				html += "<option value=" + rs.getInt("id") + ">"
						+ rs.getString("name") + "</option>";
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		html += "</select>";
		// html += "<p> Group: <input type=" + formElement("text") + " name=" +
		// formElement("groupID") + '>';
		html += "<p> <input type=" + formElement("submit") + "value="
				+ formElement("Submit") + '>';
		html += "</form>";
		return html;
	}

	/**
	 * Checks with the database if the user should be accepted based on user
	 * name, password and if the user is a member of the specified project
	 * group.
	 * 
	 * @param name
	 *            The name of the user
	 * @param password
	 *            The password of the user
	 * @param groupID
	 *            : The id of the project group
	 * @return true if the user should be accepted
	 */
	private boolean checkUser(String name, String password, String groupID,
			PrintWriter out) {

		boolean userOk = false;		
		Statement stmt;
		try {
			if (name != null && password != null && groupID != null) {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from users where username = "+ formElement(name) + " and password = "+ formElement(password));
				int userID = -1;
				if (rs.first()) {
					userID = rs.getInt("ID");					
					if (checkGroup(groupID, userID, name)) { 
						userOk = true;
					}
				}				
				stmt.close();
				if (userOk) { // if the user is accepted, save the session variables
					session.setAttribute("session", session.getId());
					session.setAttribute("name", name);
					session.setAttribute("userID", userID);
					// userGroupID is saved in session inside method checkGroup()
				} else {
					out.println("<p>That was not a valid user name / password. </p>");
				}
				stmt.close();
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return userOk;
	}

	/**
	 * This method checks if a user is a member of a specific project group, if
	 * so it sets the sessionAttribute userGroupID.
	 * 
	 * @param groupID
	 *            : The id of the project group.
	 * @param userID
	 *            : The id of the user.
	 * @return True if the user is a member of the group.
	 */
	private boolean checkGroup(String groupIDstr, int userID, String name) {
		boolean groupOK = false;
		Statement stmt;

		try {
			if (name.equals("admin")) {
				session.setAttribute("role", "admin");
				session.setAttribute("userGroupID", "0");
				groupOK = true;
			}else{
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from user_group where user_id="+ userID + " and group_id = " + groupIDstr);
				if (rs.first()) {
					session.setAttribute("userGroupID", rs.getInt("ID")); // save userGroupID in session
					session.setAttribute("role", rs.getString("role"));
					groupOK = true;
				}
				stmt.close();
			}
			
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return groupOK;
	}

	/**
	 * Implementation of all input to the servlet. All post-messages are
	 * forwarded to this method.
	 * 
	 * First logout the user, then check if he/she has provided a username and a
	 * password. If he/she has, it is checked with the database and if it
	 * matches then the session state is changed to login, the username that is
	 * saved in the session is updated, and the user is relocated to the
	 * functionality page.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String name;
		String password;
		String groupID;
		
		access.updateLog(null, null); // check timestamps
		
		session = request.getSession(true); // get session
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		
		// Om användaren är inloggad så, logga ut.
		if (loggedIn(request)) {
			session.setAttribute("state", LOGIN_FALSE);
			access.logOutUser((Integer) session.getAttribute("userID"),
					session.getId());
			out.println("<p>You are now logged out</p>");
		}

		name = request.getParameter("user"); // get the string that the user entered in the form
		password = request.getParameter("password"); // get the entered password
		groupID = request.getParameter("groupID"); // get the group id of the selected group

		if (name != null && password != null && groupID != null) {			
			// Check if user exists, has correct password and is member of the group. Saves session attributes if true.
			if (checkUser(name, password, groupID, out)) {
				if (!access.updateLog((int) session.getAttribute("userID"),
						session.getId())) { // logged out or inactive for over 20min
					out.println("We got stuff from user");
					//Good to go --> LOGIN!
					access.logInUser((int) session.getAttribute("userID"),session.getId());
					session.setAttribute("state", LOGIN_TRUE);
					response.sendRedirect("Start");
				}else{
					out.println("Unable to log in.");
					out.println(loginRequestForm());
				}
			} else {
				// prints error message in checkUser
				out.println(loginRequestForm());
			}
		} else { // name was null, probably because no form has been filled out yet. Display form.
			out.println(loginRequestForm());
		}

		out.println("</body></html>");
	}

	/**
	 * All requests are forwarded to the doGet method.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
