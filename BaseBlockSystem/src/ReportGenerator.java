

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpSession;

public final class ReportGenerator {

	private static String name = null;
	private static Date date = null;
	private static String project_group = null;
	private static int week_number = -1;
	private static int total_time = -1;
	private static int signed = -1;
	private static int[] act_sub = new int[45];
	private static int[] act = new int[9];
	private static int[] sub = new int[5];
	
	private static final int[] upper_numbers = new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19};
	private static final String[] upper_activities = new String[]{"SDP", "SRS", "SVVS", "STLDD", "SVVI", "SDDD", "SVVR", "SSD", "Slutrapport"};
	private static final String[] upper_activities_names = new String[]{"SDP", "SRS", "SVVS", "STLDD", "SVVI", "SDDD", "SVVR", "SSD", "Final Report"};
	private static final int[] lower_numbers = new int[]{21, 22, 23, 30, 41, 42, 43, 44, 100};
	protected static final String[] lower_activities_names = new String[]{"Functional test", "System test", "Regression test", "Meeting", "Lecture", "Excercise", "Computer exercise", "Home reading", "Other"};
	protected static final String[] lower_activities = new String[]{"Funktionstest", "Systemtest", "Regressionstest", "Meeting", "Lecture", "Excersice", "Terminal", "Study", "Other"};
	private static final String[] activity_type = new String[]{"Development and documentation", "Informal review", "Formal review", "Rework, improvement or correction"};
	//private static final String[] activity_type = new String[]{"Utveckling och dokumentation", "Informell granskning", "Formell granskning", "Ombearbetning"};
	private static final String[] activity_code = new String[]{"U", "I", "F", "O"};
	private static final String[] activity_description = new String[]{"Developing new code, test cases and documentation including documentation of the system",
		"Time spent preparing and at meeting for informal reviews", "Time spent preparing and at meeting for formal reviews",
		"Time spent improving, revising or correction documents and design objects"};
	/*private static final String[] activity_description = new String[]{"Utveckla ny kod, testfall och dokumentation inklusive dokumentation av systemet",
		"Tid spenderad på förberedelser inför och på informella granskningar", "Tid spenderad på förberedelser inför och på formella granskningar",
		"Tid spenderad på ombearbetning, förbättring, revision eller korrektion av dokument och design objekt"};*/
	
	protected static final String[] act_sub_names = new String[]{"SDP_U", "SDP_I", "SDP_F", "SDP_O", "SRS_U", "SRS_I", "SRS_F", "SRS_O", 
		"SVVS_U", "SVVS_I", "SVVS_F", "SVVS_O", "STLDD_U", "STLDD_I", "STLDD_F", "STLDD_O", "SVVI_U", "SVVI_I", "SVVI_F", "SVVI_O", 
		"SDDD_U", "SDDD_I", "SDDD_F", "SDDD_O", "SVVR_U", "SVVR_I", "SVVR_F", "SVVR_O", "SSD_U", "SSD_I", "SSD_F", "SSD_O", 
		"Slutrapport_U", "Slutrapport_I", "Slutrapport_F", "Slutrapport_O"};
	
	private ReportGenerator() {
		
	}
	
	private static void init_test_data() {
		name = "Test User";
		date = new Date(0);
		project_group = "Test Group";
		week_number = 1;
		total_time = 100;
		signed = 1;
		for (int i = 0; i < act_sub.length; i++) {
			act_sub[i] = i;
		}
		for (int i = 0; i < act.length; i++) {
			act[i] = i;
		}
		for (int i = 0; i < sub.length; i++) {
			sub[i] = i;
		}
	}
	
	private static void init_data(ResultSet data) {
		try {
			name = data.getString("username");
			date = data.getDate("date");
			project_group = data.getString("name");
			week_number = data.getInt("week");
			total_time = data.getInt("total_time");
			signed = data.getInt("signed");
			
			act_sub[0] = data.getInt("SDP_U");
			act_sub[1] = data.getInt("SDP_I");
			act_sub[2] = data.getInt("SDP_F");
			act_sub[3] = data.getInt("SDP_O");
			act_sub[4] = act_sub[0] + act_sub[1] +act_sub[2] +act_sub[3];
			act_sub[5] = data.getInt("SRS_U");
			act_sub[6] = data.getInt("SRS_I");
			act_sub[7] = data.getInt("SRS_F");
			act_sub[8] = data.getInt("SRS_O");
			act_sub[9] = act_sub[5] + act_sub[6] +act_sub[7] +act_sub[8];
			act_sub[10] = data.getInt("SVVS_U");
			act_sub[11] = data.getInt("SVVS_I");
			act_sub[12] = data.getInt("SVVS_F");
			act_sub[13] = data.getInt("SVVS_O");
			act_sub[14] = act_sub[10] + act_sub[11] +act_sub[12] +act_sub[13];
			act_sub[15] = data.getInt("STLDD_U");
			act_sub[16] = data.getInt("STLDD_I");
			act_sub[17] = data.getInt("STLDD_F");
			act_sub[18] = data.getInt("STLDD_O");
			act_sub[19] = act_sub[15] + act_sub[16] +act_sub[17] +act_sub[18];
			act_sub[20] = data.getInt("SVVI_U");
			act_sub[21] = data.getInt("SVVI_I");
			act_sub[22] = data.getInt("SVVI_F");
			act_sub[23] = data.getInt("SVVI_O");
			act_sub[24] = act_sub[20] + act_sub[21] +act_sub[22] +act_sub[23];
			act_sub[25] = data.getInt("SDDD_U");
			act_sub[26] = data.getInt("SDDD_I");
			act_sub[27] = data.getInt("SDDD_F");
			act_sub[28] = data.getInt("SDDD_O");
			act_sub[29] = act_sub[25] + act_sub[26] +act_sub[27] +act_sub[28];
			act_sub[30] = data.getInt("SVVR_U");
			act_sub[31] = data.getInt("SVVR_I");
			act_sub[32] = data.getInt("SVVR_F");
			act_sub[33] = data.getInt("SVVR_O");
			act_sub[34] = act_sub[30] + act_sub[31] +act_sub[32] +act_sub[33];
			act_sub[35] = data.getInt("SSD_U");
			act_sub[36] = data.getInt("SSD_I");
			act_sub[37] = data.getInt("SSD_F");
			act_sub[38] = data.getInt("SSD_O");
			act_sub[39] = act_sub[35] + act_sub[36] +act_sub[37] +act_sub[38];
			act_sub[40] = data.getInt("Slutrapport_U");
			act_sub[41] = data.getInt("Slutrapport_I");
			act_sub[42] = data.getInt("Slutrapport_F");
			act_sub[43] = data.getInt("Slutrapport_O");
			act_sub[44] = act_sub[40] + act_sub[41] +act_sub[42] +act_sub[43];
			
			act[0] = data.getInt("Funktionstest");
			act[1] = data.getInt("Systemtest");
			act[2] = data.getInt("Regressionstest");
			act[3] = data.getInt("Meeting");
			act[4] = data.getInt("Lecture");
			act[5] = data.getInt("Excersice");
			act[6] = data.getInt("Terminal");
			act[7] = data.getInt("Study");
			act[8] = data.getInt("Other");
			
			sub[0] = act_sub[0] + act_sub[5] +act_sub[10] +act_sub[15] + act_sub[20] + act_sub[25] +act_sub[30] +act_sub[35] + act_sub[40];
			sub[1] = act_sub[1] + act_sub[6] +act_sub[11] +act_sub[16] + act_sub[21] + act_sub[26] +act_sub[31] +act_sub[36] + act_sub[41];
			sub[2] = act_sub[2] + act_sub[7] +act_sub[12] +act_sub[17] + act_sub[22] + act_sub[27] +act_sub[32] +act_sub[37] + act_sub[42];
			sub[3] = act_sub[3] + act_sub[8] +act_sub[13] +act_sub[18] + act_sub[23] + act_sub[28] +act_sub[33] +act_sub[38] + act_sub[43];
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void init_data(Map<String, Integer> data) {
		try {
			name = "Statistics";
			date = new Date(System.currentTimeMillis()); 
			project_group = "";
			week_number = -1;
			signed = -1;
			
			act_sub[0] = data.get("SDP_U");
			act_sub[1] = data.get("SDP_I");
			act_sub[2] = data.get("SDP_F");
			act_sub[3] = data.get("SDP_O");
			act_sub[4] = act_sub[0] + act_sub[1] +act_sub[2] +act_sub[3];
			act_sub[5] = data.get("SRS_U");
			act_sub[6] = data.get("SRS_I");
			act_sub[7] = data.get("SRS_F");
			act_sub[8] = data.get("SRS_O");
			act_sub[9] = act_sub[5] + act_sub[6] +act_sub[7] +act_sub[8];
			act_sub[10] = data.get("SVVS_U");
			act_sub[11] = data.get("SVVS_I");
			act_sub[12] = data.get("SVVS_F");
			act_sub[13] = data.get("SVVS_O");
			act_sub[14] = act_sub[10] + act_sub[11] +act_sub[12] +act_sub[13];
			act_sub[15] = data.get("STLDD_U");
			act_sub[16] = data.get("STLDD_I");
			act_sub[17] = data.get("STLDD_F");
			act_sub[18] = data.get("STLDD_O");
			act_sub[19] = act_sub[15] + act_sub[16] +act_sub[17] +act_sub[18];
			act_sub[20] = data.get("SVVI_U");
			act_sub[21] = data.get("SVVI_I");
			act_sub[22] = data.get("SVVI_F");
			act_sub[23] = data.get("SVVI_O");
			act_sub[24] = act_sub[20] + act_sub[21] +act_sub[22] +act_sub[23];
			act_sub[25] = data.get("SDDD_U");
			act_sub[26] = data.get("SDDD_I");
			act_sub[27] = data.get("SDDD_F");
			act_sub[28] = data.get("SDDD_O");
			act_sub[29] = act_sub[25] + act_sub[26] +act_sub[27] +act_sub[28];
			act_sub[30] = data.get("SVVR_U");
			act_sub[31] = data.get("SVVR_I");
			act_sub[32] = data.get("SVVR_F");
			act_sub[33] = data.get("SVVR_O");
			act_sub[34] = act_sub[30] + act_sub[31] +act_sub[32] +act_sub[33];
			act_sub[35] = data.get("SSD_U");
			act_sub[36] = data.get("SSD_I");
			act_sub[37] = data.get("SSD_F");
			act_sub[38] = data.get("SSD_O");
			act_sub[39] = act_sub[35] + act_sub[36] +act_sub[37] +act_sub[38];
			act_sub[40] = data.get("Slutrapport_U");
			act_sub[41] = data.get("Slutrapport_I");
			act_sub[42] = data.get("Slutrapport_F");
			act_sub[43] = data.get("Slutrapport_O");
			act_sub[44] = act_sub[40] + act_sub[41] +act_sub[42] +act_sub[43];
			
			act[0] = data.get("Funktionstest");
			act[1] = data.get("Systemtest");
			act[2] = data.get("Regressionstest");
			act[3] = data.get("Meeting");
			act[4] = data.get("Lecture");
			act[5] = data.get("Excersice");
			act[6] = data.get("Terminal");
			act[7] = data.get("Study");
			act[8] = data.get("Other");
			
			sub[0] = act_sub[0] + act_sub[5] +act_sub[10] +act_sub[15] + act_sub[20] + act_sub[25] +act_sub[30] +act_sub[35] + act_sub[40];
			sub[1] = act_sub[1] + act_sub[6] +act_sub[11] +act_sub[16] + act_sub[21] + act_sub[26] +act_sub[31] +act_sub[36] + act_sub[41];
			sub[2] = act_sub[2] + act_sub[7] +act_sub[12] +act_sub[17] + act_sub[22] + act_sub[27] +act_sub[32] +act_sub[37] + act_sub[42];
			sub[3] = act_sub[3] + act_sub[8] +act_sub[13] +act_sub[18] + act_sub[23] + act_sub[28] +act_sub[33] +act_sub[38] + act_sub[43];	
			
			total_time = 0;
			for (int i : act) {
				total_time += i;
			}
			for (int i = 4; i < act_sub.length; i = i + 5) {
				int k = act_sub[i];
				total_time += k;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a String-representation of a time report containing the data specified by the data parameter.
	 * @param data: Contains the data that should be printed in the time report.
	 */
	public static String viewReport(ResultSet data) {
		if (data == null) {
			init_test_data();
		} else {
			init_data(data);
		}
		return generateViewReport();
	}
	
	/**
	 * Generates a String-representation of a time report containing the data specified by the data parameter.
	 * @param data: Contains the data that should be printed in the time report.
	 */
	public static String viewReport(Map<String, Integer> data) {
		if (data == null) {
			init_test_data();
		} else {
			init_data(data);
		}
		return generateViewReport();
	}
	
	private static String generateViewReport() {
		String html = "";
		html += "<style type='text/css'>"
				+ "table {border: 1px solid black; border-collapse: collapse; width: 700px; text-align: left;}"
				+ "td {border: 1px solid black; width: 100px;}"
				+ ".grey {background-color: #dddddd;}"
				+ "</style>";
		html += "<table>";
		html += 	"<tr>";
		html +=			"<td><b>Name</td><td colspan='4'>"+name+"</td><td><b>Date</td><td>"+date.toString()+"</td>";
		html += 	"</tr>";		
		html += 	"<tr>";
		html +=			"<td><b>Project group</td><td colspan='4'>"+project_group+"</td><td><b>Week</td><td>"+week_number+"</td>";
		html += 	"</tr>";
		html += 	"<tr>";
		html += 		"<td colspan='6' class='grey'><b>Part A - Total time this week (minutes)</td><td>"+total_time+"</td>";
		html += 	"</tr>";
		html += 	"<tr>";
		html += 		"<td colspan='7' class='grey'><b>Part B - Number of minutes per activity</td>";
		html += 	"</tr>";
		html += 	"<tr>";
		html += 		"<td><b>Number</td><td><b>Activity</td><td><b>U</td><td><b>I</td><td><b>F</td><td><b>O</td><td><b>Total time</td>";
		html += 	"</tr>";
		int k = 0;
		for (int i = 0; i < 9; i++) {
			html += "<tr>";
			for (int j = 0; j < 7; j++) {
				if (j == 0) {
					html += "<td>"+upper_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+upper_activities_names[i]+"</td>";
				}
				if (j > 1) {
					html += "<td>"+act_sub[k]+"</td>";
					k++;
				}	
			}
			html += "</tr>";
		}
		html +=		"<tr>";
		html += 		"<td><b>Sum</td><td class='grey'></td><td>"+sub[0]+"</td><td>"+sub[1]+"</td><td>"+sub[2]+"</td><td>"+sub[3]+"</td><td class='grey'></td>";			
		html +=		"</tr>";
		k = 0;
		for (int i = 0; i < 9; i++) {
			html += "<tr>";
			for (int j = 0; j < 3; j++) {
				if (j == 0) {
					html += "<td>"+lower_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td colspan='5'>"+lower_activities_names[i]+"</td>";
				}
				if (j == 2) {
					html += "<td>"+act[k]+"</td>";
					k++;
				}	
			}
			html += "</tr>";
		}
		html += 	"<tr>";
		html += 		"<td colspan='7' class='grey'><b>Part C - Time spent at different types of sub activities</td>";
		html += 	"</tr>";
		html += 	"<tr>";
		html += 		"<td><b>Activity type</td><td><b>Activity code</td><td colspan='4'><b>Description</td><td><b>Sum</td>";
		html += 	"</tr>";
		k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					html += "<td>"+activity_type[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+activity_code[i]+"</td>";
				}
				if (j == 2) {
					html += "<td colspan='4'>"+activity_description[i]+"</td>";
				}
				if (j == 3) {
					html += "<td>"+sub[i]+"</td>";
				}
			}
			html += "</tr>";
		}	
		html += 	"<tr>";
		html += 		"<td colspan='7' class='grey'><b>Del D - Signatur</td>";
		html += 	"</tr>";
		html += 	"<tr>";
		html += 		"<td><b>Signed</td><td colspan='5'></td>";
		if (signed == 0) {
			html +=		"<td>NO</td>";
		} else if (signed == 1) {
			html +=		"<td>YES</td>";
		} else {
			html += 	"<td></td>";
		}
		html += 	"</tr>";
		
		html += "</table>";
		
		return html;
	}
	
	/**
	 * Generates a String-representation of a time report HTML-form pre-filled with the data specified by the data parameter.
	 * @param data: Contains the data that should be printed in the time report.
	 */
	public static String updateReport(ResultSet data, int reportID) {
		if (data == null) {
			init_test_data();
		} else {
			init_data(data);
		}
		
		String html = "";
		html += "<style type='text/css'>"
				+ "table {border: 1px solid black; border-collapse: collapse; width: 700px; text-align: left;}"
				+ "td {border: 1px solid black; width: 100px;}"
				+ ".grey {background-color: #dddddd;}"
				+ "input {width: 100px;}"
				+ "</style>";
		html += "<form method='post' action='TimeReporting?action=updateReport&function=addUpdateReport&reportID="+reportID+"'>";
		html += 	"<table>";
		html += 		"<tr>";
		html +=				"<td><b>Name</td><td colspan='4'>"+name+"</td><td><b>Date</td><td>"+date.toString()+"</td>";
		html +=			"</tr>";		
		html += 		"<tr>";
		html +=				"<td><b>Project group</td><td colspan='4'>"+project_group+"</td><td><b>Week</td><td>"+week_number+"</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td colspan='6' class='grey'><b>Part A - Total time this week (minutes)</td><td>"+total_time+"</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Part B - Number of minutes per activity</td>";
		html +=			"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Number</td><td><b>Acitivity</td><td><b>U</td><td><b>I</td><td><b>F</td><td><b>O</td><td><b>Total time</td>";
		html +=			"</tr>";
		int k = 0;
		int l = 0;
		for (int i = 0; i < 9; i++) {
			html += 	"<tr>";
			for (int j = 0; j < 7; j++) {
				if (j == 0) {
					html += "<td>"+upper_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+upper_activities_names[i]+"</td>";
				}
				if (1 < j && j < 6) {
					html += "<td><input type='text' name='"+act_sub_names[l]+"' value='";
					if (act_sub[k] > 0) {
						html += act_sub[k];
					}
					html += "' /></td>";
					k++;
					l++;
				}
				if ( j == 6) {
					html += "<td></td>";
					k++;
				}
			}
			html += 	"</tr>";
		}
		html +=			"<tr>";
		html += 			"<td><b>Sum</td><td class='grey'></td><td></td><td></td><td></td><td></td><td class='grey'></td>";			
		html +=			"</tr>";
		k = 0;
		for (int i = 0; i < 9; i++) {
			html += 	"<tr>";
			for (int j = 0; j < 3; j++) {
				if (j == 0) {
					html += "<td>"+lower_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td colspan='5'>"+lower_activities_names[i]+"</td>";
				}
				if (j == 2) {
					html += "<td><input type='text' name='"+lower_activities_names[i]+"' value='";
					if (act[k] > 0) {
						html += act[k];
					}
					html += "' /></td>";
					k++;
					l++;
				}	
			}
			html += 	"</tr>";
		}
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Part C - Time spent at different types of sub activities</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Activity type</td><td><b>Activity code</td><td colspan='4'><b>Description</td><td><b>Sum</td>";
		html += 		"</tr>";
		k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					html += "<td>"+activity_type[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+activity_code[i]+"</td>";
				}
				if (j == 2) {
					html += "<td colspan='4'>"+activity_description[i]+"</td>";
				}
				if (j == 3) {
					html += "<td></td>";
				}
			}
			html += 	"</tr>";
		}	
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Del D - Signatur</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Signed</td><td colspan='5'></td><td></td>";
		html += 		"</tr>";
		
		html += 	"</table>";
		html += 	"<input type='submit' value='Save'/>";
		html += "</form>";
		
		return html;
	}
	
	/**
	 * Generates a String representation of a time report HTML-form pre-filled with data specified by the weekNumber and session parameter.
	 * @param weekNumber: Specifies the week number for the report that should be created.
	 * @param session: Contain information about the user which should be pre-filled in the html-form.
	 */
	public static String newReport(int weekNumber, String user_name, String group) {
		name = user_name;
		Calendar cal = Calendar.getInstance();
		date = new Date(cal.getTimeInMillis());
		project_group = group;
		week_number = weekNumber;
		
		String html = "";
		
		html += "<style type='text/css'>"
				+ "table {border: 1px solid black; border-collapse: collapse; width: 700px; text-align: left;}"
				+ "td {border: 1px solid black; width: 100px;}"
				+ ".grey {background-color: #dddddd;}"
				+ "input {width: 100px;}"
				+ "</style>";
		
		html += "<form method='post' action='TimeReporting?action=addNewReport&function=addNew&week="+week_number+"'>";
		html += 	"<table>";
		html += 		"<tr>";
		html +=				"<td><b>Namn</td><td colspan='4'>"+name+"</td><td><b>Datum</td><td>"+date.toString()+"</td>";
		html +=			"</tr>";		
		html += 		"<tr>";
		html +=				"<td><b>Projektgrupp</td><td colspan='4'>"+project_group+"</td><td><b>Vecka</td><td>"+week_number+"</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td colspan='6' class='grey'><b>Part A - Total time this week (minutes)</td><td></td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Part B - Number of minutes per activity</td>";
		html +=			"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Number</td><td><b>Activity</td><td><b>U</td><td><b>I</td><td><b>F</td><td><b>O</td><td><b>Total time</td>";
		html +=			"</tr>";
		int k = 0;
		int l = 0;
		for (int i = 0; i < 9; i++) {
			html += 	"<tr>";
			for (int j = 0; j < 7; j++) {
				if (j == 0) {
					html += "<td>"+upper_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+upper_activities_names[i]+"</td>";
				}
				if (1 < j && j < 6) {
					html += "<td><input type='text' name='"+act_sub_names[l]+"' /></td>";
					l++;
				}
				if ( j == 6) {
					html += "<td></td>";
				}
			}
			html += 	"</tr>";
		}
		html +=			"<tr>";
		html += 			"<td><b>Sum</td><td class='grey'></td><td></td><td></td><td></td><td></td><td class='grey'></td>";			
		html +=			"</tr>";
		k = 0;
		for (int i = 0; i < 9; i++) {
			html += 	"<tr>";
			for (int j = 0; j < 3; j++) {
				if (j == 0) {
					html += "<td>"+lower_numbers[i]+"</td>";
				}
				if (j == 1) {
					html += "<td colspan='5'>"+lower_activities_names[i]+"</td>";
				}
				if (j == 2) {
					html += "<td><input type='text' name='"+lower_activities_names[i]+"' /></td>";
				}	
			}
			html += 	"</tr>";
		}
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Part C - Time spent at different types of sub activities</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Activity type</td><td><b>Activity code</td><td colspan='4'><b>Description</td><td><b>Sum</td>";
		html += 		"</tr>";
		k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					html += "<td>"+activity_type[i]+"</td>";
				}
				if (j == 1) {
					html += "<td>"+activity_code[i]+"</td>";
				}
				if (j == 2) {
					html += "<td colspan='4'>"+activity_description[i]+"</td>";
				}
				if (j == 3) {
					html += "<td></td>";
				}
			}
			html += 	"</tr>";
		}	
		html += 		"<tr>";
		html += 			"<td colspan='7' class='grey'><b>Del D - Signatur</td>";
		html += 		"</tr>";
		html += 		"<tr>";
		html += 			"<td><b>Signed</td><td colspan='5'></td><td></td>";
		html += 		"</tr>";
		
		html += 	"</table>";
		html += 	"<input type='submit' value='Save'/>";
		html += "</form>";
		
		return html;
	}
}
