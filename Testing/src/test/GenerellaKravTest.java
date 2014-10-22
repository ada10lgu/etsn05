package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import org.junit.*;

public class GenerellaKravTest extends PussTest {

	private void checkMenuAs(String username, String password,
			String groupName, String[] userPages, String expectedMenu)
			throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = null;
		page = login(username, password, groupName);

		for (int i = 0; i < userPages.length; i++) {
			page = switchPage(page, userPages[i]);
			WebResponse response = page.getWebResponse();
			String content = response.getContentAsString();

			int menuStartIndex = content.indexOf("<div class='menu'>");
			int menuEndIndex = content.indexOf("</div>") + "</div>".length();

			Assert.assertFalse("Menu not available on page: "
						+ page.getUrl().toString() + " for user: " + username, menuStartIndex < 0 || menuEndIndex < "</div>".length());

			String menu = content.substring(menuStartIndex, menuEndIndex);

			Assert.assertEquals("The menu does not look right at page: "
					+ userPages[i] + "for user: " + username, expectedMenu,
					menu);
		}
	}

	/**
	 * Alla typer av inloggade användare har tillgång till menyn på samtliga
	 * sidor som visas av systemet [SRS krav 6.1.1]
	 */
	@Test
	public void FT1_1_1() throws SQLException, MalformedURLException,
			IOException {
		final String group = "menygrupp";
		final String leader = "victor";
		final String member = "mrsmith";

		final String expectedAdminMenu = "<div class='menu'><ul><li><a href='Administration'>Administration</a><ul><li><a href='Administration'>Users</a></li><li><a href='ProjectGroupAdmin'>Group</a></li></ul></li><li><a href='ProjectLeader'>Project Management</a><ul><li><a href='ProjectLeader'>Users</a></li><li><a href='ReportHandling'>Reports</a></li><li><a href='Statistics'>Statistics</a></li></ul></li><li><a href='LogIn'>Logout</a></li></ul></div>";
		final String expectedLeaderMenu = "<div class='menu'><ul><li><a href='ProjectLeader'>Project Management</a><ul><li><a href='ProjectLeader'>Users</a></li><li><a href='ReportHandling'>Reports</a></li><li><a href='Statistics'>Statistics</a></li></ul></li><li><a href='TimeReporting?function=view'>Time Reports</a><ul><li><a href='TimeReporting?function=view'>View</a></li><li><a href='TimeReporting?function=update'>Update</a></li><li><a href='TimeReporting?function=new'>New</a></li><li><a href='TimeReporting?function=statistics'>Statistics</a></li></ul></li><li><a href='ChangePassword'>Change Password</a></li><li><a href='LogIn'>Logout</a></li></ul></div>";
		final String expectedMemberMenu = "<div class='menu'><ul><li><a href='TimeReporting?function=view'>Time Reports</a><ul><li><a href='TimeReporting?function=view'>View</a></li><li><a href='TimeReporting?function=update'>Update</a></li><li><a href='TimeReporting?function=new'>New</a></li><li><a href='TimeReporting?function=statistics'>Statistics</a></li></ul></li><li><a href='ChangePassword'>Change Password</a></li><li><a href='LogIn'>Logout</a></li></ul></div>";

		addGroup(group);
		addUser(leader, leader, 0);
		addUser(member, member, 0);
		addUserToGroup(leader, group, "Project Leader");
		addUserToGroup(member, group, "t1");

		String[] adminPages = { GROUP_ADMIN, ADMINISTRATION, PROJECT_LEADER,
				REPORT_HANDLING, STATISTICS };

		String[] leaderPages = { PROJECT_LEADER, TIMEREPORTING,
				TIMEREPORTING_UPDATE, TIMEREPORTING_NEW, REPORT_HANDLING,
				CHANGE_PASSWORD };

		String[] memberPages = { TIMEREPORTING, TIMEREPORTING_UPDATE,
				TIMEREPORTING_NEW, TIMEREPORTING_STATISTICS, CHANGE_PASSWORD };

		checkMenuAs(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP, adminPages,
				expectedAdminMenu);
		checkMenuAs(leader, leader, group, leaderPages, expectedLeaderMenu);
		checkMenuAs(member, member, group, memberPages, expectedMemberMenu);
	}

	/**
	 * Menyn ska ge tillgång till de funktionaliteter som en användare besitter
	 * [SRS krav 6.1.2]
	 */
	@Test
	public void FT1_1_2() {
		// FT1_1_1 testar detta.
	}

	/**
	 * Menyns innehåll ska vara samma på varje sida som visas av systemet [SRS
	 * krav 6.1.3]
	 */
	@Test
	public void FT1_1_3() {
		// FT1_1_1 testar detta.
	}

	/**
	 * Försök ge inkorrekt input till systemet (felaktiga tecken,
	 * SQL-injections) [SRS krav 6.1.4]
	 */
	@Test
	public void FT1_1_4() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException {
		HtmlPage page = login(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP);
		page = switchPage(page, ADMINISTRATION);
		webClient.getPage(ADMINISTRATION_URL + "?deletename=" + ADMIN_USERNAME + "&deleteid=1");
		ResultSet rs = getUserByName(ADMIN_USERNAME);
		assertTrue("The admin was deleted.", rs.next());
	}

	/**
	 * I en projektgupp får det finnas max två stycken projektledare och tre
	 * typer av roller: t1, t2, och t3. [SRS krav 6.1.6]
	 */
	@Ignore
	public void FT1_1_5() throws SQLException, MalformedURLException,
			IOException {
		final String group = "endast";
		final String leader1 = "maxtva";
		final String leader2 = "maxen";
		final String member = "member";

		String groupNbr = Integer.toString(addGroup(group));
		addUser(leader1, leader1, 0);
		addUser(leader2, leader2, 0);
		String memberNbr = Integer.toString(addUser(member, member, 0));

		addUserToGroup(leader1, group, "Project Leader");
		addUserToGroup(leader2, group, "Project Leader");

		HtmlPage page = login(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP);
		page = switchPage(page, GROUP_ADMIN);
		page = switchPage(page, GROUP_ADMIN + "?editid=" + groupNbr);
		
		HtmlForm form = page.getFormByName("input");
		
		HtmlInput radio = form.getInputByValue(memberNbr);
		radio.setChecked(true);
		
		HtmlSelect roleList = form.getSelectByName("role");
		roleList.setSelectedAttribute("Project Leader", true);
		
		HtmlSubmitInput button = form.getInputByValue("Add user");

		System.in.read();
		button.click();
	}

	/**
	 * Varje projekt har minst en och max två användare som besitter rollen som
	 * projektledare [SRS krav 6.1.8]
	 * 
	 * @throws SQLException
	 */
	@Ignore
	public void FT1_2_1() throws SQLException, FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		final String group = "thegroup";
		final String user1 = "andreas";
		final String user2 = "greger";
		final String user3 = "fusroad";

		String groupNbr = Integer.toString(addGroup(group));
		String memberNbr1 = Integer.toString(addUser(user1, user1, 0));
		String memberNbr2 = Integer.toString(addUser(user2, user2, 0));
		String memberNbr3 = Integer.toString(addUser(user3, user3, 0));

		HtmlPage page = login(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP);
		page = switchPage(page, GROUP_ADMIN);
		
		HtmlForm form = page.getFormByName("input");
		HtmlTextInput projectName = form.getInputByName("projectname");
		projectName.setValueAttribute("watyoudo");
		
		HtmlSubmitInput button = form.getInputByValue("Add project");
		page = button.click();
		
		assertEquals("Could not start adding a new group.", GROUP_HANDLING_URL, page.getUrl().toString());
	
		form = page.getFormByName("input");
		
		HtmlInput radio = form.getInputByName("selectedradiouser");
		radio = form.getInputByValue(memberNbr1);
		radio.setChecked(true);
		
		HtmlSelect roleList = form.getSelectByName("role");
		roleList.setSelectedAttribute("Project Leader", true);
		
		button = form.getInputByValue("Add user");
		button.click();
		
		System.in.read();
	}

	/**
	 * Försök lägga till administratören i en projektgrupp [SRS krav 6.1.11]
	 */
	@Ignore
	public void FT1_3_1() {

	}

	/**
	 * När man tar bort en projektledare bekräftar man bortagningen genom en
	 * dialogruta, väljer man “Ja” tas projektledaren bort och man dirigeras
	 * till en uppdaterad lista av användarna [SRS krav 6.1.14]
	 */
	@Ignore
	public void FT1_4_1() {

	}

	/**
	 * När man tar bort en vanlig användare bekräftar man bortagningen genom en
	 * dialogruta, väljer man “Ja” tas den vanliga användaren bort och man
	 * dirigeras till en uppdaterad lista av användarna [SRS krav 6.1.14]
	 */
	@Ignore
	public void FT1_4_2() {

	}

	/**
	 * När man tar bort en projektledare bekräftar man bortagningen genom en
	 * dialogruta, väljer man “Nej” tas projektledaren inte bort och man
	 * dirigeras till listan av användarna [SRS krav 6.1.14]
	 */
	@Ignore
	public void FT1_4_3() {

	}

	/**
	 * När man tar bort en vanlig användare bekräftar man bortagningen genom en
	 * dialogruta, väljer man “Nej” tas den vanliga användaren inte bort och man
	 * dirigeras till listan av användarna [SRS krav 6.1.14]
	 */
	@Ignore
	public void FT1_4_4() {

	}
	
	@Ignore
	public void ST1_2_1() {
		
	}
	
	
}
