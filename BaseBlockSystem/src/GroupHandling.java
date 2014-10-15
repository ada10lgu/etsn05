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
	 * 
	 * @param userID
	 * @param groupID
	 * @param role
	 * @return
	 */
	private boolean addUserToGroup(int userID, int groupID, PrintWriter out, HttpServletRequest request){
		boolean resultOk = true;
		String role = null;
		
		try{
			Statement stmt1 = conn.createStatement();		    
			ResultSet rs = stmt1.executeQuery("select * from user_group where group_id = '" + groupID + "'");
			if (!rs.first()) {
				role = "Project Leader";
			} else {
				role = request.getParameter("role");
			}
			
			if (role!=null) {
				if (!addAsRoleOk(userID, groupID, role)) {
					resultOk = false;
				}
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
	
	private boolean addAsRoleOk(int userID, int groupID, String role) throws SQLException {
		Statement stmt = conn.createStatement();		    
		ResultSet rs = stmt.executeQuery("select * from user_group where group_id = '" + groupID + "'");
		int total = 0, roleCounter = 0;
		boolean roleAlreadyAssigned = false;
		while (rs.next( )) {
			total++;
			if (rs.getInt("user_id") == userID) {
				roleAlreadyAssigned = true;
			}
			if (rs.getString("role").equals(role)) {
				roleCounter++;
			}
		}
		if (((role.equals("Project Leader") && roleCounter<2) || (!role.equals("Project Leader") && roleCounter<6)) && total < 20 && !roleAlreadyAssigned) {
			String statement = "insert into user_group (user_id, group_id, role) values('" + userID + "', '" + groupID + "', '" + role + "')";
			stmt.executeUpdate(statement);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param userID
	 * @param groupID
	 * @return
	 */
	private boolean removeUserFromGroup(int userID, int groupID){
		
		return false;
	}
	
	private void listUsers(PrintWriter out){
		try {
			
			Statement stmt = conn.createStatement();		    
		    ResultSet rs = stmt.executeQuery("select * from users order by username asc");
		    out.println("<p>Users:</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>NAME</td><td>ADD AS PG</td><td>ADD AS T1</td><td>ADD AS T2</td><td>ADD AS T3</td><td>REMOVE</td></tr>");
		    while (rs.next( )) {
		    	
		    	//THE FOLLOWING CODE IS UGLY AND WILL BE CHANGED
		    	String name = rs.getString("username");
		    	String addPG = "GroupHandling?addname="+name+"&role=Project Leader";
		    	String addT1 = "GroupHandling?addname="+name+"&role=t1";
		    	String addT2 = "GroupHandling?addname="+name+"&role=t2";
		    	String addT3 = "GroupHandling?addname="+name+"&role=t3";
		    	String removeURL = "GroupHandling?removename="+name;
		    	String addCodePG = "<a href=" + formElement(addPG) +
		    			            " onclick="+formElement("return confirm('Are you sure you want to add "+name+"?')") + 
		    			            "> add </a>";
		    	String addCodeT1 = "<a href=" + formElement(addT1) +
			            " onclick="+formElement("return confirm('Are you sure you want to add "+name+"?')") + 
			            "> add </a>";
		    	String addCodeT2 = "<a href=" + formElement(addT2) +
			            " onclick="+formElement("return confirm('Are you sure you want to add "+name+"?')") + 
			            "> add </a>";
		    	String addCodeT3 = "<a href=" + formElement(addT3) +
			            " onclick="+formElement("return confirm('Are you sure you want to add "+name+"?')") + 
			            "> add </a>";
		    	String removeCode = "<a href=" + formElement(removeURL) +
			            " onclick="+formElement("return confirm('Are you sure you want to remove "+name+"?')") + 
			            "> remove </a>";
		    	out.println("<tr>");
		    	out.println("<td>" + name + "</td>");
		    	out.println("<td>" + addCodePG + "</td>");
		    	out.println("<td>" + addCodeT1 + "</td>");
		    	out.println("<td>" + addCodeT2 + "</td>");
		    	out.println("<td>" + addCodeT3 + "</td>");
		    	out.println("<td>" + removeCode + "</td>");
		    	out.println("</tr>");
		    	
		    	//UGLY CODE END
		    }
		    out.println("</table>");
		    out.println("<input type=" + formElement("submit") + "value=" + formElement("OK") + '>');
		    stmt.close();
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
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
    	HttpSession session = request.getSession(true);
    	Object groupIDObject = session.getAttribute("groupHandlingID");
    	int groupID = (int)groupIDObject;
    	Object nameObj = session.getAttribute("name");
    	String myName = "";
    	if (nameObj != null)
    		myName = (String)nameObj;  // if the name exists typecast the name to a string
		
    	if (!loggedIn(request))
			response.sendRedirect("LogIn");
		else {
			if (myName.equals("admin")) {
				out.println("<h1>Group Handling " + "</h1>");
				try {
					String addToGroup = request.getParameter("addname");
					if (addToGroup != null) {
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery("select * from users where username = '"+addToGroup+"'");
						if (rs.first()) {
							int userID = rs.getInt("id");
							if (!addUserToGroup(userID, groupID, out, request)) {
								out.println("User was not added to group");
							} else {
								out.println("User was successfully added to group");
							}
						} else {
							System.out.println("NO SUCH NAME");
						}
					}
				} catch (SQLException ex) {
					// System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}
				try {
					String removeFromGroup = request.getParameter("removename");
					if (removeFromGroup != null) {
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery("select * from users where name = '"+removeFromGroup+"'");
						int userID = rs.getInt("ID");
						removeUserFromGroup(userID, groupID);
					}
				} catch (SQLException ex) {
					// System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}
				listUsers(out);
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