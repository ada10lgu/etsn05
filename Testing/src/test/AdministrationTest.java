package test;

import java.sql.SQLException;

import org.junit.Test;

public class AdministrationTest extends PussTest{
	@Test
	public void FT2_5_4(){
		try {
			addUser("Kenny", "passwo",0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
