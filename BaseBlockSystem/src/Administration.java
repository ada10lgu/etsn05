

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Administration. 
 * Constructs a page for administration purpose. 
 * Checks first if the user is logged in and then if it is the administrator. 
 * If that is OK it displays all users and a form for adding new users.
 * 
 *  @author Martin Host
 *  @version 1.0
 */
@WebServlet("/Administration")
public class Administration extends servletBase {
	private static final long serialVersionUID = 1L;
	private static final int PASSWORD_LENGTH = 6;

	/**
	 * @see servletBase#servletBase()
	 */
	public Administration() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * generates a form for adding new users
	 * @return HTML code for the form
	 */
	private String addUserForm() {
		String html;
		html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p> Add user name: <input type=" + formElement("text") + " name=" + formElement("addname") + '>';    	
		html += "<input type=" + formElement("submit") + "value=" + formElement("Add user") + '>';
		html += "</form>";
		return html;
	}

	/**
	 * Checks if a username corresponds to the requirements for user names. 
	 * @param name The investigated username
	 * @return True if the username corresponds to the requirements
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

	/**
	 * Creates a random password.
	 * @return a randomly chosen password
	 */
	private String createPassword() {
		String result = "";
		Random r = new Random();
		for (int i=0; i<PASSWORD_LENGTH; i++)
			result += (char)(r.nextInt(26)+97); // 122-97+1=26
		return result;
	}


	/**
	 * Adds a user and a randomly generated password to the database.
	 * @param name Name to be added
	 * @return true if it was possible to add the name. False if it was not, e.g. 
	 * because the name already exist in the database. 
	 */
	private boolean addUser(String name) {
		boolean resultOk = true;
		try{
			Statement stmt = conn.createStatement();
			String statement = "insert into users (username, password,is_admin,is_project_leader,is_logged_in) values('" + name + "', '" + 
					createPassword() + "',"+0+","+0+","+0+")";
			stmt.executeUpdate(statement); 
			stmt.close();

		} catch (SQLException ex) {
			resultOk = false;
			// System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return resultOk;
	}

	/**
	 * Deletes a user from the database. 
	 * If the user does not exist in the database nothing happens. 
	 * @param name name of user to be deleted. 
	 */
	//Ändra beskrivningen ovan i STLDD


	private boolean deleteUser(int userID) {
		int groupId;
		try{
			Statement stmt = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			boolean removeGroup = false;
			ArrayList<Integer> groupsToRemove = new ArrayList<Integer>();
			//Check if the user is the only projectleader in any group
			ResultSet rs = stmt.executeQuery("Select * from user_group where user_id = " + userID);
			while(rs.next()){
				if(rs.getString("role").equals("Project Leader")){
					ResultSet groupMembers = stmt2.executeQuery("Select * from user_group where group_id = " + rs.getInt("group_id"));
					int countLeaders = 0;
					int countMembers = 0;
					while(groupMembers.next()){
						if(groupMembers.getString("role").equals("Project Leader")){
							countLeaders++;
						}
						countMembers++;
					}
					if(countLeaders == 1 && countMembers  > 1){		//The user is the only leader in at least one group
						return false;		
					} else if(countLeaders == 1 && countMembers  == 1){
						removeGroup = true;
						groupsToRemove.add(rs.getInt("group_id"));
					}
				}
			}

			//OK to remove, start with the time reports
			rs = stmt.executeQuery("Select * from user_group where user_id = " + userID);
			while(rs.next()){
				// Kanske en INNER JOIN på följande????????????????????????????
				int userGroupID = rs.getInt("id");
				ResultSet reports = stmt2.executeQuery("Select * from reports where user_group_id = " + userGroupID);
				while(reports.next()){
					Statement stmt3 = conn.createStatement();
					stmt3.executeUpdate("Delete from report_times where report_id = " + reports.getInt("id"));
				}
				stmt2.executeUpdate("Delete from reports where user_group_id = " + userGroupID);

			}
			stmt.executeUpdate("delete from user_group where user_id=" + userID); 
			stmt.executeUpdate("Delete from log where user_id = " + userID);
			if(removeGroup){
				for(int i=0;i<groupsToRemove.size();i++){
					stmt.executeUpdate("delete from groups where ID=" + groupsToRemove.get(i));
				}
			}
			int result= stmt.executeUpdate("delete from users where ID=" + userID);
			stmt.close();
			if(result==1){
				return true;
			}
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return false;
	}


	/**
	 * Handles input from the user and displays information for administration. 
	 * 
	 * First it is checked if the user is logged in and that it is the administrator. 
	 * If that is the case all users are listed in a table and then a form for adding new users is shown. 
	 * 
	 * Inputs are given with two HTTP input types: 
	 * addname: name to be added to the database (provided by the form)
	 * deletename: name to be deleted from the database (provided by the URLs in the table)
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		access.updateLog(null, null);
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu());
		String myName = "";
		HttpSession session = request.getSession(true);
		Object nameObj = session.getAttribute("name");
		if (nameObj != null)
			myName = (String)nameObj;  // if the name exists typecast the name to a string

		// check that the user is logged in
		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		}
		else
			if (myName.equals("admin")) {
				out.println("<h1>Administration page " + "</h1>");

				// check if the administrator wants to add a new user in the form
				String newName = request.getParameter("addname");
				if (newName != null) {
					if (checkNewName(newName)) {
						boolean addPossible = addUser(newName);
						if (!addPossible)
							out.println("<p>Error: Suggested user name not possible to add</p>");
					}	else
						out.println("<p>Error: Suggesten name not allowed</p>");
				}

				// check if the administrator wants to delete a user by clicking the URL in the list
				String deleteName = request.getParameter("deletename");
				if (deleteName != null) {
					if (checkNewName(deleteName)) {						
						String deleteidString = request.getParameter("deleteid");
						int deleteid=Integer.parseInt(deleteidString);
						System.out.println("id: "+deleteid);
						if(!deleteUser(deleteid)){
							out.println("<p>Error: Failed to remove user</p>");
						}
					}	
					else{
						out.println("<p>Error: URL wrong</p>");
					}
				}

				try {
					Statement stmt = conn.createStatement();		    
					ResultSet rs = stmt.executeQuery("select * from users order by username asc");
					out.println("<p>Registered users:</p>");
					out.println("<table border=" + formElement("1") + ">");
					out.println("<tr><td>NAME</td><td>PASSWORD</td><td></td></tr>");
					while (rs.next( )) {
						String name = rs.getString("username");
						String pw = rs.getString("password");
						int id=rs.getInt("ID");
						String deleteURL = "Administration?deletename="+name+"&deleteid="+id;
						String deleteCode = "<a href=" + formElement(deleteURL) +
								" onclick="+formElement("return confirm('Are you sure you want to delete "+name+"?')") + 
								"> delete </a>";
						if (name.equals("admin")) 
							deleteCode = "";
						out.println("<tr>");
						out.println("<td>" + name + "</td>");
						out.println("<td>" + pw + "</td>");
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
				out.println(addUserForm());

				out.println("<p><a href =" + formElement("Start") + "> Functionality selection page </p>");
				out.println("<p><a href =" + formElement("LogIn") + "> Log out </p>");
				out.println("</body></html>");
			} else  // name not admin
				response.sendRedirect("Start");
	}

	/**
	 *
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
