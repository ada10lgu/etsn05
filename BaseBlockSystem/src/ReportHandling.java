import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet("/ReportHandling")
public class ReportHandling extends servletBase{
	
	private PrintWriter out;
	private HttpSession session;
	private String function = null;
	
	
	public ReportHandling(){
		super();
	}

	private String signString(int signed){
		String signedStr = "NO";
		if (signed == 1) {
			signedStr = "YES";
		}
		return signedStr;
	}
	
	private void showAllReports(int groupID){
		try {
			Statement stmt = conn.createStatement();
		//	ResultSet rs = stmt.executeQuery("select users.name, reports.id, reports.date, reports.week, reports.total_time, reports.signed "
		//									+ "from reports INNER JOIN user_group on reports.user_group_id = user_group.id "
			ResultSet rs = stmt.executeQuery("select users.username, reports.id, reports.date, reports.week, reports.total_time, reports.signed "
											+ " from user_group INNER JOIN reports on user_group.id = reports.user_group_id "
											+ " INNER JOIN users on user_group.user_id = users.id"
											+ " where user_group.group_id =" + groupID);
 
			
			out.println("<div class='floati'>");
			out.println("<p>Time reports: </p>");
			out.println("<table border=" + formElement("1") + ">");
			out.println("<tr><td>Selection</td><td>Username</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
			
			out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post"));
			while(rs.next()){
		    	String reportID = ""+rs.getInt("ID");
		    	String userName = rs.getString("username");
				int date = rs.getInt("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + userName + "</td>");				
				out.println("<td>" + date + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + formElement(signString(signed)) + "</td>");
				out.println("</tr>");
			}
			out.println("<p><input type=" + formElement("submit") + "value=" + formElement("Sort") + '>');
			out.println("</form>");
			out.println("</div>");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private String selectSortList(){
		String html = "";
		html += "<br><select name='sort'>";
		html += "<option value='0' selected='true'>Sort by: </option>";
		html += "<option value=" + formElement("username asc") + ">"
				+ "Namn, stigande" + "</option>";
		html += "<option value=" + formElement("username desc" ) + ">"
				+ "Namn, fallande" + "</option>";
		html += "<option value=" + formElement("week asc") + ">"
				+ "Vecka, stigande" + "</option>";
		html += "<option value=" + formElement("week desc") + ">"
				+ "Vecka, fallande" + "</option>";
		html += "<option value=" + formElement("signed desc") + ">"
				+ "Signerat/Osignerat" + "</option>";
		html += "<option value=" + formElement("sigend asc") + ">"
				+ "Osignerat/Signerat" + "</option>";
		html += "</select>";
		return html;
	}
	
	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	/**
	 * All requests are forwarded to the doGet method.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		access.updateLog(null, null); // check timestamps
		out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu(request));
		session = request.getSession();
		//     	HttpSession session = request.getSession(true);
		Object groupIDObject = session.getAttribute("groupID");
		int groupID = -1;
		if(groupIDObject != null) {
			groupID = Integer.parseInt((String) groupIDObject);
			showAllReports(groupID);
			out.println(selectSortList());
		}
		
	}



}
