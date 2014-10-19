import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	protected static final String NEW = "new";
	protected static final String PRINT_NEW = "printNew";
	protected static final String ADD_NEW = "addNew";
	protected static final String STATISTICS = "statistics";
	
	
	public TimeReporting(){
		super();
	}

	//EDIT STLDD - change parameter userID --> userGroupID
	/**
	 * Prints out a list of the user’s own reports. 
	 * @param userGroupID: The id of the user.
	 */
	private void viewReportList(int userGroupID){
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from reports where user_group_id=" + userGroupID + " order by week asc");
			//create table
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
				out.println("</tr>");
			}
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
	private void viewReport(int reportID){
		
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
	private void addNewReport(HttpServletRequest request){
		String test = request.getParameter("SDP_U");
		System.out.println(test);
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
		System.out.println(function);
		String weekStr = request.getParameter("week");
		
		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		} else if (function != null && !isAdmin()) {
			switch (function) {
			case VIEW:
				//String testReport 
				viewReportList((int) session.getAttribute("userGroupID"));
				break;
			case VIEW_REPORT:
				break;
			case UPDATE:
				System.out.println("getParameter works!");
				break;
			case NEW:
				if (weekStr != null){
					response.sendRedirect("TimeReporting?function=printNew&week="+weekStr);
				} else {
					out.println(requestWeekForm()); //create request for weeknumber and a ok button.
				}
				break;
			case PRINT_NEW:
				//ADD CHECK IF WEEK IS CORRECT FORMAT
				int week = Integer.parseInt(weekStr);
				printNewReport(week, out);
					//when user presses "done" check that everything is filled out correctly 
					//change inside printNewReport() function = ADD_NEW
			
				break;
			case ADD_NEW:
				addNewReport(request);
				response.sendRedirect("TimeReporting?function=view");
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
