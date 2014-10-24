package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class AdministrationTest extends PussTest {

	@Ignore
	public void FT4_1_1() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException {
		clearDatabase();
		final WebClient webClient = new WebClient();

		// Get the first page
		final HtmlPage page1 = webClient.getPage(LOGIN_URL);

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("input");

		final HtmlSubmitInput button = form.getInputByValue("Submit");
		final HtmlTextInput userField = form.getInputByName("user");
		final HtmlPasswordInput passwordField = form.getInputByName("password");

		// Change the value of the text field
		userField.setValueAttribute("admin");
		passwordField.setValueAttribute("adminpw");

		// Now submit the form by clicking the button and get back the second
		// page.
		final HtmlPage page2 = button.click();

		assertEquals("admin could not log in", START_URL, page2.getUrl().toString());

		HtmlAnchor administrationpage = page2.getAnchorByHref("Administration");
		final HtmlPage page3 = administrationpage.click();

		assertEquals("Administration are not acces to administrations page", ADMINISTRATION_URL, page3.getUrl().toString());

		clearDatabase();

		webClient.closeAllWindows();

	}

	@Ignore
	public void FT4_1_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException {
		clearDatabase();
		final WebClient webClient = new WebClient();

		// Get the first page
		final HtmlPage page1 = webClient.getPage(LOGIN_URL);

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("input");

		final HtmlSubmitInput button = form.getInputByValue("Submit");
		final HtmlTextInput userField = form.getInputByName("user");
		final HtmlPasswordInput passwordField = form.getInputByName("password");
		final HtmlSelect groupList = form.getSelectByName("groupID");

		// Change the value of the text field
		userField.setValueAttribute("jonatan");
		passwordField.setValueAttribute("jonatan");
		groupList.setSelectedAttribute("91", true);

		// Now submit the form by clicking the button and get back the second
		// page.
		final HtmlPage page2 = button.click();

		assertEquals("user could not log in", START_URL, page2.getUrl().toString());

		HtmlAnchor administrationpage = page2.getAnchorByHref("Administration");
		final HtmlPage page3 = administrationpage.click();

		assertEquals("Vanligt avnändare kan åtkom åt administration sida", START_URL, page3.getUrl().toString());

		clearDatabase();

		webClient.closeAllWindows();

	}

	private HtmlPage nameNewGroup(HtmlPage page, String groupName) throws IOException {
		final HtmlForm form1 = page.getFormByName("input");
		final HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		final HtmlTextInput projectNameField1 = form1.getInputByName("projectname");
		projectNameField1.setValueAttribute(groupName);
		return page = button1.click();
	}

	private HtmlPage addUserToGroup(HtmlPage page, int userId, String group, String role) throws Exception {
		ResultSet rs = sendSQLQuery("select username from users where id = " + userId + ";");
		rs.next();
		String username = rs.getString(1);

		System.out.println("lägg till användare med namn: " + username + " med roll: " + role + " i grupp: " + group);
		System.in.read();
		return page;
	}

	private HtmlPage addInitialUserToGroup(HtmlPage page, int userId) throws Exception {

		// ResultSet rs = sendSQLQuery("select username from users where id = "
		// + userId + ";");
		// rs.next();
		// String username = rs.getString(1);
		//
		// System.out.println("lägg till användare med namn: " + username +
		// " med roll: " + role + " i grupp: " + group);
		// System.in.read();

		List<DomElement> list1 = page.getElementsByIdAndOrName("selectedradiouser");

		HtmlRadioButtonInput radioButton = null;
		for (DomElement element : list1) {
			radioButton = (HtmlRadioButtonInput) element;
			if (String.valueOf(userId).equals(radioButton.getValueAttribute())) {
				radioButton.click();
				break;
			}
		}

		HtmlForm form = page.getFormByName("input");
//		final HtmlSelect groupList = form.getSelectByName("role");
//		groupList.setSelectedAttribute(groupList.getOptionByText(role), true);

		HtmlSubmitInput button = form.getInputByValue("Add user");
		page = button.click();

		return page;
	}

	private HtmlPage changeUserRole(HtmlPage page, String username, String role) throws Exception {
		List<DomElement> elements = page.getElementsByIdAndOrName("changename");
		for (DomElement element : elements) {
			HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) element;
			if (radioButton.getValueAttribute().equals(username)) {
				radioButton.click();
				break;
			}
		}

		HtmlForm form = page.getFormByName("input");

		final HtmlSelect groupList = form.getSelectByName("role");
		groupList.setSelectedAttribute(groupList.getOptionByText(role), true);

		HtmlSubmitInput button = form.getInputByValue("Change role");
		return button.click();
	}

	@Test
	public void FT4_4_1() throws Exception {
		// förberedelser
		String username = "user1";
		String password = "pass12";
		String group = null; // admin behöver inte ange grupp
		String tooShortGroupName = "brie";
		String tooLongGroupName = "creamcheese";
		String badCharGroupName = "cheddar?";

		addUser(username, password, 0);
		HtmlPage page = login("admin", "adminpw", null);

		assertEquals("Admin blev inte inloggad", START_URL, page.getUrl().toString());

		// navigera till projektgruppsadministrationssidan.

		page = getPageByAnchor(page, "ProjectGroupAdmin");

		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för kort namn
		page = nameNewGroup(page, tooShortGroupName);

		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för kort namn", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för långt namn
		page = nameNewGroup(page, tooLongGroupName);
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för långt namn", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med namn som innehåller olämpligt tecken
		page = nameNewGroup(page, badCharGroupName);
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för långt namn", GROUP_ADMIN_URL, page.getUrl().toString());

		ResultSet rs = sendSQLQuery("select count(*) from groups;");
		rs.next();
		assertEquals("Grupper har lagts till", 0, rs.getInt(1));
		// TODO kontrollera att databsen är tom
	}

	@Test
	public void FT4_4_2() throws Exception {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String projectGroupName1 = "gouda";
		String projectGroupName2 = "mozzarella";
		int userId = addUser(username, password, 0);
		HtmlPage page = login("admin", "adminpw", null);

		// navigera till projektgruppsadministrationssidan.
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp
		page = nameNewGroup(page, projectGroupName1);
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1, GROUP_ADMIN_URL, page.getUrl().toString());

		page = addInitialUserToGroup(page, userId);

		// navigera till projektgruppsadministrationssidan.
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		// TODO är på fel sida
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp
		page = nameNewGroup(page, projectGroupName2);
		addInitialUserToGroup(page, userId);

		// make sure the right content is in the database
		ResultSet rs = sendSQLQuery("select * from groups where name = '" + projectGroupName1 + "';");
		System.out.println(projectGroupName1);
		if (rs.next()) {
			assertEquals(projectGroupName1, rs.getString("name"));
		} else {
			fail("Det finns ingen group med namnet: " + projectGroupName1);
		}

		rs = sendSQLQuery("select * from groups where name = '" + projectGroupName2 + "';");
		if (rs.next()) {
			assertEquals(projectGroupName2, rs.getString("name"));
		} else {
			fail("Det finns ingen group med namnet: " + projectGroupName2);
		}

		rs = sendSQLQuery("select * from groups where name <> '" + projectGroupName1 + "' and name <> '" + projectGroupName2 + "';");
		assertTrue("Det finns fler grupper än de två som har lagts in", !rs.next());

	}

	@Test
	public void FT4_4_3() throws Exception {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String group = null;
		String projectGroupName = "getost";
		int userId = -1;
		int groupId = -1;
		userId = addUser(username, password, 0);
		groupId = addGroup(projectGroupName);
		HtmlPage page = null;
		page = login("admin", "adminpw", null);

		// navigera till projektgruppsadministrationssidan.
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp

		page = nameNewGroup(page, projectGroupName);

		assertEquals("Admin kunde skapa en grupp med ett upptaget namn", GROUP_ADMIN_URL, page.getUrl().toString());
		assertTrue("Inget felmeddelande visas", page.asText().contains("Error: Suggested project group name not possible to add"));
	}

	@Test
	public void FT4_4_4() throws Exception {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String[] groupNames = { "grupp1", "grupp2", "grupp3", "grupp4", "grupp5" };
		int userId = -1;
		userId = addUser(username, password, 0);
		HtmlPage page = null;
		page = login("admin", "adminpw", null);

		// lägg till fem grupper
		for (String groupName : groupNames) {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
			page = nameNewGroup(page, groupName);
//			assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + groupName, GROUP_HANDLING_URL, page.getUrl().toString());
			page = addInitialUserToGroup(page, userId);
		}

		// kontrollera att rätt grupper finns i databasen
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("Fel antal grupper finns i databasen", 5, rs.getInt(1));
	}

	@Test
	public void FT4_4_5() throws Exception {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String[] groupNames = { "grupp1", "grupp2", "grupp3", "grupp4", "grupp5" };
		String groupName6 = "grupp6";
		int userId = -1;
		userId = addUser(username, password, 0);
		HtmlPage page = login("admin", "adminpw", null);
		// lägg till fem grupper
		for (String groupName : groupNames) {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
			page = nameNewGroup(page, groupName);
			page = addInitialUserToGroup(page, userId);
		}

		// försök lägga till projektgrupp #6
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		page = nameNewGroup(page, groupName6);
		assertEquals("Admin kunde skapa en sjätte projektgrupp med namnet: " + groupName6, GROUP_ADMIN_URL, page.getUrl().toString());

		// kontrollera att rätt grupper finns i databasen
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("Fel antal grupper finns i databasen", 5, rs.getInt(1));
		rs = sendSQLQuery("select count(*) from groups where name = '" + groupName6 + "';");
		rs.next();
		assertEquals(groupName6 + "finns i databasen", 0, rs.getInt(1));
	}

	@Test
	public void FT4_4_6() throws Exception {
		// TODO trasigt test?

		// förberedelser
		String username1 = "user1";
		String password1 = "pass12";
		String username2 = "user2";
		String password2 = "pass12";
		String role = "t1";
		int userId2 = -1;

		String[] groupNames = { "group1", "group2" };
		int[] groupIds = { -1, -1 };
		addUser(username1, password1, 0);
		userId2 = addUser(username2, password2, 0);
		for (int i = 0; i < groupNames.length; i++) {
			groupIds[i] = addGroup(groupNames[i]);
			addUserToGroup(username1, groupNames[i], "Project Leader");
		}
		HtmlPage page = null;
		page = login("admin", "adminpw", null);

		// lägg till användare två i grupperna
		for (int i = 0; i < groupNames.length; i++) {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
			page = getPageByAnchor(page, "ProjectGroupAdmin?editid=" + groupIds[i]);
			assertEquals("admin är inte på gruppediteringssidan", GROUP_HANDLING_URL, page.getUrl().toString());
			// System.in.read();
			page = addUserToGroup(page, userId2, groupNames[i], role);

			// ResultSet rs =
			// sendSQLQuery("select count(*) from user_group where user_id = " +
			// userId2 + " and group_id = " + groupIds[i] + ";");
			// rs.next();
		}

		// kontrollera att användare två är med i bägge grupperna
		for (int i = 0; i < groupNames.length; i++) {
			ResultSet rs = sendSQLQuery("select count(*) from user_group where user_id = " + userId2 + " and group_id = " + groupIds[i] + ";");
			rs.next();
			assertEquals("användare två finns i för många eller för få grupper", 1, rs.getInt(1));
		}
	}

	@Test
	public void FT4_4_7() throws Exception {
		// TODO rapportera fel
		// förberedelser
		String groupName = "edamer";
		HtmlPage page = login("admin", "adminpw", null);

		// navigera till gruppadministration
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		assertEquals("admin kunde inte navigera till gruppadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());
		page = nameNewGroup(page, groupName);
		page = getPageByAnchor(page, "ProjectGroupAdmin");

		// kontrollera att gruppen inte har lagts till
		ResultSet rs = sendSQLQuery("select count(*) from groups where name = '" + groupName + "';");
		rs.next();
		assertEquals("gruppen lades till utan användare", 0, rs.getInt(1));
	}

	@Test
	public void FT4_4_8() throws Exception {
		// TODO rapportera fel
		// förberedelser
		String username = "user1";
		String password = "pass12";
		String role = "Project Leader";
		String groupName = "group1";
		int userId = -1;

		userId = addUser(username, password, 0);
		int groupId = addGroup(groupName);
		addUserToGroup(username, groupName, role);

		HtmlPage page = login("admin", "adminpw", null);

		ResultSet rs = sendSQLQuery("select count(*) from users where username = '" + username + "'");
		rs.next();
		assertEquals("användaren finns inte från början", 1, rs.getInt(1));

		// navigera till administration
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		// delete user from project
		page = getPageByAnchor(page, "ProjectGroupAdmin?editid=" + groupId);
		page = getPageByAnchor(page, "GroupHandling?deletename=" + userId);

		// kontrollera att användaren inte har raderats
		rs = sendSQLQuery("select count(*) from users where username = '" + username + "'");
		rs.next();
		assertEquals("användaren togs bort trots att den var den enda användaren i gruppen", 1, rs.getInt(1));

	}

	@Test
	public void FT4_4_9() throws Exception {
		// förberedelser
		String username = "user1";
		String password = "pass12";
		String groupName = "edamer";

		int userId = addUser(username, password, 0);

		// navigera till projektadministrationssisdan
		HtmlPage page = login("admin", "adminpw", null);
		page = getPageByAnchor(page, "ProjectGroupAdmin");

		// skapa ny projektgrupp
		page = nameNewGroup(page, groupName);
		page = addInitialUserToGroup(page, userId);

		// navigera bort från skapandet av projektgrupp
		page = getPageByAnchor(page, "ProjectGroupAdmin");

		// kontrollera att gruppen finns och att användaren är i gruppen
		ResultSet rs = sendSQLQuery("select count(*) from groups where name = '" + groupName + "';");
		rs.next();
		assertEquals("gruppen blev inte tillagd", 1, rs.getInt(1));

		rs = sendSQLQuery("select id from groups where name = '" + groupName + "';");
		rs.next();
		int groupId = rs.getInt(1);
		try {
			rs = sendSQLQuery("select count(*) from user_group where user_id = " + userId + " and group_id = " + groupId + ";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		rs.next();
		assertEquals("användaren finns inte i gruppen", 1, rs.getInt(1));

	}

	@Test
	public void FT4_4_10() throws Exception {
		// förberedelser
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String role1 = "t1";
		String role2 = "t2";
		int totalNumberOfUsers = 20;
		int groupId = addGroup(groupName);
		String username = null;
		int userId = -1;
		for (int i = 0; i < totalNumberOfUsers; i++) {
			username = usernameTemplate + i;
			userId = addUser(username, password, 0);
			if (i < totalNumberOfUsers - 1) {
				addUserToGroup(username, groupName, role1);
			}
		}
		HtmlPage page = login("admin", "adminpw", null);

		// navigera till gruppadministration
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		page = getPageByAnchor(page, "ProjectGroupAdmin?editid=" + groupId);
		assertEquals("admin är inte på group handling sidan", GROUP_HANDLING_URL, page.getUrl().toString());

		// lägg till en till användare
		addUserToGroup(page, userId, groupName, role2);

		// kontrollera att det finns 20 användare i gruppen
		ResultSet rs = sendSQLQuery("select count(*) from user_group where group_id = " + groupId + ";");
		rs.next();
		assertEquals("fel antal användare i gruppen", totalNumberOfUsers, rs.getInt(1));
	}

	@Test
	public void FT4_4_11() throws Exception {
		// förberedelser
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String role1 = "t1";
		String role2 = "t2";
		int totalNumberOfUsers = 21;
		int groupId = addGroup(groupName);
		String username = null;
		int userId = -1;
		for (int i = 0; i < totalNumberOfUsers; i++) {
			username = usernameTemplate + i;
			userId = addUser(username, password, 0);
			if (i < totalNumberOfUsers - 1) {
				addUserToGroup(username, groupName, role1);
			}
		}
		HtmlPage page = login("admin", "adminpw", null);

		// navigera till gruppadministration
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		page = getPageByAnchor(page, "ProjectGroupAdmin?editid=" + groupId);
		assertEquals("admin är inte på group handling sidan", GROUP_HANDLING_URL, page.getUrl().toString());

		// lägg till en till användare
		addUserToGroup(page, userId, groupName, role2 );

		// kontrollera att det finns 20 användare i gruppen och att den 21:a
		// användaren inte är tillagd i gruppen
		ResultSet rs = sendSQLQuery("select count(*) from user_group where group_id = " + groupId + ";");
		rs.next();
		assertEquals("fel antal användare i gruppen", totalNumberOfUsers - 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where group_id = " + groupId + " and user_id = " + userId + ";");
		rs.next();
		assertEquals("för många användare tillagda i gruppen", 0, rs.getInt(1));
	}

	@Test
	public void FT4_4_12() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String role1 = "t1";
		String[] roles = { "t1", "t2", "t3" };
		int numberOfUsers = roles.length;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, role1);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			page = changeUserRole(page, usernameTemplate + i, roles[i]);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from user_group where role <> 'Project Leader';");
		rs.next();
		assertEquals("fel antal användare i gruppen", numberOfUsers, rs.getInt(1));

		for (int i = 0; i < numberOfUsers; i++) {
			rs = sendSQLQuery("select role from user_group where user_id = " + userIds[i] + ";");
			rs.next();
			assertEquals("en användare har fel roll", roles[i], rs.getString(1));
		}
	}

	@Test
	public void FT4_4_13() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String role1 = "t1";
		String[] roles = { "t1", "t2", "t3", "t4" };
		int numberOfUsers = roles.length;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, role1);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			try {
				page = changeUserRole(page, usernameTemplate + i, roles[i]);
				assertNotEquals("en användare med rollen " + roles[numberOfUsers - 1] + " las till", roles[i], roles[numberOfUsers - 1]);
			} catch (Exception e) {
				assertEquals("en användare med rollen " + roles[i] + " kunde inte läggas till", roles[i], roles[numberOfUsers - 1]);
			}
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from user_group where role <> 'Project Leader';");
		rs.next();
		assertEquals("fel antal användare i gruppen", numberOfUsers, rs.getInt(1));

		for (int i = 0; i < numberOfUsers - 1; i++) {
			rs = sendSQLQuery("select role from user_group where user_id = " + userIds[i] + ";");
			rs.next();
			if (i < numberOfUsers - 1) {
				assertEquals("en användare har inte tilldeltas sin roll", roles[i], rs.getString(1));
			} else {
				assertNotEquals("en användare har tilldelats rollen " + roles[numberOfUsers - 1], roles[i], rs.getString(1));
			}
		}
	}

	@Test
	public void FT4_4_14() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t2";
		String newRole = "t1";
		int numberOfUsers = 6;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers, rs.getInt(1));
	}

	@Test
	public void FT4_4_15() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t3";
		String newRole = "t2";
		int numberOfUsers = 6;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers, rs.getInt(1));
	}

	@Test
	public void FT4_4_16() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t1";
		String newRole = "t3";
		int numberOfUsers = 6;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers, rs.getInt(1));
	}

	@Test
	public void FT4_4_17() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t2";
		String newRole = "t1";
		int numberOfUsers = 7;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers - 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + initialRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, 1, rs.getInt(1));
	}

	@Test
	public void FT4_4_18() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t3";
		String newRole = "t2";
		int numberOfUsers = 7;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers - 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + initialRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, 1, rs.getInt(1));
	}

	@Test
	public void FT4_4_19() throws Exception {
		// förberedelser
		String leaderName = "leader";
		String usernameTemplate = "user";
		String password = "pass12";
		String groupName = "group1";
		String initialRole = "t1";
		String newRole = "t3";
		int numberOfUsers = 7;
		int[] userIds = new int[numberOfUsers];

		addGroup(groupName);
		addUser(leaderName, password, 0);
		addUserToGroup(leaderName, groupName, "Project Leader");
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			userIds[i] = addUser(username, password, 0);
			addUserToGroup(username, groupName, initialRole);
		}

		HtmlPage page = login(leaderName, password, groupName);

		// navigera till gruppediteringssidan
		assertTrue("Den inloggade användaren är inte project leader", page.asText().contains("Project Management"));
		page = getPageByAnchor(page, "ProjectLeader");

		// byt roll på användarna
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			page = changeUserRole(page, username, newRole);
		}

		// kontrollera att alla har rätt roll
		ResultSet rs = sendSQLQuery("select count(*) from groups");
		rs.next();
		assertEquals("incorrect number of groups", 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + newRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, numberOfUsers - 1, rs.getInt(1));

		rs = sendSQLQuery("select count(*) from user_group where role = '" + initialRole + "';");
		rs.next();
		assertEquals("incorrect number of users with role " + newRole, 1, rs.getInt(1));
	}

	@Test
	public void FT4_4_20() throws Exception {
		HtmlPage page = login("admin", "adminpw", null);
		try {
			page = getPageByAnchor(page, "Administration");
		} catch (Exception e) {
			fail("Administratören kunde inte komma åt administrationssidan");
		}
	}
}
