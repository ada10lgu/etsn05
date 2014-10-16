package services;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

public final class ReportGenerator {

	private ReportGenerator() {
		
	}
	
	/**
	 * Generates a String-representation of a time report containing the data specified by the data parameter.
	 * @param data: Contains the data that should be printed in the time report.
	 */
	public static String viewReport(ResultSet data) {
		try {
			String name = data.getString("username");
			Date date = data.getDate("date");
			String project_group = data.getString("name");
			int week_number = data.getInt("week");
			int total_time = data.getInt("total_time");
			int signed = data.getInt("signed");
			
			int[] act_sub = new int[45];
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
			
			int[] act = new int[9];
			act[0] = data.getInt("Funktionstest");
			act[1] = data.getInt("Systemtest");
			act[2] = data.getInt("Regressionstest");
			act[3] = data.getInt("Meeting");
			act[4] = data.getInt("Lecture");
			act[5] = data.getInt("Excersice");
			act[6] = data.getInt("Terminal");
			act[7] = data.getInt("Study");
			act[8] = data.getInt("Other");
			
			int[] sub = new int[4];
			sub[0] = act_sub[0] + act_sub[5] +act_sub[10] +act_sub[15] + act_sub[20] + act_sub[25] +act_sub[30] +act_sub[35] + act_sub[40];
			sub[1] = act_sub[1] + act_sub[6] +act_sub[11] +act_sub[16] + act_sub[21] + act_sub[26] +act_sub[31] +act_sub[36] + act_sub[41];
			sub[3] = act_sub[2] + act_sub[7] +act_sub[12] +act_sub[17] + act_sub[22] + act_sub[27] +act_sub[32] +act_sub[37] + act_sub[42];
			sub[4] = act_sub[3] + act_sub[8] +act_sub[13] +act_sub[18] + act_sub[23] + act_sub[28] +act_sub[33] +act_sub[38] + act_sub[43];	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String html = "";
		
		html += "<form>";
		html += 	"<tr>";
		html += 	"</tr>";		
		html += "</form>";
		
		return null;
	}
	
	/**
	 * Generates a String-representation of a time report HTML-form pre-filled with the data specified by the data parameter.
	 * @param data: Contains the data that should be printed in the time report.
	 */
	public static String updateReport(ResultSet data) {
		return null;
	}
	
	/**
	 * Generates a String representation of a time report HTML-form pre-filled with data specified by the weekNumber and session parameter.
	 * @param weekNumber: Specifies the week number for the report that should be created.
	 * @param session: Contain information about the user which should be pre-filled in the html-form.
	 */
	public static String newReport(int weekNumber, HttpSession session) {
		return null;
	}
}
