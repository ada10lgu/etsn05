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


@WebServlet("/ProjectGroupAdmin") 
public class ProjectGroupAdmin extends servletBase {
	boolean addUser;
	
	/**
	 * @see servletBase#servletBase()
	 */
	public ProjectGroupAdmin(){
		super();
	}
	
	/**
	 * Constructs a form for adding project groups
	 * @return String: The html-code for constructing the form 
	 */
	private String addProjectForm(){
		String html;
    	html = "<p> <form name=" + formElement("input");
    	html += " method=" + formElement("post");
    	html += "<p> Project name: <input type=" + formElement("text") + " name=" + formElement("projectname") + '>';
    	html += "<input type='hidden' value='checkGroup' name='function'>";
    	html += "<input type=" + formElement("submit") + "value=" + formElement("Add project") + '>';
    	html += "</form>";
    	return html;
	}
	
	/**
	 * Lists all users
	 * @param out: needed for printing
	 * @param groupID: don't print already added users
	 * @param inGroup: check if you want to list users inside of group
	 */
	private void listUsers(PrintWriter out, String newGroupName){
		try {
			out.println("<div>");
			Statement stmt = conn.createStatement();
			stmt = conn.createStatement();			
			ResultSet rs = stmt.executeQuery("select * from users order by username asc");
			out.println("<p>Add projectleader to: " + newGroupName + "</p>");
			out.println("<form name=" + formElement("input") + " method=" + formElement("post") + ">");
			out.println("<table border=" + formElement("1") + ">");
			out.println("<tr><td>Username</td><td>Select user</td></tr>");		
			while (rs.next( )) {				
				String name = rs.getString("username");						
				if(!rs.getString("username").equals(ADMIN)){
					out.println("<tr>");
					out.println("<td>" + name + "</td>");
					String userID = "" + rs.getInt("id");
					
					out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("selectedradiouser") +
								" value=" + formElement(userID) +"></td>");		//radiobutton
					
					out.println("</tr>");
				}
			}
			out.println("</table>");			
			out.println("<input type='hidden' value='"+ newGroupName +"' name='groupName'>");
			out.println("<input type='hidden' value='addUserAndGroup' name='function'>");
			out.println("<input type=" + formElement("submit") + "value=" + formElement("Add user") + '>');			
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
	 * Adds a new project group. Has to be followed by a successful call of addUserToGroup(userID,groupID, 
	 * role) with role = project leader.
	 * @param name: The name of the new group
	 * @return boolean: True if the project is added successfully
	 */
	private int addProject(String name) {
		int resultOk = -1;
		try{			
			Statement stmt = conn.createStatement();
			String statement = "insert into groups (name) values('" + name + "')";
			stmt.executeUpdate(statement);
			ResultSet rs = stmt.executeQuery("select * from groups where name = '"+name+"'");
			if (rs.first()) {
				resultOk = rs.getInt("id");
			} 
			stmt.close();

		} catch (SQLException ex) {
			resultOk = -1;
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return resultOk;
	}
	
	/**
	 * Checks and make sures there are more than admin in the system so that a group can be added
	 * @return True if more than admin exist in system
	 */
	private boolean usersInSystem(){
		try{
			ResultSet rs = conn.createStatement().executeQuery("select COUNT(*) AS total from users");			
			rs.first();
			int nbrOfUsers = rs.getInt("total");
			if(nbrOfUsers > 1){ //check if there are more users than just admin
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}				
	}
	
	/**
	 * Checks if it's ok to add a user to a group and if it's ok the user is added
	 * @param userID: The id of the user who will be added to the group.
	 * @param groupID: The id of the group to which the user will be added to.
	 * @param role: The role the user will have in the group.
	 * @return boolean: true if the user was successfully added
	 * @throws SQLException
	 */
	private boolean addAsRoleOk(String userID, int groupID, String role) throws SQLException {
		Statement stmt = conn.createStatement();		    
		ResultSet rs = stmt.executeQuery("select * from user_group where group_id = '" + groupID + "'");
		int total = 0, roleCounter = 0;
		boolean roleAlreadyAssigned = false;
		while (rs.next( )) {
			total++;
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
	 * Deletes a project group
	 * @param projectID: The id of the project group which will be deleted
	 * @return boolean: True if the project is deleted successfully
	 * @throws SQLException 
	 */
	private boolean deleteProject(int projectID) {
		boolean resultOk = true;
		try{
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select reports.id from reports INNER JOIN user_group on reports.user_group_id = user_group.id"
								+ " where user_group.group_id=" + projectID);
			while(rs.next()){
				conn.createStatement().executeUpdate("Delete from report_times where report_id=" + rs.getInt("reports.id"));
				conn.createStatement().executeUpdate("Delete from reports where id=" + rs.getInt("reports.id"));
			}
			String statement = "delete from user_group where group_id=" + projectID;
			conn.createStatement().executeUpdate(statement); 
			statement = "delete from groups where id=" + projectID;
			int result = conn.createStatement().executeUpdate(statement); 
			if(result != 1){
				resultOk = false;
			}
			stmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return resultOk;
	}

	/**
     * Checks if a project name corresponds to the requirements for project names. 
     * @param name The investigated project name
     * @return True if the project name corresponds to the requirements
     */
	private boolean checkNewName(String name) {
		//check if the same name exist in db
		try {
			ResultSet rs = conn.createStatement().executeQuery("select COUNT(*) as total from groups where name = '" + name +"'");
			if(rs.first()){
				int count = rs.getInt("total");
				if(count == 1){
					return false; //exist another group with the same name
				}
			}
		} catch (SQLException e) {			
		}
		int length = name.length();
		boolean ok = (length>=5 && length<=10);
		if (ok)
			for (int i=0; i<length; i++) {
				int ci = (int)name.charAt(i);
				boolean thisOk = ((ci>=48 && ci<=57) || 
						(ci>=65 && ci<=90) ||
						(ci>=97 && ci<=122));
				ok = ok && thisOk;
			}    	
		return ok;
	}
	/**
	 * Lists all groups.
	 * @param out: PrintWriter object needed for print outs.
	 */
	private void listGroups(PrintWriter out) {
		try {
			Statement stmt = conn.createStatement();	
			Statement stmt2 = conn.createStatement();
			Statement stmt3 = conn.createStatement();
		    ResultSet rs = stmt.executeQuery("select * from groups order by name asc");
		    out.println("<p>Project groups:</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>NAME</td><td>Projectleader 1</td><td>Projectleader 2</td><td></td><td></td></tr>");
		    while (rs.next()) {
		    	String name = rs.getString("name");
		    	ResultSet rsGroup = stmt2.executeQuery("select * from groups where name = '" + name + "'");
		    	rsGroup.first();
		    	int groupID = rsGroup.getInt("id");
		    	ResultSet rsPL = stmt3.executeQuery("select users.id, users.username from user_group inner join users on user_group.user_id = users.id where user_group.group_id = " + groupID + " and user_group.role = " + formElement(PROJECT_LEADER)); 
		   
		    	String[] projectLeaders = {"", ""};
		    	int i = 0;
		    	while(rsPL.next()){
		    		projectLeaders[i] = rsPL.getString("username");
		    		i++;
		    	}
		    	
		    	
		    	ResultSet rsUsersInGroup = stmt2.executeQuery("select * from user_group where group_id = '" + groupID + "'");
		    	while(rsUsersInGroup.next()){
		    		String role = rsUsersInGroup.getString("role");
		    	}		    	
		    	String deleteURL = "ProjectGroupAdmin?deletename="+name;
		    	String deleteCode = "<a href=" + formElement(deleteURL) +
		    			            " onclick="+formElement("return confirm('Are you sure you want to delete "+name+"?')") + 
		    			            "> delete </a>";
		    	String editURL = "ProjectGroupAdmin?editid="+groupID;
		    	String editCode = "<a href=" + formElement(editURL) +
		    			            " onclick="+formElement("return confirm('Are you sure you want to edit "+name+"?')") + 
		    			            "> edit </a>";
		    	out.println("<tr>");
		    	out.println("<td>" + name + "</td>");
		    	out.println("<td>" + projectLeaders[0] + "</td>");
		    	out.println("<td>" + projectLeaders[1] + "</td>");
		    	out.println("<td>" + editCode + "</td>");
		    	out.println("<td>" + deleteCode + "</td>");
		    	out.println("</tr>");
		    }
		    out.println("</table>");
		    stmt.close();
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		out.println(addProjectForm());
	}
	
	private boolean checkGroupExist(String groupID){
		try {
			ResultSet rs = conn.createStatement().executeQuery("select COUNT(*) as total from groups where id = " + groupID);
			if(rs.first()){
				int count = rs.getInt("total");
				if(count == 1){
					return true; //exist another group with the same name
				}
			}
		} catch (SQLException e) {	
			return false;
		}
		return false;
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
		out.println("<div class='floati'>");
		String myName = "";
    	HttpSession session = request.getSession(true);
    	Object nameObj = session.getAttribute("name");
    	if (nameObj != null)
    		myName = (String)nameObj;  // if the name exists typecast the name to a string
		
		// check that the user is logged in
		if (!loggedIn(request))
			response.sendRedirect("LogIn");
		else
			if (myName.equals(ADMIN)) {
				out.println("<h1>Project group administration page " + "</h1>");
				
				// check if the administrator wants to add a new project group in the form
					
				String function = request.getParameter("function");
				if(function != null){					
					if(function.equals("checkGroup")){
						String newName = request.getParameter("projectname");	
						if (newName != null) {
							int nbrOfGroups = 0;
							try {
								Statement stmt = conn.createStatement();
								ResultSet rs = stmt.executeQuery("select count(*) AS total from groups");
								rs.first();
								nbrOfGroups = rs.getInt("total");
							} catch (SQLException e) {
							}
							if (nbrOfGroups < 5){
								if(usersInSystem()){
									if (checkNewName(newName)) {
										listUsers(out, newName); //print the user add form
										//response.sendRedirect("ProjectGroupAdmin?function=selectUsers&groupName=" + newName);
									} else {
										out.println("<p>Error: Suggested name not allowed</p>");
									}
								}else{
									out.println("<p>Error: There are no users in the system.</p>");
								}
							} else{
								out.println("<p>Error: The system does not allow more groups than 5.</p>");
							}						
						}
					}else if(function.equals("addUserAndGroup")){						
						String newGroupName = request.getParameter("groupName");
						if(newGroupName != null){
							//start by creating the group
							//then add the user to the group
							//if the user wasn't added remove the group and go to start
							//if the user was added, redirect to groupHandling
							String userIdString = request.getParameter("selectedradiouser");							
							if (userIdString != null) {
								int addPossible = addProject(newGroupName);
								if (addPossible == -1) {
									out.println("<p>Error: Suggested project group name not possible to add</p>");							
								}else{									
									try {
										if(addAsRoleOk(userIdString, addPossible, PROJECT_LEADER)){
											session.setAttribute("groupHandlingID", addPossible);
											response.sendRedirect("GroupHandling");	
										}else{
											deleteProject(addPossible);
											response.sendRedirect("projectGroupAdmin?ERROR=true");
										}
									} catch (SQLException e) {										
									}									
								}
							}							
						}
					}
				}
				//check if the administrator wants to delete a project by clicking the URL in the list
				String deleteName = request.getParameter("deletename");
				if (deleteName != null) {
					Statement stmt;
					try {
						stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery("select * from groups where name = '" + deleteName + "'");
						if(rs.first()){
							int deleteID = rs.getInt("id");
							if(deleteProject(deleteID)){
								out.println("Project group was successfully removed");
							}else{
								out.println("Project group was not removed");
							}
						}		
					} catch (SQLException ex) {
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
					}					
				}
				String editIDString = request.getParameter("editid");
				if(editIDString != null){
					int editID = Integer.parseInt(editIDString);
					if(checkGroupExist(editIDString)){
						session.setAttribute("groupHandlingID", editID);
						response.sendRedirect("GroupHandling");
					}else{
						out.println("<p>Error: The group you are trying to edit does no exist.</p>");
					}							
				}
				listGroups(out);
				out.println("</div>");
				out.println("</body></html>");
			} else  // name not admin
				response.sendRedirect("Start");
	}

	
}
