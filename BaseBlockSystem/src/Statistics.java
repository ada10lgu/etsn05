

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Statement;

/**
 * Servlet implementation class Statistics
 */
@WebServlet("/Statistics")
public class Statistics extends servletBase {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Statistics() {
        super();
    }
    /**
     * Shows a time report based on the parameters requested by the user.
     * @param groupID: The project leaders group id.
     * @param userID: The user for the time report.
     * @param role: The role of the user(s) for the time report.
     * @param weeks: The weeks for which the time report will be shown.
     * @return boolean: True if the report was successfully generated and shown.
     */
    private boolean generateStatisticsReport(String groupID, String userID, String role, String weeks, HttpServletResponse response){
    	PrintWriter out;
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
			String end = " where user_group.group_id= " + groupID;
			
			if(!userID.equals("-1")){
				end += " and user_group.user_id =  " + userID;
			}else if(role != null){
				end += " and user_group.role = " + formElement(role);
			}
			
			if(weeks.contains("-")){
				String[] split = weeks.split("-");
				end += " and reports.week >= " + split[0] + " and reports.week <= " + split[1];
			}else if(weeks != null && !weeks.equals("")){
				end += " and reports.week = " + weeks;
			}
			end += " and reports.signed = 1";
			
			query += inner;
			query += inner1;
			query += inner2;
			query += inner3;
			query += end;
			//System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			Map<String, Integer> total = new HashMap<String, Integer>();
			if (rs.next()) {
				for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
					int val = rs.getInt(ReportGenerator.act_sub_names[i]);
					total.put(ReportGenerator.act_sub_names[i], val);
				}
				for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
					int val = rs.getInt(ReportGenerator.lower_activities[i]);
					total.put(ReportGenerator.lower_activities[i], val);
				}
			}
			while(rs.next()){
				for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
					int val = rs.getInt(ReportGenerator.act_sub_names[i]);
					total.put(ReportGenerator.act_sub_names[i], total.get(ReportGenerator.act_sub_names[i]) + val);
				}
				for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
					int val = rs.getInt(ReportGenerator.lower_activities[i]);
					total.put(ReportGenerator.lower_activities[i], total.get(ReportGenerator.lower_activities[i]) + val);
				}
			}
			
			if(rs.first()){		
				out = response.getWriter();
				out.println(ReportGenerator.viewReport(total));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			return false;
		}
    	return false;

    }
    /**
     * Shows a time report with the summarized activity from several time reports.
     * @param timeReports: The time reports which it will summarize from.
     * @return boolean: True if the time report was successfully generated.
     */
    protected boolean generateSummarizedReport(List<Integer> timeReports, HttpServletResponse response){
    	PrintWriter out;
    	if (timeReports != null && !timeReports.isEmpty()) {
	    	try {
				Statement stmt = conn.createStatement();
			
				String query = "select reports.id, reports.week, reports.total_time, reports.signed, ";
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
				String end = " where";
				for(int i = 0; i < timeReports.size(); i++){
					end += " reports.id = " + timeReports.get(i);
					if(i != timeReports.size()-1){
						end += " or ";
					}
				}
				
				query += inner;
				query += inner1;
				query += inner2;
				query += inner3;
				query += end;
				//System.out.println(query);
				ResultSet rs = stmt.executeQuery(query);			
				Map<String, Integer> total = new HashMap<String, Integer>();
				if (rs.next()) {
					for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
						int val = rs.getInt(ReportGenerator.act_sub_names[i]);
						total.put(ReportGenerator.act_sub_names[i], val);
					}
					for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
						int val = rs.getInt(ReportGenerator.lower_activities[i]);
						total.put(ReportGenerator.lower_activities[i], val);
					}
				}
				while(rs.next()){
					for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
						int val = rs.getInt(ReportGenerator.act_sub_names[i]);
						total.put(ReportGenerator.act_sub_names[i], total.get(ReportGenerator.act_sub_names[i]) + val);
					}
					for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
						int val = rs.getInt(ReportGenerator.lower_activities[i]);
						total.put(ReportGenerator.lower_activities[i], total.get(ReportGenerator.lower_activities[i]) + val);
					}
				}
				
				out = response.getWriter();
				out.println(ReportGenerator.viewReport(total));
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("SQLException: " + e.getMessage());
				return false;
			}
    	}
    	return false;
    }
   
    /**
     * Finds the activity that has the most combined minutes reported by the users in the projectgroup.
     * @param groupID: The project leaders group id.
     * @return Returns the activity.
     */
    private String commonActivity(int groupID){
    	String common = "";
    	try {
			Statement stmt = conn.createStatement();
			String query = "select reports.signed, ";
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
			query += ", reports.user_group_id from reports";
			String inner = " inner join report_times on reports.id = report_times.report_id";			
			String inner1 = " inner join user_group on reports.user_group_id = user_group.id";
			String end = " where reports.signed = 1 and user_group.group_id = " + groupID;
			query += inner;
			query += inner1;
			query += end;
			ResultSet rs = stmt.executeQuery(query);
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
				map.put(ReportGenerator.act_sub_names[i], 0);				
			}
			for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
				map.put(ReportGenerator.lower_activities[i], 0);			
			}			
			while(rs.next()){
				for (int i = 0; i<ReportGenerator.act_sub_names.length; i++) {
					int val = rs.getInt(ReportGenerator.act_sub_names[i]);
					map.put(ReportGenerator.act_sub_names[i], map.get(ReportGenerator.act_sub_names[i])+ val);								
				}
				for (int i = 0; i<ReportGenerator.lower_activities.length; i++) {
					int val = rs.getInt(ReportGenerator.lower_activities[i]);
					map.put(ReportGenerator.lower_activities[i], map.get(ReportGenerator.lower_activities[i])+ val);		
				}				
			}
			int highest = 0;
			
			for(String activity : map.keySet()){
				if(map.get(activity)>highest){
					common = activity;
					highest = map.get(activity);
				}	
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
    	
    	return common;
    }
    
    /**
     * Finds the week with the most combined minutes reported by the users in the project group.
     * @param groupID: The project leaders group id.
     * @return int: Returns the busiest week.
     */
    private int busiestWeek(int groupID){
    	int currentWeek = 0;
    	int busiestWeek = 0;
    	int sumWeek = 0; 
    	try{
    		String query = "select reports.week, reports.total_time from reports inner join user_group on reports.user_group_id=user_group.id where user_group.group_id = " + groupID + " and reports.signed=1 order by reports.week";
    		System.out.println(query);
    		
    		Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery(query);        	
        	if(!rs.first()){        		
        		return -1;
        	}
        	currentWeek = rs.getInt("week");
        	sumWeek = rs.getInt("total_time");
        	int sumBusiestWeek = sumWeek;
        	busiestWeek = currentWeek;
        	while(rs.next()){
        		if(currentWeek == rs.getInt("week")){
        			sumWeek += rs.getInt("total_time");
        		}else{
        			if(sumWeek > sumBusiestWeek){
        				busiestWeek = currentWeek;        				
        				sumBusiestWeek = sumWeek;
        			}
        			currentWeek = rs.getInt("week");
        		}        		
        	}
        	stmt.close();
    	}catch (Exception ex){
    		ex.printStackTrace();
    	}
    	
    	//hï¿½mta alla
    	//fï¿½r varje vecka addera
    	//kolla den med mest
    	//reuturnera veckonummer
    	return busiestWeek;
    }
    
    /**
	 * Checks if the user is either an admin or a Project Leader of the group
	 * 
	 * @param myName
	 * @return true if the user is admin/project leader, else false
	 */
	private boolean projectLeaderOrAdmin(String myName) {
		if (myName.equals("admin")) {
			return true;
		} else {
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt
						.executeQuery("select * from user_group INNER JOIN users on user_group.user_id = users.ID where users.username = '"
								+ myName + "'");
				if (rs.first()) {
					String role = rs.getString("role");
					if (role.equals("Project Leader")) {
						return true;
					}
				}
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return false;
	}
    
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String username = (String) session.getAttribute("name");
		boolean isAdmin = username.equals("admin");
		if (projectLeaderOrAdmin(username)) {
			String html = "";
			PrintWriter out = response.getWriter();
			access.updateLog(null, null); // check timestamps
			int groupID = Integer.parseInt((String)session.getAttribute("groupID"));
			out = response.getWriter();
			out.println(getPageIntro());
			out.println(printMainMenu(request));
			out.println("<div class='floati'>");
			String selectedGroup = "";//håller koll på den grupp admin har valt
			if(!isAdmin){
				selectedGroup = "" + groupID;
			}else{
				String gr = request.getParameter("groupID");
				if(gr == null){
					String gr_sel = request.getParameter("alreadySelected");
					if(gr_sel == null){
						selectedGroup = "0";
					}else{
						selectedGroup = gr_sel;
						System.out.println("ALREADY SEL");
					}
				}else{
					selectedGroup = gr;
				}
			}
			String function = request.getParameter("function");
			if(function != null){
			
				switch(function){				
				case "generateSelectedReports": //user selected reports from a table
					out.println("<h1>Summarized report</h1>");
					String[] checkedIds = request.getParameterValues("reportID");					
					if(checkedIds != null){
						List<Integer> ids = new ArrayList<Integer>();
						for(String s : checkedIds){
							ids.add(Integer.parseInt(s));							
						}					
						if(ids.size() != 0){
							if(!generateSummarizedReport(ids, response)){
								out.println("No report was generated");
							}
						}						
					}
					break;
				case "generateStatsReports" :
					System.out.println("YAY");
					String role = request.getParameter("roles");
					String userID = request.getParameter("userID");
					String weeks = request.getParameter("weeks");
					if(!generateStatisticsReport(selectedGroup, userID, role, weeks, response)){
						out.println("No report was generated");
					}
					break;
				}
				
				
			}
			out.print(printOptions(selectedGroup, isAdmin)); 
			
			out.println("</div>");
			
		}
			
	}
		
	private String printOptions(String groupID, boolean isAdmin){		
		String html ="<h1>Generate Statistics</h1>";
		//generate stats for selected reports (select reports from a list? checkboxes?)
		html +="<div class='floati'>";
		if(isAdmin){
			html += "<form name=" + formElement("chooseGroup") + " method=" + formElement("post")+ ">";						
			html += getGroupsList(groupID);
			html += "<input  type=" + formElement("submit") + " value="+ formElement("Select group") +">";			
			html += "<input type='hidden' name='function' value='selectGroup'>";
			html += "</form>";
		}
		if(!groupID.equals("0")){
			html += "<form name=" + formElement("selectedReports") + " method=" + formElement("post")+ ">";
			if(isAdmin){
				html += "<input type='hidden' name='alreadySelected' value=" + formElement(groupID) + ">";
			}
			html += "<input type='hidden' name='function' value='generateSelectedReports'>";
			html += getReportsTable(groupID);			
			html += "<input  type=" + formElement("submit") + " value="+ formElement("Generate report") +">";
			html += "</form>";
		}
		html+= "</div>";
		html +="<div style='margin-left: 20px;' class='floati'>";
		if(!groupID.equals("0")){
			html += "<form name=" + formElement("selectFields") + " method=" + formElement("post")+ ">";
			if(isAdmin){
				html += "<input type='hidden' name='alreadySelected' value=" + formElement(groupID) + ">";
			}
			html += getUsersList(groupID);
			html +="</br>";
			html += "<select name='roles'>";
			html += "<option value='0'>Select a role</option>";
			html += "<option value='"+PROJECT_LEADER+"'>Project Leader</option>";
			html += "<option value='"+t1+"'>t1</option>";
			html += "<option value='"+t2+"'>t2</option>";
			html += "<option value='"+t3+"'>t3</option>";
			html += "</select>";		
			html +="<p>Specify week(s) (i.e. 5 or 4-23) </p> <input type='text' name='weeks'>";
			
			html += "<input type='hidden' name='function' value='generateStatsReports'>";
			html += "<input  type=" + formElement("submit") + " value="+ formElement("Generate statistics") +">";
			html += "</form>";
		}
		html+= "</div>";
		//print a list with all reports from the group where signed= 1
		
		
		
		//html += "<ul><li><a href='Statistics?function=busiestWeek'>Busy week</a></li>";
	//	html += "<li><a href='Statistics?function=commonActivity'>Common</a></li>";
	//	html += "<li><a href='Statistics?function=AllReports'>All reports</a></li>";
	//	html += "<li><a href='Statistics?function=generateStatisticsReport'>Generate custom statistics</a></li>";
		
		return html;
	}
	private String getUsersList(String groupID){
		String html ="";
		html += "<select name='userID'>";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select users.id, users.username from users inner join user_group on users.id = user_group.user_id where user_group.group_id = " + groupID);
			html += "<option value='-1'>Select a user</option>";
			while(rs.next()) {				
				html += "<option value='"+rs.getInt("id")+"'>"+rs.getString("username")+"</option>";								
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		html += "</select>";		
		return html;
	}

	private String getGroupsList(String currentgroupID){
		String html ="";
		html += "<select name='groupID'>";
		try {
			Statement stmt = conn.createStatement();
			ResultSet groups = stmt.executeQuery("select * from groups");
			while(groups.next()) {
				if(currentgroupID.equals("" + groups.getInt("id"))){
					html += "<option selected='true' value='"+groups.getInt("id")+"'>"+groups.getString("name")+"</option>";
				}else{
					html += "<option value='"+groups.getInt("id")+"'>"+groups.getString("name")+"</option>";
				}				
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		html += "</select>";		
		return html;
	}
	private String getReportsTable(String groupID){
		String html ="";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select reports.id, reports.date, reports.week, reports.total_time from reports inner join user_group on reports.user_group_id= user_group.id where user_group.group_id=" + groupID + " and reports.signed = 1 order by week asc");
			//create table			
			html += "<p>Time Reports:</p>";
			html += "<table border=" + formElement("1") + ">";
		    html += "<tr><td>Selection</td><td>Last update</td><td>Week</td><td>Total Time</td></tr>";
		    int inWhile = 0;
	    	
		    while(rs.next()){		    	
		    	inWhile = 1;
		    	String reportID = ""+rs.getInt("id");
		    	
				Date date = rs.getDate("date"); 
				int week = rs.getInt("week");
				int totalTime = rs.getInt("total_time");				
				//print in box
				html += "<tr>";
				html += "<td>" + "<input type=" + formElement("checkbox") + " name=" + formElement("reportID") +
						" value=" + formElement(reportID) +"></td>";		//checkboxes
				html += "<td>" + date.toString() + "</td>";
				html += "<td>" + week + "</td>";
				html += "<td>" + totalTime + "</td>";
				html += "</tr>";
			}
		    html += "</table>";
		    
		    if (inWhile == 0){
		    	html += "No reports to show";
		    }
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}	
		return html;
	}
}
