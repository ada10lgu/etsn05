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

	private HtmlPage addUserToGroup(HtmlPage page, int userId) throws IOException {

		List<DomElement> list1 =  page.getElementsByIdAndOrName("selectedradiouser");
		System.out.println("number of DomElements: " + list1.size());
		HtmlRadioButtonInput radioButton = null;
		for(DomElement element : list1) {
			radioButton = (HtmlRadioButtonInput) element;
			System.out.println("checked attribute: " + radioButton.getCheckedAttribute());
//			System.out.println(radioButton.getValueAttribute());
			if(String.valueOf(userId).equals(radioButton.getValueAttribute())) {
				System.out.println("match!");
				radioButton.click();
				break;
			}
			
		}
		HtmlForm form = page.getFormByName("input");
//		List<HtmlRadioButtonInput> radioButtons = form.getRadioButtonsByName("selectedradiouser");
//		System.out.println("radio button listas storlek: " + radioButtons.size());
//		for (HtmlRadioButtonInput radioButton : radioButtons) {
//			System.out.println("radio button räknad");
//			if (String.valueOf(userId).equals(radioButton.getValueAttribute())) {
//				radioButton.click();
//				radioButton.setChecked(true); //??
//				System.out.println("Radiobutton klickad");
//				break;
//			}
//		}
		HtmlSubmitInput button = form.getInputByValue("Add user");
		radioButton.setChecked(true);
		return button.click();
	}

	@Test
	public void FT4_4_1() {
		// förberedelser
		String username = "admin";
		String password = "adminpw";
		String group = null; // admin behöver inte ange grupp
		String tooShortGroupName = "brie";
		String tooLongGroupName = "creamcheese";
		String badCharGroupName = "cheddar?";

		HtmlPage page = null;
		try {
			page = login(username, password, group);
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
			fail("Admin kunde inte logga in");
		}

		assertEquals("Admin blev inte inloggad", START_URL, page.getUrl().toString());

		// navigera till projektgruppsadministrationssidan.

		try {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för kort namn
		try {
			page = nameNewGroup(page, tooShortGroupName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för kort namn", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för långt namn
		try {
			page = nameNewGroup(page, tooLongGroupName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för långt namn", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med namn som innehåller olämpligt tecken
		try {
			page = nameNewGroup(page, badCharGroupName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals("Admin kunde lägga till en projektgrupp med ett för långt namn", GROUP_ADMIN_URL, page.getUrl().toString());

		try {
			ResultSet rs = sendSQLQuery("select count(*) from groups;");
			rs.next();
			assertEquals("Grupper har lagts till", 0, rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO kontrollera att databsen är tom
	}

	@Test
	public void FT4_4_2() throws Exception {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String projectGroupName1 = "gouda";
		String projectGroupName2 = "mozzarella";
		int userId = -1;
		userId = addUser(username, password, 0);
		HtmlPage page = login("admin", "adminpw", null);

		// navigera till projektgruppsadministrationssidan.
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp

		page = nameNewGroup(page, projectGroupName1);
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1, GROUP_HANDLING_URL, page.getUrl().toString());

		page = addUserToGroup(page, userId);

		// navigera till projektgruppsadministrationssidan.
		page = getPageByAnchor(page, "ProjectGroupAdmin");
		// TODO är på fel sida
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp
		page = nameNewGroup(page, projectGroupName2);
		assertEquals("Admin hamnde inte på gruppediteringssidan", GROUP_HANDLING_URL, page.getUrl().toString());
		addUserToGroup(page, userId);
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1, GROUP_HANDLING_URL, page.getUrl().toString());

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
	public void FT4_4_3() {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String group = null;
		String projectGroupName = "getost";
		int userId = -1;
		int groupId = -1;
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(projectGroupName);
		} catch (SQLException e) {
			try {
				userId = getUserId(username);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		HtmlPage page = null;
		try {
			page = login("admin", "adminpw", null);
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}

		// navigera till projektgruppsadministrationssidan.
		try {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp

		try {
			page = nameNewGroup(page, projectGroupName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		assertEquals("Admin kunde skapa en grupp med ett upptaget namn", GROUP_ADMIN_URL, page.getUrl().toString());
		assertTrue("Inget felmeddelande visas", page.asText().contains("Error: Suggested project group name not possible to add"));
	}

	@Test
	public void FT4_4_4() {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String[] groupNames = { "grupp1", "grupp2", "grupp3", "grupp4", "grupp5" };
		int userId = -1;
		try {
			userId = addUser(username, password, 0);
		} catch (SQLException e) {
			try {
				userId = getUserId(username);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		HtmlPage page = null;
		try {
			page = login("admin", "adminpw", null);
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		// lägg till fem grupper
		for (String groupName : groupNames) {
			try {
				page = getPageByAnchor(page, "ProjectGroupAdmin");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				page = nameNewGroup(page, groupName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + groupName, GROUP_HANDLING_URL, page.getUrl().toString());
			try {
				page = addUserToGroup(page, userId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// kontrollera att rätt grupper finns i databasen
		try {
			ResultSet rs = sendSQLQuery("select count(*) from groups");
			rs.next();
			assertEquals("Fel antal grupper finns i databasen", 5, rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
			assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + groupName, GROUP_HANDLING_URL, page.getUrl().toString());
			page = addUserToGroup(page, userId);
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
			page = addUserToGroup(page, userId2);

			ResultSet rs = sendSQLQuery("select count(*) from user_group where user_id = " + userId2 + " and group_id = " + groupIds[i] + ";");
			rs.next();
			System.out.println("om noll så har ingen användare lagts till i gruppen: " + rs.getInt(1));
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
		assertEquals("admin kunde inte navigera till gruppediteringssidan", GROUP_HANDLING_URL, page.getUrl().toString());
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
		addGroup(groupName);
		addUserToGroup(username, groupName, role);

		HtmlPage page = login("admin", "adminpw", null);
		
		ResultSet rs = sendSQLQuery("select count(*) from users where username = '" + username + "'");
		rs.next();
		assertEquals("användaren finns inte från början", 1, rs.getInt(1));
		
		// navigera till administration
		page = getPageByAnchor(page, "Administration");
		// delete user from project
		page = getPageByAnchor(page, "Administration?deletename=" + username + "&deleteid=" + userId);

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
		page = addUserToGroup(page, userId);

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

}
