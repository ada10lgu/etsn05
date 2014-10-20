

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean generateStatisticsReport(int groupID, int userID, String role, String weeks, HttpServletResponse response){
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
			
			if(userID != -1){
				end += " and user_group.user_id =  " + userID;
			}else if(role != null){
				end += " and user_group.role = " + formElement(role);
			}
			
			if(weeks.contains("-")){
				String[] split = weeks.split("-");
				end += " and reports.week >= " + split[0] + " and reports.week <= " + split[1];
			}else if(weeks != null){
				end += " and reports.week = " + weeks;
			}
			end += " and reports.signed = 1";
			
			query += inner;
			query += inner1;
			query += inner2;
			query += inner3;
			query += end;
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
			for(int id : timeReports){
				end += " reports.id = " + id + " or ";				
			}
			end += " reports.signed = 1";
			
			query += inner;
			query += inner1;
			query += inner2;
			query += inner3;
			query += end;
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
    	
    	return busiestWeek;
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
		PrintWriter out = response.getWriter();
		access.updateLog(null, null); // check timestamps
		HttpSession session = request.getSession(true);
		out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu(request));
		String function = request.getParameter("function");
		if(function != null){
			switch(function){
			case "busiestWeek" :
				int val = busiestWeek(Integer.parseInt((String)session.getAttribute("groupID")));
				if(val < 0){
					out.print("No results");
				}else{
					out.println("Busisest week : " + val);
				}				
				break;
			case "commonActivity" :
				out.println("Common : " + commonActivity(Integer.parseInt((String)session.getAttribute("groupID"))));
				break;	
			case "AllReports" :
	        	try {
	        		Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery("select * from reports");
					List<Integer> ids = new ArrayList<Integer>();
					while(rs.next()){
						ids.add(rs.getInt("id"));
					}
					generateSummarizedReport(ids, response);
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				break;	
			}
			
		}else{
			out.print(printOptions());
		}
		
	}
	
	private String printOptions(){
		String html ="<ul><li><a href='Statistics?function=busiestWeek'>Busy week</a></li>";
		html += "<li><a href='Statistics?function=commonActivity'>Common</a></li>";
		html += "<li><a href='Statistics?function=AllReports'>All reports</a></li>";
		return html;
	}

}
