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
	private String sort;
	private int reportID;
	
	public ReportHandling(){
		super();
		sort = "username asc";
		reportID = -1;
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
			ResultSet rs = stmt.executeQuery("select users.username, reports.id, reports.date, reports.week, reports.total_time, reports.signed "
											+ " from user_group INNER JOIN reports on user_group.id = reports.user_group_id "
											+ " INNER JOIN users on user_group.user_id = users.id"
											+ " where user_group.group_id =" + groupID+ " order by "+ sort);
			out.println("<div class='floati'>");
			out.println("<p>Time reports: </p>");
			out.println("<table border=" + formElement("1") + ">");
			out.println("<tr><td>Selection</td><td>Username</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
			
			out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post"));
			out.println(selectSortList());
			out.println("<p><input type=" + formElement("submit") + " name='sort' value="+ formElement("Sort") + '>');
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
			out.println("<p><input type=" + formElement("submit") + " name='view' value=" + formElement("View") + '>');
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
		html += "<option value=" + formElement("signed asc") + ">"
				+ "Osignerat/Signerat" + "</option>";
		html += "</select>";
		return html;
	}
	
	/**
	 * Fetches the data for the time report specified by the reportID parameter
	 * and passes the data on to the static method viewReport in the static ReportGenerator class.
	 * @param reportID: The id of the time report.
	 */
	private void printViewReport(){
		try {
			Statement stmt = conn.createStatement();
			String query = "select reports.week, reports.total_time, reports.signed, ";
			String q = "";
			for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
				String valueStr = "report_times." + ReportGenerator.act_sub_names[i];
				q += valueStr+",";
			}
			for (int i = 0; i<ReportGenerator.lower_activities.length-1; i++) {
				String valueStr = ReportGenerator.lower_activities[i];
				q += valueStr+",";
			}
			q += ReportGenerator.lower_activities[ReportGenerator.lower_activities.length-1];
			query += q;
			query += ", reports.user_group_id, reports.date, users.username, groups.name from reports";
			String inner = " inner join report_times on reports.id = report_times.report_id";			
			String inner1 = " inner join user_group on reports.user_group_id = user_group.id";
			String inner2 = " inner join users on user_group.user_id = users.id";
			String inner3 = " inner join groups on user_group.group_id = groups.id";
			String end = " where reports.id = " + reportID;
			query += inner;
			query += inner1;
			query += inner2;
			query += inner3;
			query += end;
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first()){				
				out.println(ReportGenerator.viewReport(rs));
				rs = stmt.executeQuery("Select signed from reports where id=" + reportID);
				rs.first();
				out.println("<p> <form name=" + formElement("input") + " method=" + formElement("post"));
				if(rs.getInt("signed") == 0){
					out.println("<p><input type=" + formElement("submit") + " name='sign' value="+ formElement("Sign") + '>');
				} else {
					out.println("<p><input type=" + formElement("submit") + " name='unsign' value="+ formElement("Unsign") + '>');
				}
				out.println("</form>");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
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
		HttpSession session = request.getSession(true);
		Object groupIDObject = session.getAttribute("groupID");
		int groupID = -1;
		if(groupIDObject != null) {		
			groupID = Integer.parseInt((String) groupIDObject);
			String reportIDString = request.getParameter("reportID");
			String buttonView = request.getParameter("view");
			if(reportIDString != null && buttonView != null){
				reportID = Integer.parseInt(reportIDString);
				printViewReport();
			} else {
				// Will be fixed soon
				/*				String buttonSign = request.getParameter("sign");
				String buttonUnsign = request.getParameter("unsign");
				if(buttonSign != null){
					System.out.println("inne i sign");
					Statement stmt;
					try {
						stmt = conn.createStatement();
						String statement = "Update reports SET signed = 1 where ID=" + reportID; 
						stmt.executeUpdate(statement);
					} catch (SQLException e) {
						e.printStackTrace();
					}			
				}	
				if(buttonUnsign != null){
					Statement stmt;
					try {
						stmt = conn.createStatement();
						String statement = "Update reports SET signed = 0 where ID=" + reportID; 
						stmt.executeUpdate(statement);
					} catch (SQLException e) {
						e.printStackTrace();
					}			
				}			
				 */				String buttonSort = request.getParameter("sort");
				 if(buttonSort != null){
					 String sortOrder = request.getParameter("sort");
					 if(!sortOrder.equals("0")){
						 sort = sortOrder;
					 }
				 }			
				 showAllReports(groupID);
			}
		}
	}
}
