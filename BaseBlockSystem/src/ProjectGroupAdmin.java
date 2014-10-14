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
    	html += "<input type=" + formElement("submit") + "value=" + formElement("Add project") + '>';
    	html += "</form>";
    	return html;
	}

	/**
	 * Adds a new project group. Has to be followed by a successful call of addUserToGroup(userID,groupID, 
	 * role) with role = project leader.
	 * @param name: The name of the new group
	 * @return boolean: True if the project is added successfully
	 */
	private int addProject(String name) {
		int resultOk = -1;
		
	
		//Detta ska komma efter att man har valt en projektledare
		try{
			Statement stmt = conn.createStatement();
			String statement = "insert into groups (name) values('" + name + "')";
			stmt.executeUpdate(statement);
			ResultSet rs = stmt.executeQuery("select * from groups where name = '"+name+"'");
			if (rs.first()) {
				resultOk = rs.getInt("id");
			} else {
				System.out.println("NO SUCH ID");
			}
			stmt.close();

		} catch (SQLException ex) {
			resultOk = -1;
			// System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return resultOk;
	}
	
	/**
	 * Deletes a project group
	 * @param projectID: The id of the project group which will be deleted
	 * @return boolean: True if the project is deleted successfully
	 */
	private boolean deleteProject(int projectID) {
		return false;
	}

	/**
     * Checks if a project name corresponds to the requirements for project names. 
     * @param name The investigated project name
     * @return True if the project name corresponds to the requirements
     */
	private boolean checkNewName(String name) {
		int length = name.length();
		boolean ok = (length>=5 && length<=10);
		if (ok)
			for (int i=0; i<length; i++) {
				int ci = (int)name.charAt(i);
				boolean thisOk = ((ci>=48 && ci<=57) || 
						(ci>=65 && ci<=90) ||
						(ci>=97 && ci<=122));
				//String extra = (thisOk ? "OK" : "notOK");
				//System.out.println("bokst:" + name.charAt(i) + " " + (int)name.charAt(i) + " " + extra);
				ok = ok && thisOk;
			}    	
		return ok;
	}
	
	private void listGroups(PrintWriter out) {
		//boolean resultOk = false;
		try {
			Statement stmt = conn.createStatement();		    
		    ResultSet rs = stmt.executeQuery("select * from groups order by name asc");
		    out.println("<p>Project groups:</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>NAME</td><td>Projectleader 1</td><td>Projectleader 2</td><td></td></tr>");
		    while (rs.next( )) {
		    	String name = rs.getString("name");
		    	//Hämta projektledarnas namn
		    	String deleteURL = "ProjectGroupAdmin?deletename="+name;//osäker på denna raden
		    	String deleteCode = "<a href=" + formElement(deleteURL) +
		    			            " onclick="+formElement("return confirm('Are you sure you want to delete "+name+"?')") + 
		    			            "> delete </a>";
		    	if (name.equals("admin")) 
		    		deleteCode = "";
		    	out.println("<tr>");
		    	out.println("<td>" + name + "</td>");
		    	out.println("<td>" + " " + "</td>");
		    	out.println("<td>" + " " + "</td>");
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
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		
		String myName = "";
    	HttpSession session = request.getSession(true);
    	Object nameObj = session.getAttribute("name");
    	if (nameObj != null)
    		myName = (String)nameObj;  // if the name exists typecast the name to a string
		
		// check that the user is logged in
		if (!loggedIn(request))
			response.sendRedirect("LogIn");
		else
			if (myName.equals("admin")) {
				out.println("<h1>Project group administration page " + "</h1>");
				
				// check if the administrator wants to add a new project group in the form
				String newName = request.getParameter("projectname");
				if (newName != null) {
					if (checkNewName(newName)) {
						int addPossible = addProject(newName);
						if (addPossible == -1) {
							out.println("<p>Error: Suggested project group name not possible to add</p>");
						} else {
							session.setAttribute("groupHandlingID", addPossible);
							response.sendRedirect("GroupHandling");
						}
						
					}	else
						out.println("<p>Error: Suggested name not allowed</p>");
				}
					
				// check if the administrator wants to delete a project by clicking the URL in the list
				/*String deleteName = request.getParameter("deletename");
				if (deleteName != null) {
					if (checkNewName(deleteName)) {
						//deleteProject(deleteName); Hämta id för gruppen vars namn är deleteName
					}	else
						out.println("<p>Error: URL wrong</p>");
				}*/
				
				else {
					listGroups(out);
				}
				
				out.println("<p><a href =" + formElement("Start") + "> Start page </p>");
				out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
				out.println("</body></html>");
			} else  // name not admin
				response.sendRedirect("Start");
	}

	
}
