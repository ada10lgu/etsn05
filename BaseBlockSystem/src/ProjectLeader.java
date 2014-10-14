import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Random;

/**
 * Servlet implementation class Administration. Constructs a page for
 * administration purpose. Checks first if the user is logged in and then if it
 * is the administrator. If that is OK it displays all users and a form for
 * adding new users.
 * 
 * @author Martin Host
 * @version 1.0
 */

@WebServlet("/ProjectLeader")
public class ProjectLeader extends servletBase {
	private static final long serialVersionUID = 1L;

	/**
	 * @see servletBase#servletBase()
	 */
	public ProjectLeader() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		String myName = "";
		HttpSession session = request.getSession(true);
		//WHICH OF THESE NEED TO BE FETCHED?
		Object nameObj = session.getAttribute("name");
		Object groupObj = session.getAttribute("groupID");
		String groupID = (String)groupObj;
		
		String role = request.getParameter("role");
		String userID = request.getParameter("userID");
		//String groupID = request.getParameter("groupID");
		
		if (nameObj != null) {
			myName = (String)nameObj;  // if the name exists typecast the name to a string
		}
		// check that the user is logged in
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		} else {
			out.println("<h1>Project Management " + "</h1>");
			if (userID != null) {
				int ID = Integer.parseInt(userID);
				changeRole(ID, role);
			} else {
				int ID = Integer.parseInt(groupID);
				showAllUsers(ID, out);
			}
		}
	}

		private void showAllUsers(int ID, PrintWriter out) {
			try {
				Statement stmt = conn.createStatement();		    
				ResultSet rs = stmt.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.ID where user_group.group_id = '"+ID+"'");
				out.println("<p>Project users:</p>");
				out.println("<table border=" + formElement("1") + ">");
				out.println("<tr><td>NAME</td><td>ROLE</td><td></td></tr>");
				while (rs.next( )) {
					String name = rs.getString("username");
					String role = rs.getString("role");
					System.out.println(name+role);
					int id=rs.getInt("ID");
					String changeRoleURL = "ProjectLeader?changeRole="+role+"username="+name;
					String roleCode = "<a href=" + formElement(changeRoleURL) +"> Change Role </a>";
					//NO BUTTON FOR PROJECT LEADERS
					out.println("<tr>");
					out.println("<td>" + name + "</td>");
					out.println("<td>" + role + "</td>");
					out.println("<td>" + roleCode + "</td>");
					out.println("</tr>");
				}
				out.println("</table>");
				stmt.close();
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}

		private void changeRole(int ID, String role) {
			// TODO Auto-generated method stub

		}

		/**
		 *
		 */
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			// TODO Auto-generated method stub
		}

	}