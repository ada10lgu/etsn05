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

@WebServlet("/ProjectLeader")
public class ProjectLeader extends servletBase {
	private static final long serialVersionUID = 1L;

	/**
	 * @see servletBase#servletBase()
	 */
	public ProjectLeader() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		access.updateLog(null, null);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu(request));
		
		String myName = "";
		HttpSession session = request.getSession(true);
		Object nameObj = session.getAttribute("name");
		Object groupObj = session.getAttribute("groupID");
		int groupID = Integer.parseInt((String) groupObj);
		if(request.getParameter("OK") != null){
			String newGroupIDStr = request.getParameter("SelectedGroupID");
			if (newGroupIDStr != null) {
				int newGroupID = Integer.parseInt(newGroupIDStr);
				if(newGroupID != 0){
					groupID = newGroupID;
					session.setAttribute("groupID", newGroupIDStr);
				}
			}
		}
		String role = request.getParameter("role");
		String username = request.getParameter("changename");

		if (nameObj != null) {
			myName = (String) nameObj; // if the name exists typecast the name
										// to a string
		}

		if (!loggedIn(request)) { //Check that user is logged in
			response.sendRedirect("LogIn");
		} else if (projectLeaderOrAdmin(myName)) { //Check that user is allowed
			out.println("<h1>Project Management " + "</h1>");
			if (username != null && !role.equals("0")) {
				if (myName.equals("admin")) {
					listAllGroups(out);
				}
				try {
					Statement stmt1 = conn.createStatement();		    
					ResultSet rs1 = stmt1.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.id where user_group.group_id = "+groupID+" and users.username = '"+username+"'");
					rs1.first();
					int userIDint = rs1.getInt("user_group.ID");
					if(!changeRole(userIDint, role)) {
						out.println("<p>User role change is not allowed.</p>");
					}
					showAllUsers(groupID, out);
				} catch (SQLException ex) {
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				}
			} else if (myName.equals("admin")){
				//SHOW LIST OF GROUPS AND USERS
				listAllGroups(out);
				if(groupID != 0){
					showAllUsers(groupID, out);
				}
			} else {
				showAllUsers(groupID, out);
			}
		} else {
			listAllProjectMembers(groupID, out);
		}
	}
	/**
	 * This function is not intended for the ProjectLeader but is instead performed if a regular users wants to list Group Members
	 * @param groupID
	 * @param out
	 */
	private void listAllProjectMembers(int groupID, PrintWriter out) {
		try {
			Statement s = conn.createStatement();		    
			ResultSet r = s.executeQuery("select * from groups where ID = "+groupID);
			String groupName = "";
			if (r.first()) {
				groupName = r.getString("name");
			}

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.ID where user_group.group_id = "
							+ groupID + "");
			out.println("<p><b>Users in "+groupName+"</b></p>");
			out.println("<table border=" + formElement("1") + ">");
			out.println("<tr><td>NAME</td><td>ROLE</td>");
			out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post") + ">");
			out.println("</tr>");
			while (rs.next( )) {

				String name = rs.getString("username");
				String role = rs.getString("role");
				out.println("<tr>");
				out.println("<td>" + name + "</td>");
				out.println("<td>" + role + "</td>");
				out.println("</tr>");
			}
			out.println("</table>");
			out.println("</form>");
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	private void listAllGroups(PrintWriter out){
		try {
			String html = "";
			html += "<p> <form name=" + formElement("input") + " method=" + formElement("post") + ">";
			html += "<br><select name='SelectedGroupID'>";
			html += "<option value='0' selected='true'>Select group: </option>";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from groups order by name asc");
			while(rs.next()){
				html += "<option value=" + rs.getInt("id") + ">"
						+ rs.getString("name") + "</option>";
			}
			html += "</select>";
			html += "<input type=" + formElement("submit") + " name='OK' value="+ formElement("OK") + '>';
			html += "</form>";
			out.println(html);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the user is either an admin or a Project Leader of the group
	 * 
	 * @param myName
	 * @return true if the user is admin/project leader, else false
	 */
	private boolean projectLeaderOrAdmin(String myName) {
		if (myName.equals("admin")) {
			return true;
		} else {
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt
						.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.ID where users.username = '"
								+ myName + "'");
				if (rs.first()) {
					String role = rs.getString("role");
					if (role.equals("Project Leader")) {
						return true;
					}
				}
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return false;
	}

	/**
	 * Displays a list of all the users in a group, their role and a button to
	 * change the role
	 * 
	 * @param ID
	 *            group ID
	 * @param out
	 *            PrintWriter
	 */
	
	private void showAllUsers(int groupID, PrintWriter out) {
		try {
			Statement s = conn.createStatement();		    
			ResultSet r = s.executeQuery("select * from groups where ID = "+groupID);
			String groupName = "";
			if (r.first()) {
				groupName = r.getString("name");
			}
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.ID where user_group.group_id = "
							+ groupID + "");
			if (groupName.equals("")) {
				out.println("<p><b>Choose a group to edit</b></p>");
			} else {
				out.println("<p><b>Users in "+groupName+"</b></p>");
				out.println("<table border=" + formElement("1") + ">");
				out.println("<tr><td>NAME</td><td>ROLE</td><td>Select user</td>");
				out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post") + ">");
				out.println("</tr>");
				while (rs.next( )) {

					String name = rs.getString("username");
					String role = rs.getString("role");
					out.println("<tr>");
					out.println("<td>" + name + "</td>");
					out.println("<td>" + role + "</td>");
					out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("changename") +
							" value=" + formElement(name) +"></td>");		//radiobutton
					out.println("</tr>");
				}
				out.println("</table>");
				out.println(selectRoleList());
				out.println("<p><input type=" + formElement("submit") + "value=" + formElement("Change role") + '>');
				out.println("</form>");
				stmt.close();
			}
		} catch (SQLException ex) {
			System.out.println("showAllUsers");
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	/**
	 * Returns html for a list with selection of roles.
	 * @return String: Html code
	 */
	private String selectRoleList(){
		String html = "";
		html += "<br><select name='role'>";
		html += "<option value='0' selected='true'>Select a role</option>";
		html += "<option value=" + formElement(PROJECT_LEADER) + ">"
				+ PROJECT_LEADER + "</option>";
		html += "<option value=" + formElement(t1) + ">"
				+ t1 + "</option>";
		html += "<option value=" + formElement(t2) + ">"
				+ t2 + "</option>";
		html += "<option value=" + formElement(t3) + ">"
				+ t3 + "</option>";
		html += "</select>";
		return html;
	}

	/**
	 * Changes the role of a user in the group
	 * 
	 * @param ID
	 *            the user to be changed
	 * @param role
	 *            the new role for the user
	 */
	private boolean changeRole(int userGroupID, String role) {
		boolean changeOK = false;
		
		try {
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery("select * from user_group where ID = "+userGroupID);
			rs2.first();
			int groupID = rs2.getInt("group_id");
			String currentRole = rs2.getString("role");
			System.out.println(role+ "in changeRole");
			Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery("select * from user_group where group_id = "+groupID);
			int roleCount = 0;
			int plCount = 0;
			while (rs1.next()) {
				if (rs1.getString("role").equals(PROJECT_LEADER)) {
					plCount++;
				} else if (rs1.getString("role").equals(role)){
					roleCount++;
				}
			}
			if (currentRole.equals(PROJECT_LEADER) && plCount == 1) {
				return false;
			} else {
				if ((role.equals(PROJECT_LEADER) && plCount < 2) || ((!role.equals(PROJECT_LEADER)) && roleCount < 6)) {
					Statement stmt = conn.createStatement();
					String statement = "Update user_group SET role='"+role+"' where ID=" + userGroupID; 
					stmt.executeUpdate(statement);
					changeOK = true;
				}
			}
			
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return changeOK;
	}

	/**
	 *
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}