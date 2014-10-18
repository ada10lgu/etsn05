package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AutentiseringTest extends PussTest{
	@Test
	public void FT2_5_4(){
		
		String groupname = "groupz";
		String username = "Cartman";
		String password = "pass12";

		
		String role = "t1";
		
		int is_admin = 0;
		int groupId = -1;
		int userId = -1;
		try {
			//TODO ändra till clearDatabase();
			deleteGroup(groupname);
			deleteUser(username);
			userId = addUser(username, password, is_admin);
			groupId = addGroup(groupname);
			assignGroup(userId, groupId, role);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		HtmlPage page = null;
		try {
			page = login(username, password, groupname);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(username + " could not log in", START_URL, page.getUrl().toString());
		
		try {
			deleteGroup(groupname);
			deleteUser(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
