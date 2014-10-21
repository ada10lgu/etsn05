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

	private void checkMenuAs(String username, String password,
			String groupName, String[] userPages, String expectedMenu)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		HtmlPage page = login(username, password, groupName);
		for (int i = 0; i < userPages.length; i++) {
			page = webClient.getPage(userPages[i]);
			String html = page.asText();
			html.indexOf("<div class = 'menu'>");
			String menu = html.substring(html.indexOf("<div class = 'menu'>"),
					html.indexOf("<\\div>"));
			Assert.assertEquals("The menu does not look right at page: "
					+ userPages[i] + "for user: " + username, expectedMenu,
					menu);
		}
	}

	/**
	 * Alla typer av inloggade användare har tillgång till menyn på samtliga
	 * sidor som visas av systemet [SRS krav 6.1.1]
	 */
	@Ignore
	public void FT1_1_1() throws SQLException, FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		final String group = "menygrupp";
		final String leader = "victor";
		final String member = "mrsmith";

		final String expectedAdminMenu = "";
		final String expectedLeaderMenu = "";
		final String expectedMemberMenu = "";

		addGroup(group);
		addUser(leader, leader, 0);
		addUser(member, member, 0);
		addUserToGroup(leader, group, "Project Leader");
		addUserToGroup(member, group, "t1");

		String[] adminPages = { GROUP_ADMIN_URL, START_URL, ADMINISTRATION_URL };

		String[] leaderPages = { PROJECT_LEADER_URL, TIMEREPORTING_URL,
				REPORT_HANDLING_URL, CHANGE_PASSWORD_URL, START_URL };

		String[] memberPages = { START_URL, ADMINISTRATION_URL };

		checkMenuAs(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP, adminPages,
				expectedAdminMenu);
		checkMenuAs(leader, leader, group, leaderPages, expectedLeaderMenu);
		checkMenuAs(member, member, group, memberPages, expectedMemberMenu);
	}

	/**
	 * Menyn ska ge tillgång till de funktionaliteter som en användare besitter
	 * [SRS krav 6.1.2]
	 */
	@Ignore
	public void FT1_1_2() {
		// FT1_1_1 testar detta.
	}

	/**
	 * Menyns innehåll ska vara samma på varje sida som visas av systemet [SRS
	 * krav 6.1.3]
	 */
	@Ignore
	public void FT1_1_3() {
		// FT1_1_1 testar detta.
	}

	/**
	 * Försök ge inkorrekt input till systemet (felaktiga tecken,
	 * SQL-injections) [SRS krav 6.1.4]
	 */
	@Ignore
	public void FT1_1_4() {

	}

	/**
	 * I en projektgupp får det finnas max två stycken projektledare och tre
	 * typer av roller: t1, t2, och t3. [SRS krav 6.1.6]
	 */
	@Ignore
	public void FT1_1_5() throws SQLException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		final String group = "endast";
		final String leader1 = "maxtva";
		final String leader2 = "maxen";
		final String member = "member";
		
		addGroup(group);
		addUser(leader1, leader1, 0);
		addUser(leader2, leader2, 0);
		addUser(member, member, 0);
		
		addUserToGroup(leader1, group, "Project Leader");
		addUserToGroup(leader2, group, "Project Leader");
		addUserToGroup(member, group, "t1");
		
		HtmlPage page = login(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP);
	}

	/**
	 * Varje projekt har minst en och max två användare som besitter rollen som
	 * projektledare [SRS krav 6.1.8]
	 */
	@Ignore
	public void FT1_2_1() {

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
}
