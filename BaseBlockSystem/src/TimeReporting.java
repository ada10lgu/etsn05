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
		    out.println("<tr><td>Last Modifed</td><td>Week</td><td>Total Time</td><td>Signed</td></tr>");
		    int inWhile = 0;
		    while(rs.next()){
		    	inWhile = 1;
				int date = rs.getInt("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
				out.println("<tr>");
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
		String signedStr = "N";
		if (signed == 1) {
			signedStr = "Y";
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
	private void printNewReport(int weekNumber){
		ReportGenerator rg = new ReportGenerator();
		String name = (String) session.getAttribute("name");
		String group = (String) session.getAttribute("groupID");
		out.println(rg.newReport(weekNumber, name, group));
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
	private void addNewReport(){
		
		//newReport(int weekNumber, String user_name, String group);
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
		String function = request.getParameter("function");
		
		int week;
		
		if (!loggedIn(request)){
			response.sendRedirect("LogIn");
		} else if (function != null) {
			switch (function) {
			case VIEW:
				viewReportList((int) session.getAttribute("userGroupID"));
				break;
			case UPDATE:
				break;
			case NEW: //when user chooses New in menu
				//out.println(requestWeekForm()); create request for weeknumber and a ok button
				//check inside requestWeekForm that weeknumber is entered correctly and if so
				//change function = PRINT_NEW
				break;
			case PRINT_NEW:
				//week = request.getParameter("week"); //get weeknumber
				//printNewReport(week); //print the shell for the report
				//when user presses "done" check that everything is filled out correctly 
				//change inside printNewReport() function = ADD_NEW
				break;
			case ADD_NEW:
				break;
			case STATISTICS:
				break;
			}
		} else {
			viewReportList((int) session.getAttribute("userGroupID"));
		}
		
		
	}
	

}
