import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
	private static final String VIEW = "view";
	private static final String VIEW_REPORT = "viewReport";
	private static final String UPDATE = "update";
	private static final String UPDATE_REPORT = "updateReport";
	private static final String ADD_UPDATE_REPORT = "addUpdateReport";
	
	private static final String NEW = "new";
	private static final String PRINT_NEW = "printNew";
	private static final String ADD_NEW = "addNew";
	private static final String STATISTICS = "statistics";
	private static final String PRINT_STATISTICS = "printStatistics";
	
	
	public TimeReporting(){
		super();
	}
	
	/**
	 * Prints out a list of the users own reports. 
	 * @param userGroupID: The id of the user.
	 */
	private void viewReportList(int userGroupID){
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from reports where user_group_id=" + userGroupID + " order by week asc");
			//create table
			out.println("<form name=" + formElement("input") + " method=" + formElement("post")+ ">");
			out.println("<h1>Time Reports - View</h1>");
			out.println("Select a time report to view");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;   
	    	
		    while(rs.next()){		    	
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("ID");
				Date date = rs.getDate("date"); 
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + date.toString() + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + signString(signed) + "</td>");
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
			out.println("<h1>Time Reports - Update/Delete</h1>");
			out.println("<form name=" + formElement("input") + " method=" + formElement("post") +">");	
			out.println("Select a time report to update/delete");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;		        	
		    while(rs.next()){		    	
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("ID");
				Date date = rs.getDate("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("radio") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + date.toString() + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + signString(signed) + "</td>");
				out.println("</tr>");
			}
		    out.println("</table>");
		    out.println("<input type='hidden' name='function' value='updateReport'>");
		    
		    out.println("<input  type=" + formElement("submit") + " name='update' value="+ formElement("Update") +">");
		    out.println("<input  type=" + formElement("submit") + "onclick=" + formElement("return confirm('Are you sure you want to delete report?')") + " name='delete' value="+ formElement("Delete") +">");
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
	 * Creates the string representation for a signed or unsigned report.
	 * @param signed: int signed 0=unsigned 1=signed
	 * @return String: string representation
	 */
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
			ResultSet rs = stmt.executeQuery(query);
			if(rs.first()){				
				out.println(ReportGenerator.viewReport(rs));
			}
		} catch (SQLException e) {
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
				out.println("<div class='floati'>");
				out.println(ReportGenerator.updateReport(rs,reportID));
				out.println("</div>");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
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
				out.println("<div class='floati'>");
				out.println(ReportGenerator.newReport(weekNumber,(String) session.getAttribute("name"), groupName));
				out.println("</div>");
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
	private boolean updateReport(int reportID, HttpServletRequest request){
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
				String value = request.getParameter(ReportGenerator.lower_activities_names[i]);
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
			Date date = new Date(cal.getTimeInMillis());

			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE reports SET date='"+date.toString()+"',total_time="+totalTime+" WHERE id="+reportID);
			stmt.executeUpdate("DELETE FROM report_times WHERE report_id="+reportID);
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
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(q);
			stmt2.close();

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return true;
	}
	
	/**
	 * Deletes the report specified by the reportID parameter after confirmation and 
	 * if and only if it is unsigned.
	 * @param reportID: The id of the time report to delete.
	 */
	private void deleteReport(int reportID){
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM report_times WHERE report_id="+reportID);
			stmt.executeUpdate("DELETE FROM reports WHERE id="+reportID);
		} catch (SQLException e) {
		}
	}
	
	/**
	 * Inserts the data from the new report form into the database.
	 * @param request: The HttpServletRequest for this session.
	 * return boolean: True if successful operation.
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
				String value = request.getParameter(ReportGenerator.lower_activities_names[i]);
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
			Date date = new Date(cal.getTimeInMillis()); 
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
	
	/**
	 * Checks if the week is valid and doesnt already exist.
	 * @param userGroupID: User group id of the user that creates the report.
	 * @param week: The week to be tested.
	 * @return int: Returns the id of the report with the existing week, if week doesnt exist returns -1
	 */
	private int weekOk(int userGroupID, String week) {
		try {
			Statement stmt1 = conn.createStatement();
			ResultSet rs = stmt1.executeQuery("select * from reports where user_group_id = "+userGroupID+" and week = "+week);
			if (rs.first()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return -1;

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
		function = request.getParameter("function");
		String weekStr = request.getParameter("week");
		String reportID = request.getParameter("reportID");
		String update = request.getParameter("update");
		String delete = request.getParameter("delete");
		String success = request.getParameter("success");
		String submitStatistics = request.getParameter("submitStatistics");
		List<Integer> timeReports = new ArrayList<Integer>();
		if(submitStatistics != null){
			function = PRINT_STATISTICS;
			timeReports = getTimeReports(request, Integer.parseInt(submitStatistics));
		}
		
		int userGroupID = (int) session.getAttribute("userGroupID");
		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		} else if (function != null && !isAdmin()) {
			switch (function) {
			case VIEW:
				if(reportID != null){					
					response.sendRedirect("TimeReporting?function=viewReport&reportID="+reportID);
				}
				if (success != null && success.equals("true")){
					out.println("Time report was saved successfully");
				}
				viewReportList(userGroupID);
				break;
			case VIEW_REPORT:
				out.println("<div class='floati'>");
				if(reportID != null){
					out.println("<h1>Time Reports - Report</h1>");
					printViewReport(Integer.parseInt(reportID));
				};
				out.println("</div>");
				break;
			case UPDATE:
			if(delete!=null&&reportID!=null){
				deleteReport(Integer.parseInt(reportID));
				updateReportList(userGroupID);
				
			}
			else if(update!=null&&reportID!=null){
				response.sendRedirect("TimeReporting?function=updateReport&reportID="+reportID);
			}
			else{
				updateReportList(userGroupID);	
			}
				break;
			case UPDATE_REPORT: 
				out.println("<div class='floati'>");
				if(reportID != null){
					out.println("<h1>Time Reports - Update</h1>");
					printUpdateReport(Integer.parseInt(reportID));					
				}	
				out.println("</div>");
				break;
			case ADD_UPDATE_REPORT:
				if(updateReport(Integer.parseInt(reportID),request)){
					response.sendRedirect("TimeReporting?function=view&success=true");
				} else {
					out.println("<p>Wrong format input, use only numbers and the numbers have to be between 0-99999.</p>");
					printNewReport(Integer.parseInt(weekStr), out);
				}
				break;	
				
			case NEW:
				if (weekStr != null){
					if (checkInt(weekStr)&&(Integer.parseInt(weekStr)<100)){
						int weekOk = weekOk(userGroupID, weekStr);
						if (weekOk < 0) {
							response.sendRedirect("TimeReporting?function=printNew&week="+weekStr);
						} else {
							if(!isSigned(weekOk)){
								response.sendRedirect("TimeReporting?function=updateReport&reportID="+weekOk);
							} else {
								response.sendRedirect("TimeReporting?function=viewReport&reportID="+weekOk);
							}
						}
					} else {
						out.println("Wrong format");
						out.println(requestWeekForm(request));
					}
					
				} else {
					out.println(requestWeekForm(request)); //create request for weeknumber and a ok button.
				}
				break;
			case PRINT_NEW:
				out.println("<h1>Time Reports - New</h1>");
				int week = Integer.parseInt(weekStr);
				printNewReport(week, out);
				break;
			case ADD_NEW:
				if(addNewReport(request)){
					response.sendRedirect("TimeReporting?function=view&success=true");
				} else {
					out.println("<p>Wrong format input, use only numbers and the numbers have to be between 0-99999.</p>");
					printNewReport(Integer.parseInt(weekStr), out);
				}
				break;
			case STATISTICS:
				if (success != null) {
					if (success.equals("false")) {
						out.println("<p>No reports chosen</p>");
					}
				}
				statisticsReportList(userGroupID);
				break;
			case PRINT_STATISTICS:
				Statistics stats = new Statistics();
				out.println("<div class='floati'>");
				if (!stats.generateSummarizedReport(timeReports, response)) {
					response.sendRedirect("TimeReporting?function=statistics&success=false");
				}
				statisticsReportList(userGroupID);
				out.println("<div>");
				break;
			}
		} else if (isAdmin()){
			out.println("Admin is not allowed to reach this page");
		} else {
			viewReportList(userGroupID);
		}
		
		
	}

	/**
	 * Gets the report ids of the timereports that are selected.
	 * @param request: HttpServletRequest of the session
	 * @param nbrOfReports: Number of reports shown in the list.
	 * @return List<Integer>: List with time report ids.
	 */
	private List<Integer> getTimeReports(HttpServletRequest request, int nbrOfReports) {
		List<Integer> timeReports = new ArrayList<Integer>();
		for (int i=1; i<=nbrOfReports; i++) {
			String reportIDstr = request.getParameter("reportIDs"+i);
			if (reportIDstr != null) {
				int reportID = Integer.parseInt(reportIDstr);
				timeReports.add(reportID);
			}
		}
		return timeReports;
	}
	
	/**
	 * Creates a list of reports that can be used for generating statistics.
	 * @param userGroupID: User group id for the user.
	 */
	private void statisticsReportList(int userGroupID) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from reports where user_group_id=" + userGroupID + " order by week asc");
			//create table
			out.println("<form name=" + formElement("input") + " method=" + formElement("post")+ ">");
			out.println("<h1>Time Reports - Statistics</h1>");
			out.println("<p>Select time reports that you want to summerize and click view</p>");
		    out.println("<table border=" + formElement("1") + ">");
		    out.println("<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;   
	    	int nbrOfReports = 0;
		    while(rs.next()){
		    	nbrOfReports++;
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("ID");
				Date date = rs.getDate("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
				out.println("<td>" + "<input type=" + formElement("checkbox") + " name=" + formElement("reportIDs"+nbrOfReports) +
						" value=" + formElement(reportID) +"></td>");		//radiobutton
				out.println("<td>" + date.toString() + "</td>");
				out.println("<td>" + week + "</td>");
				out.println("<td>" + totalTime + "</td>");
				out.println("<td>" + signString(signed) + "</td>");
				out.println("</tr>");
			}
		    out.println("</table>");
		    out.println("<button type=" + formElement("submit") + "name='submitStatistics' value="+ nbrOfReports +">View </button>");
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
	 * Checks if a report is signed.
	 * @param reportID: Id of the report to be checked.
	 * @return boolean: True if the report is signed.
	 */
	private boolean isSigned(int reportID) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select signed from reports where id="+reportID);
			if(rs.first()){
				if(rs.getInt("signed") == 1) {
					return true;
				}
			}
		} catch (SQLException e) {
		}
		return false;
	}

	/**
	 * Checks if the input is valid.
	 * @param str: input to be checked.
	 * @return boolean: True if the input is valid.
	 */
	private boolean checkInt(String str) {
		try {
			int integer = Integer.parseInt(str);
			if(integer>=0&&integer<100000){
				return true;
			}
			else {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * Checks if a user is Admin.
	 * @return boolean: True if the user is Admin.
	 */
	private boolean isAdmin() {
		String name = (String) session.getAttribute("name");
		if (name.equals(ADMIN)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates the form to fill in a week number when a report is created.
	 * @param request: HttpServletRequest for the session.
	 * @return String: Html code.
	 */
	private String requestWeekForm(HttpServletRequest request) {
		String message = "";
		int latestWeek = -1;
		int userGroupID = (int) session.getAttribute("userGroupID");
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select week from reports where id=(select max(id) from reports where user_group_id="+userGroupID+")");
			if(rs.first()){
				latestWeek = rs.getInt("week");
				message += "The latest report that was created was week " + latestWeek;
			} else {
				message += "There are no previous reports.";
			}
		} catch (SQLException e) {
		}
		
		String html = "<h1>Time Reports - New </h1>"; 
		html += "<p>Please enter week number:</p>";
		html += "<p> <form name=" + formElement("input");
		html += " method=" + formElement("post");
		html += "<p> Week Number: <input type=" + formElement("text") + " name="
				+ formElement("week") + '>';
		html += "<hidden name='function' value='printNew'>";
		html += "<input type=" + formElement("submit") + "value="
				+ formElement("Submit") + '>';
		html += "<p>"+message+"</p>";
		html += "</form>";
		return html;
	}
	

}
