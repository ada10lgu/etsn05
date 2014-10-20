

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     *@param groupID: The project leaders group id.
     *@param userID: The user for the time report.
     *@param role: The role of the user(s) for the time report.
     *@param weeks: The weeks for which the time report will be shown.
     *@param activity: The activity that will be included in the time report.
     *@return boolean: True if the report was successfully generated and shown.
     */
    private boolean generateStatisticsReport(int groupID, int userID, String role, String weeks, String activity){
    	return false;
    }
    /**
     * Shows a time report with the summarized activity from several time reports.
     * @param timeReports: The time reports which it will summarize from.
     * @param activity: The activity which will be summarized.
     * @param subactivity: The subactivity which will be summarized.
     * @return boolean: True if the time report was successfully generated.
     */
    private boolean generateSummarizedReport(List<Integer> timeReports, String activity){
    	
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
			String end = " where signed = 1 and user_group.group_id = " + groupID;
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
					int val = rs.getInt(ReportGenerator.act_sub_names[i]);
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
    		String query = "select reports.week, reports.total_time from reports inner join user_group on reports.user_group_id=user_group.id where user_group.group_id = " + groupID + " order by reports.week";
    		Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery(query);        	
        	rs.first();
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
    	}catch (Exception ex){
    		ex.printStackTrace();
    	}
    	
    	//hämta alla
    	//för varje vecka addera
    	//kolla den med mest
    	//reuturnera veckonummer
    	
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
		
	
	}

}
