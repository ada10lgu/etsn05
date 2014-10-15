import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;



public class Access {

	private Connection conn;
	
	public Access(Connection conn){
		this.conn = conn;
	}
	
	
	/**
	 * Updates the log with a new timestamps for the given user and session.
	 * @param userID: The requesting users id.
	 * @param session: The requesting users session id.
	 * @return boolean - True if the user has not been inactive for too long.
	 */
	public boolean updateLog(int userID, String session){
		Timestamp newTS = new Timestamp(System.currentTimeMillis());	
		long accessTime = 20*60*1000;  			//Efter hur många inaktiva millisekunder man ska loggas ut 
		try {
			Statement stmt = conn.createStatement();		
			ResultSet rs = stmt.executeQuery("Select * from log where user_id = '" +userID+ "' AND session = '" +session+ "'");
			
			if(rs.first()){
				Timestamp oldTS = rs.getTimestamp("time");
				if(newTS.getTime() - oldTS.getTime() > accessTime){
					return false;
				}
				stmt.executeUpdate("Update log SET time = '" + newTS +"' where user_id = '" +userID+ "' AND session = '" +session+ "'");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return false;	
	}
	
	/**
	 * Updates the database, sets the user as logged in, stores the current session id and current
	 * timestamp for the requesting user.
	 * @param userID: The requesting users id.
	 * @param session: The requesting users session id.
	 * @return boolean - True if the user is not already logged in
	 */
	public boolean logInUser(int userID, String session){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			Timestamp newTS = new Timestamp(System.currentTimeMillis());
			
			ResultSet rs = stmt.executeQuery("Select * from log where user_id = " +userID+ " AND session = '" +session+ "'");
			if(rs.first()){
				stmt.executeUpdate("Update log SET time = '" + newTS +"' where user_id = " +userID+ " AND session = '" +session+ "'");
				return true;
			} 			
			stmt.executeUpdate("Insert into log (user_id, time, session) values(" +userID+ ", '" +newTS+ "', '" +session+ "')");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Updates the database, sets the user as logged out, removes the current session id and current
	 * timestamp for the requesting user.
	 * @param userID: The requesting users id.
	 * @param session: The requesting users session id.
	 * @return boolean - True if the user is not already logged out.

	 */
	public boolean logOutUser(int userID, String session){
		//TODO
		return false;
	}
	
}