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



@WebServlet("/TimeReporting")
public class TimeReporting extends servletBase{
	
	private PrintWriter out;
	private HttpSession session;
	private String function = null;
	protected static final String VIEW = "view";
	protected static final String VIEW_REPORT = "viewReport";
	protected static final String UPDATE = "update";
	protected static final String UPDATE_REPORT = "updateReport";
	protected static final String NEW = "new";
	protected static final String PRINT_NEW = "printNew";
	protected static final String ADD_NEW = "addNew";
	protected static final String STATISTICS = "statistics";
	
	
	public TimeReporting(){
		super();
	}

	//EDIT STLDD - change parameter userID --> userGroupID
	/**
	 * Prints out a list of the users own reports. 
	 * @param userGroupID: The id of the user.
	 */
	private void viewReportList(int userGroupID){
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from reports where user_group_id=" + userGroupID + " order by week asc");
			//create table
			out.println("<form name=" + formElement("input") + " method=" + formElement("post"));
			out.println("<p>Time Reports:</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;   
	    	
		    while(rs.next()){		    	
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("ID");
				int date = rs.getInt("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + date + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + formElement(signString(signed)) + "</td>");
			//	out.println("<td>" + "<a href='TimeReporting?function="+ VIEW_REPORT + "&reportID="+ reportID + "'>View" + "</a></td>");
				//out.println("<td>" + "<a href='TimeReporting?function="+ UPDATE + "&reportID="+ reportID + "'>Update" + "</a></td>");
				out.println("</tr>");
			}
		    out.println("</table>");
		    out.println("<hidden name='function' value='viewReport'>");		    
		    out.println("<input  type=" + formElement("submit") + " value="+ formElement("View") +">");
		    out.println("</form>");
		    if (inWhile == 0){
		    	out.println("No reports to show");
		    }
		} catch(SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}	
	}
	
	/**
	 * Prints out a list of the users own reports. 
	 * @param userGroupID: The id of the user.
	 */
	private void updateReportList(int userGroupID){
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from reports where user_group_id=" + userGroupID + " and signed=0 order by week asc");
			//create table 
			out.println("<p>Time Reports:</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;
		    out.println("<form name=" + formElement("input") + " method=" + formElement("post"));	    	
		    while(rs.next()){		    	
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("ID");
				int date = rs.getInt("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + date + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + formElement(signString(signed)) + "</td>");
			//	out.println("<td>" + "<a href='TimeReporting?function="+ VIEW_REPORT + "&reportID="+ reportID + "'>View" + "</a></td>");
				//out.println("<td>" + "<a href='TimeReporting?function="+ UPDATE + "&reportID="+ reportID + "'>Update" + "</a></td>");
				out.println("</tr>");
			}
		    out.println("<hidden name='function' value='updateReport'");		    
		    out.println("<input  type=" + formElement("submit") + " value="+ formElement("Update") +">");
		    out.println("</form>");
		    if (inWhile == 0){
		    	out.println("No reports to show");
		    }
		} catch(SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}	
	}
	
	
	private String signString(int signed){
		String signedStr = "NO";
		if (signed == 1) {
			signedStr = "YES";
		}
		return signedStr;
	}
	
	/**
	 * Fetches the data for the time report specified by the reportID parameter
	 * and passes the data on to the static method viewReport in the static ReportGenerator class.
	 * @param reportID: The id of the time report.
	 */
	private void printViewReport(int reportID){
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
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first()){				
				out.println(ReportGenerator.viewReport(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}
	
	/**
	 * Fetches the data for the time report specified by the reportID parameter and passes
	 * the data on to the static method updateReport in the static ReportGenerator class.
	 * @param reportID: The id of the time report.
	 */
	private void printUpdateReport(int reportID){
		
	}
	
	/**
	 * Prints out the new timereport html-form.
	 * @param weekNumber: The weeknumber for the time report.
	 */
	private void printNewReport(int weekNumber, PrintWriter out){

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select name from groups where ID=" + (String) session.getAttribute("groupID"));
			if(rs.first()){
				String groupName = rs.getString("name");
				out.println(ReportGenerator.newReport(weekNumber,(String) session.getAttribute("name"), groupName));
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}
	
	
	
	
	/**
	 * Updates the data stored in the database for the report specified by the reportID parameter. 
	 * @param reportID: The id of the time report to update.
	 */
	private void updateReport(int reportID){
		
	}
	
	/**
	 * Deletes the report specified by the reportID parameter after confirmation and 
	 * if and only if it is unsigned.
	 * @param reportID: The id of the time report to delete.
	 */
	private void deleteReport(int reportID){
		
	}
	
	/**
	 * Inserts the data from the new report form into the database.
	 */
	private boolean addNewReport(HttpServletRequest request){
		try {
			int totalTime = 0;
			String[] act_sub_values = new String[ReportGenerator.act_sub_names.length];
			String[] lower_activity_values = new String[ReportGenerator.lower_activities.length];
			for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
				String value = request.getParameter(ReportGenerator.act_sub_names[i]);
				if (!value.equals("")) {
					act_sub_values[i] = value;
					if(!checkInt(value)){
						return false;
					}
					totalTime += Integer.parseInt(value);
				}else {
					act_sub_values[i] = "0";
				}
			}

			for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
				String value = request.getParameter(ReportGenerator.lower_activities[i]);
				if (!value.equals("")) {
					lower_activity_values[i] = value;
					if(!checkInt(value)){
						return false;
					}
					totalTime += Integer.parseInt(value);
				} else {
					lower_activity_values[i] = "0";
				}
			}
			
			Calendar cal = Calendar.getInstance();
			Date date = new Date(cal.getTimeInMillis()); //PUTTING THIS DATE OBJECT INTO THE DATABASE DOESN'T WORK, PLEASE HAVE A LOOK
			String week = request.getParameter("week");
			int userGroupID = (int) session.getAttribute("userGroupID");

			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO reports (user_group_id, date, week, total_time, signed) VALUES ("+userGroupID+",'"+date.toString()+"',"+week+","+totalTime+","+0+")");

			Statement stmt1 = conn.createStatement();
			ResultSet rs = stmt1.executeQuery("select * from reports where user_group_id = "+userGroupID+" and week = "+week); 
			int reportID = -1;
			if (rs.first()) {
				reportID = rs.getInt("id");
			}
			System.out.println(reportID);
			stmt.close();

			String q = "INSERT INTO report_times (report_id, ";
			for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
				String valueStr = ReportGenerator.act_sub_names[i];
				q += valueStr+",";
			}

			for (int i = 0; i<ReportGenerator.lower_activities.length-1; i++) {
				String valueStr = ReportGenerator.lower_activities[i];
				q += valueStr+",";
			}
			q += ReportGenerator.lower_activities[ReportGenerator.lower_activities.length-1];

			q += ") VALUES ("+reportID+",";
			for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
				String valueStr = act_sub_values[i];
				q += valueStr+",";
			}

			for (int i = 0; i<ReportGenerator.lower_activities.length-1; i++) {
				String valueStr = lower_activity_values[i];
				q += valueStr+",";
			}
			q += lower_activity_values[lower_activity_values.length-1]+");";
			System.out.println(q);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(q);
			stmt2.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return true;
	}
	
	private boolean weekOk(int userGroupID, String week) {
		try {
			Statement stmt1 = conn.createStatement();
			ResultSet rs = stmt1.executeQuery("select * from reports where user_group_id = "+userGroupID+" and week = "+week);
			if (rs.first()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return true;

	}

	/**
	 * Filters what time reports that should be shown in the list.
	 * @param filter: The filter that should be applied.
	 */
	private void filterReportList(String filter){
		
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
		out.println(printMainMenu());
		session = request.getSession();
		function = request.getParameter("function");
		String weekStr = request.getParameter("week");
		String reportID = request.getParameter("reportID");
		
	
		int userGroupID = (int) session.getAttribute("userGroupID");

		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		} else if (function != null && !isAdmin()) {
			switch (function) {
			case VIEW:
				//String testReport 
				if(reportID != null){					
					response.sendRedirect("TimeReporting?function=viewReport&reportID="+reportID);
				}
				viewReportList((int) session.getAttribute("userGroupID"));
				break;
			case VIEW_REPORT:
				if(reportID != null){		
					printViewReport(Integer.parseInt(reportID));
				}
				//printViewReport(reportID);
				break;
			case UPDATE:
				updateReportList((int) session.getAttribute("userGroupID"));				
				break;
			case UPDATE_REPORT:  
				//updateReport(reportID);
				break;
			case NEW:
				if (weekStr != null){
					response.sendRedirect("TimeReporting?function=printNew&week="+weekStr);
				} else {
					out.println(requestWeekForm()); //create request for weeknumber and a ok button.
				}
				break;
			case PRINT_NEW:
				if (checkInt(weekStr)) {
					if (weekOk(userGroupID, weekStr)) {
						int week = Integer.parseInt(weekStr);
						printNewReport(week, out);
					} 
				} else {
					out.println("You already have a report for this week or the format is wrong.");
					out.println(requestWeekForm());
				}
				break;
			case ADD_NEW:
				if(addNewReport(request)){
					response.sendRedirect("TimeReporting?function=view");
				} else {
					out.println("<p>Wrong format input, use only numbers.</p>");
					printNewReport(Integer.parseInt(weekStr), out);
				}
				break;
			case STATISTICS:
				break;
			}
		} else if (isAdmin()){
			out.println("Admin is not allowed to reach this page");
		} else {
			viewReportList((int) session.getAttribute("userGroupID"));
		}
		
		
	}

	private boolean checkInt(String str) {
		try {
			int integer = Integer.parseInt(str);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	private boolean isAdmin() {
		String name = (String) session.getAttribute("name");
		if (name.equals(ADMIN)) {
			return true;
		}
		return false;
	}

	private String requestWeekForm() {
		String html = "<p>Please enter week number:</p>";
		html += "<p> <form name=" + formElement("input");
		html += " method=" + formElement("post");
		html += "<p> Week Number: <input type=" + formElement("text") + " name="
				+ formElement("week") + '>';
		html += "<hidden name='function' value='printNew'>";
		html += "<p> <input type=" + formElement("submit") + "value="
				+ formElement("Submit") + '>';
		html += "</form>";
		return html;
	}
	

}
