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




public class TimeReporting extends servletBase{
	
	private PrintWriter out;
	private HttpSession session;
	
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
			while(rs.next()){
				int date = rs.getInt("date");
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");
				int signed = rs.getInt("signed");
				//print in box
			}
		} catch(SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		
		
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
		System.out.println("doPost");
		access.updateLog(null, null); // check timestamps
		out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu());
		session = request.getSession();
//		if (!loggedIn(request)){
//			response.sendRedirect("LogIn");
//		} else {
//			viewReportList((int) session.getAttribute("userGroupID"));
//		}
	}
	

}
