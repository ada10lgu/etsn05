package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.*;

public class GenerellaKravTest extends PussTest {

	/**
	 * Alla typer av inloggade användare har tillgång till menyn på samtliga
	 * sidor som visas av systemet [SRS krav 6.1.1]
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	@Test
	public void FT1_1_1() throws SQLException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		final String GROUP = "menygrupp";
		final String LEADER = "victor";
		final String MEMBER = "mrsmith";
		
		addGroup(GROUP);
		addUser(LEADER, LEADER, 0);
		addUser(MEMBER, MEMBER, 0);
		addUserToGroup(LEADER, GROUP, "Project Leader");
		addUserToGroup(MEMBER, GROUP, "t1");
		
		String[] adminPages = new String[3];
		adminPages[0] = GROUP_ADMIN_URL;
		adminPages[1] = START_URL;
		//adminPages[2] = ;
		
		String[] leaderPages = new String[5];
		leaderPages[0] = PROJECT_LEADER_URL;
		leaderPages[1] = TIMEREPORTING_URL;
		leaderPages[2] = REPORT_HANDLING_URL;
		leaderPages[3] = CHANGE_PASSWORD_URL;
		leaderPages[4] = START_URL;
		
		String[] memberPages = new String[12];
		//memberPages[0] = ;
		
		
		final WebClient webClient = new WebClient();
		
		HtmlPage page = login(ADMIN_USERNAME, ADMIN_PASSWORD, GROUP);
		
		page = login(LEADER, LEADER, GROUP);
		
		for(int i = 0; i < leaderPages.length; i++) {
			page = webClient.getPage(leaderPages[i]);
			String html = page.asText();
			Assert.assertTrue("The meny does not exist on page: " + leaderPages[i], html.contains("<div class='menu'>"));
		}
		
		
		deleteUser("mrsmith");
		deleteUser("victor");
		deleteGroup("menygrupp");
	}

	@Ignore
	public void FT1_1_2() {

	}

	@Ignore
	public void FT1_1_3() {

	}
	
	@Ignore
	public void FT1_1_5(){
		String g = "SouthPark";
		String pOne = "Cartman";
		String passpOne = "pass12";
		String pTwo = "Kenny";
		String passpTwo = "pass34";
		String mOne = "Garrison";
		String passmOne = "pass56";
		
		int idpOne = -1;
		int idpTwo = -1;
		int idmOne = -1;
		int idg = -1;
		
		String pl = "Project Leader";
		String t1 = "t1";
		String t4 = "t4";
		
		try {
			idg = addGroup(g);
			idpOne = addUser(pOne, passpOne, 0);
			idpTwo = addUser(pTwo, passpTwo, 0);
			idmOne = addUser(mOne, passmOne, 0);
			assignGroup(idpOne, idg, pl);
			assignGroup(idpTwo, idg, pl);
			assignGroup(idmOne, idg, t1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HtmlPage page = null;
		try {
			page = login("admin", "adminpw", g);
			HtmlAnchor anchor = page.getAnchorByHref("ProjectLeader");
			anchor.click();
			anchor = page.getAnchorByHref("ProjectLeader?groupID=" + idg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			deleteGroup(g);
			deleteUser(mOne);
			deleteUser(pOne);
			deleteUser(pTwo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
