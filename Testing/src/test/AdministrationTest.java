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

	private HtmlPage getPageByAnchor(HtmlPage page, String anchor) throws IOException {
		HtmlAnchor htmlAnchor = page.getAnchorByHref("ProjectGroupAdmin");
		return page = htmlAnchor.click();
	}

	private HtmlPage nameNewGroup(HtmlPage page, String groupName) throws IOException {
		final HtmlForm form1 = page.getFormByName("input");
		final HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		final HtmlTextInput projectNameField1 = form1.getInputByName("projectname");
		projectNameField1.setValueAttribute(groupName);
		return page = button1.click();
	}

	private HtmlPage addUserToGroup(HtmlPage page, int userId) throws IOException {
		HtmlForm form = page.getFormByName("input");
		List<HtmlRadioButtonInput> radioButtons = form.getRadioButtonsByName("selectedradiouser");
		for (HtmlRadioButtonInput radioButton : radioButtons) {
			if (userId == Integer.parseInt(radioButton.getValueAttribute())) {
				try {
					radioButton.click();
					System.out.println("Radiobutton klickad");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		HtmlSubmitInput button = form.getInputByValue("Add user");
		return page = button.click();
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
		// HtmlAnchor anchor = page.getAnchorByHref("ProjectGroupAdmin");
		// try {
		// page = anchor.click();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för kort namn
		try {
			page = nameNewGroup(page, tooShortGroupName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// final HtmlForm form1 = page.getFormByName("input");
		// final HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		// final HtmlTextInput projectNameField1 =
		// form1.getInputByName("projectname");
		// projectNameField1.setValueAttribute(tooShortGroupName);
		// try {
		// page = button1.click();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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
			assertEquals("Grupper har lagts till", 0, rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// TODO kontrollera att databsen är tom
	}

	@Test
	public void FT4_4_2() {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String group = null;
		String projectGroupName1 = "gouda";
		String projectGroupName2 = "mozzarella";
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

		// navigera till projektgruppsadministrationssidan.
		try {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp

		try {
			page = nameNewGroup(page, projectGroupName1);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1, GROUP_HANDLING_URL, page.getUrl().toString());

		try {
			page = addUserToGroup(page, userId);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// HtmlForm form1 = page.getFormByName("input");
		// HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		// HtmlTextInput projectNameField1 =
		// form1.getInputByName("projectname");
		// projectNameField1.setValueAttribute(projectGroupName1);
		// try {
		// page = button1.click();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// assertEquals("Admin kunde inte skapa projektgrupp med namnet: " +
		// projectGroupName1, GROUP_HANDLING_URL, page.getUrl().toString());
		// HtmlForm form2 = page.getFormByName("input");
		// List<HtmlRadioButtonInput> radioButtons =
		// form2.getRadioButtonsByName("selectedradiouser");
		// for (HtmlRadioButtonInput radioButton : radioButtons) {
		// if (userId == Integer.parseInt(radioButton.getValueAttribute())) {
		// try {
		// radioButton.click();
		// System.out.println("Radiobutton klickad");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// break;
		// }
		// }
		// HtmlSubmitInput button = form2.getInputByValue("Add user");
		// try {
		// page = button.click();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// navigera till projektgruppsadministrationssidan.
		try {
			page = getPageByAnchor(page, "ProjectGroupAdmin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin hamnade inte på projektadministrationssidan", GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp
		try {
			page = nameNewGroup(page, projectGroupName1);
			addUserToGroup(page, userId);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1, GROUP_HANDLING_URL, page.getUrl().toString());

		// make sure the right content is in the database
		try {
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

		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		//lägg till fem grupper
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
			assertEquals("Fel antal grupper finns i databasen", 5, rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void FT4_4_5() {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String[] groupNames = { "grupp1", "grupp2", "grupp3", "grupp4", "grupp5" };
		String groupName6 = "grupp6";
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
		//lägg till fem grupper
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

		// försök lägga till projektgrupp #6
		try {
			page = nameNewGroup(page, groupName6);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin kunde skapa en sjätte projektgrupp med namnet: " + groupName6, GROUP_ADMIN_URL, page.getUrl().toString());

		// kontrollera att rätt grupper finns i databasen
		try {
			ResultSet rs = sendSQLQuery("select count(*) from groups");
			assertEquals("Fel antal grupper finns i databasen", 5, rs.getInt(1));
			rs = sendSQLQuery("select count(*) from groups where name = '" + groupName6 + "';");
			assertEquals(groupName6 + "finns i databasen", 0, rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void FT4_4_6() {
		
	}
}
