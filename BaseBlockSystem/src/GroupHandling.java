import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/GroupHandling") 
public class GroupHandling extends servletBase {

	/**
	 * @see servletBase#servletBase()
	 */
	public GroupHandling(){
		super();
	}

	/**
	 * Adds a user with a role to a group.
	 * @param userID: The id of the user who will be added to the group.
	 * @param groupID: The id of the group to which the user will be added to.
	 *
	 * @param request: 
	 * @return boolean: True if the user was successfully added.
	 */
	private boolean addUserToGroup(int userID, int groupID, HttpServletRequest request){
		boolean resultOk = true;
		String role = null;

		try{
			Statement stmt1 = conn.createStatement();		    
			ResultSet rs = stmt1.executeQuery("select * from user_group where group_id = '" + groupID + "'");
			if (!rs.first()) {
				role = PROJECT_LEADER;
			} else {
				role = request.getParameter("role");
			}

			if (!role.equals("0")) {
				resultOk = addAsRoleOk(userID, groupID, role);
			} else {
				resultOk = false;
			}
		} catch (SQLException ex) {
			resultOk = false;
			// System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return resultOk;
	}

	/**
	 * Checks if it's ok to add a user to a group and if it's ok the user is added
	 * @param userID: The id of the user who will be added to the group.
	 * @param groupID: The id of the group to which the user will be added to.
	 * @param role: The role the user will have in the group.
	 * @return boolean: true if the user was successfully added
	 * @throws SQLException
	 */
	private boolean addAsRoleOk(int userID, int groupID, String role) throws SQLException {
		Statement stmt = conn.createStatement();		    
		ResultSet rs = stmt.executeQuery("select * from user_group where group_id = '" + groupID + "'");
		int total = 0, roleCounter = 0;
		boolean roleAlreadyAssigned = false;
		while (rs.next( )) {
			total++;
			/*if (rs.getInt("user_id") == userID) {//Kollar om användaren redan finns med i gruppen men vi har ändrat så att bara användare utanför gruppen listas
				roleAlreadyAssigned = true;
			}*/
			if (rs.getString("role").equals(role)) {
				roleCounter++;
			}
		}
		if (((role.equals(PROJECT_LEADER) && roleCounter<2) || (!role.equals(PROJECT_LEADER) && roleCounter<6)) && total < 20 && !roleAlreadyAssigned) {
			String statement = "insert into user_group (user_id, group_id, role) values('" + userID + "', '" + groupID + "', '" + role + "')";
			stmt.executeUpdate(statement);
			return true;
		}
		return false;
	}

	/**
	 * Removes the user from the group.
	 * @param userID: The id of the user who will be removed from the group.
	 * @param groupID: The id of the group to which the user will be removed from.
	 * @return boolean: True if the user was successfully removed.
	 */
	private boolean removeUserFromGroup(int userID, int groupID){
		boolean resultOk = true;
		int projectLeaderCounter = 0; 
		boolean isProjectLeader = false;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rsRoleOfUser = stmt.executeQuery("select * from user_group where group_id = '" + groupID + "'" + " and user_id = '" + userID + "'");
			if(rsRoleOfUser.first()){
				if(rsRoleOfUser.getString("role").equals(PROJECT_LEADER)){
					isProjectLeader = true;
					ResultSet rs = stmt.executeQuery("select * from user_group where group_id = '" + groupID + "'");
					while(rs.next()){
						if(rs.getString("role").equals(PROJECT_LEADER)){
							projectLeaderCounter++;
						}
					}
				}
			}
			if(!(isProjectLeader && projectLeaderCounter < 2)){
				String statement = "delete from user_group where user_id=" + userID +  " and group_id=" + groupID;
				int result = stmt.executeUpdate(statement); 
				stmt.close();
				if(result != 1){
					resultOk = false;
				}
			}else{
				//Man försöker ta bort en projektledare och antalet PL < 2
				resultOk = false;
			}
		} catch (SQLException ex) {
			resultOk = false;
			// System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		return resultOk;
	}

	/**
	 * Lists all users
	 * @param out: needed for printing
	 * @param groupID: don't print already added users
	 * @param inGroup: check if you want to list users inside of group
	 */
	private void listUsers(PrintWriter out, int groupID, boolean inGroup){
		try {
			out.println("<div class='floati'>");
			Statement stmt = conn.createStatement();
			ResultSet rsgrp = stmt.executeQuery("select * from groups where id=" + groupID);
			rsgrp.first();
			String groupName = rsgrp.getString("name");
			stmt = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from users order by username asc");
			if(inGroup){
				out.println("<p>Remove user from: " + groupName + "</p>");
				out.println("<table border=" + formElement("1") + ">");
				out.println("<tr><td>Username</td><td>Role</td><td></td></tr>");
			}else{
				out.println("<p>Add user to: " + groupName + "</p>");
				out.println("<table border=" + formElement("1") + ">");
				out.println("<tr><td>Username</td><td>Select user</td></tr>");
			}

			out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post"));
			while (rs.next( )) {
				ResultSet rsGroup = stmt2.executeQuery("select * from user_group where user_id=" + 
								rs.getInt("id") + " AND group_id=" + groupID); //om detta != null så finns redan användaren i gruppen
				String name = rs.getString("username");
				boolean print = false;
				if(inGroup){
					print = rsGroup.first();
				} else {
					print = !rsGroup.first();
				}
				if(!rs.getString("username").equals(ADMIN) && print){
					out.println("<tr>");
					out.println("<td>" + name + "</td>");
					String userID = "" + rs.getInt("id");
					if(inGroup){
						out.println("<td>" + rsGroup.getString("role") + "</td>");
						String deleteURL = "GroupHandling?deletename="+userID;
				    	String deleteCode = "<a href=" + formElement(deleteURL) +
				    			            " onclick="+formElement("return confirm('Are you sure you want to delete "+name+" from " + groupName+"?')") + 
				    			            "> delete </a>";
				    	out.println("<td>" + deleteCode + "</td>");
					}else{
						out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("selectedradiouser") +
								" value=" + formElement(userID) +"></td>");		//radiobutton
					}
					out.println("</tr>");
				}
			}
			out.println("</table>");	
			if(!inGroup){
				out.println(selectRoleList());
				out.println("<p><input type=" + formElement("submit") + "value=" + formElement("Add user") + '>');
			}
			out.println("</form>");
			out.println("</div>");
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	/**
	 * List all roles
	 * @return The html-string for a drop down list of all roles
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
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Handles input from the user and displays information for project group administration. 
	 * 
	 * First it is checked if the user is logged in and that it is the administrator. 
	 * If that is the case all project groups are listed in a table and then a form for adding new projects
	 *  is shown. 
	 * 
	 * Inputs are given with two HTTP input types: 
	 * projectname: name to be added to the database (provided by the form)
	 * deletename: name to be deleted from the database (provided by the URLs in the table)
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		access.updateLog(null, null);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu(request));
		HttpSession session = request.getSession(true);
		Object groupIDObject = session.getAttribute("groupHandlingID");
		int groupID = (int)groupIDObject;
		Object nameObj = session.getAttribute("name");
		String myName = "";
		String op = request.getParameter("operation");
		if (nameObj != null) {
			myName = (String)nameObj;  // if the name exists typecast the name to a string
		}
		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		} else {
			if (myName.equals(ADMIN)) {
				out.println("<h1>Group Handling " + "</h1>");
				
				//Add user to group (LEFT TABLE)
				String userIdString = request.getParameter("selectedradiouser");
				if (userIdString != null) {
					int userID = Integer.parseInt(userIdString);
					if (!addUserToGroup(userID, groupID, request)) {
						response.sendRedirect("GroupHandling?operation=FailedAdd");
					} else {
						response.sendRedirect("GroupHandling?operation=Added");
					}
				}
				//Remove user from group (RIGHT TABLE)
				String deleteName = request.getParameter("deletename");
				if (deleteName != null) {
					int userID = Integer.parseInt(deleteName);
					boolean removed = removeUserFromGroup(userID, groupID);
					if(removed){
						response.sendRedirect("GroupHandling?operation=Removed");
					}else{
						response.sendRedirect("GroupHandling?operation=FailedRemoved");
					}
				}
				listUsers(out, groupID, false);//Print users outside of the group
				listUsers(out, groupID, true);//Print users already in the group
				if(op !=null){
					out.println("<div class='floati'>");
					switch(op){
						case "FailedAdd": out.println("User was not added, please make sure<br>that you have selected a role and that<br>the limitations have not been reached.");
						break;
						case "Added": out.println("User was added");
						break;
						case "Removed": out.println("User was removed ");
						break;
						case "FailedRemoved": out.println("User was not removed because there<br>is only one project leader left.");
						break;						
					}
					out.println("</div>");
				}
			}
		}
	}
}
	/*
+"<li><a href=" + formElement("Administration") + ">Administration</a>"
+ "<ul>"
+ "<li><a href=" + formElement("Administration") + ">Users</a></li>"
+ "<li><a href= " + formElement("ProjectGroupAdmin") + ">Group</a></li>"
	 */
