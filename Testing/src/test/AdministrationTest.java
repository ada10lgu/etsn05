package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
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
	public void FT4_1_1() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException, SQLException {
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

		assertEquals("admin could not log in", START_URL, page2.getUrl()
				.toString());

		HtmlAnchor administrationpage = page2.getAnchorByHref("Administration");
		final HtmlPage page3 = administrationpage.click();

		assertEquals("Administration are not acces to administrations page",
				ADMINISTRATION_URL, page3.getUrl().toString());

		clearDatabase();

		webClient.closeAllWindows();

	}

	@Ignore
	public void FT4_1_2() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException, SQLException {
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
	    

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();
	    
	    assertEquals("user could not log in", START_URL, page2.getUrl().toString());
	    
	    HtmlAnchor administrationpage = page2.getAnchorByHref("Administration");
	    final HtmlPage page3 = administrationpage.click();

	    assertEquals("Vanligt avnändare kan åtkom åt administration sida", START_URL, page3.getUrl().toString());

	    clearDatabase();
	    
	    webClient.closeAllWindows();
	    
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

		assertEquals("Admin blev inte inloggad", START_URL, page.getUrl()
				.toString());

		// navigera till projektgruppsadministrationssidan.
		HtmlAnchor anchor = page.getAnchorByHref("ProjectGroupAdmin");
		try {
			page = anchor.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin hamnade inte på projektadministrationssidan",
				GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för kort namn
		final HtmlForm form1 = page.getFormByName("input");
		final HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		final HtmlTextInput projectNameField1 = form1
				.getInputByName("projectname");
		projectNameField1.setValueAttribute(tooShortGroupName);
		try {
			page = button1.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals(
				"Admin kunde lägga till en projektgrupp med ett för kort namn",
				GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med för långt namn
		final HtmlForm form2 = page.getFormByName("input");
		final HtmlSubmitInput button2 = form2.getInputByValue("Add project");
		final HtmlTextInput projectNameField2 = form2
				.getInputByName("projectname");
		projectNameField2.setValueAttribute(tooLongGroupName);
		try {
			page = button2.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals(
				"Admin kunde lägga till en projektgrupp med ett för långt namn",
				GROUP_ADMIN_URL, page.getUrl().toString());

		// lägg till projektgrupp med namn som innehåller olämpligt tecken
		final HtmlForm form3 = page.getFormByName("input");
		final HtmlSubmitInput button3 = form3.getInputByValue("Add project");
		final HtmlTextInput projectNameField3 = form3
				.getInputByName("projectname");
		projectNameField3.setValueAttribute(tooLongGroupName);
		try {
			page = button3.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggested name not allowed"));
		assertEquals(
				"Admin kunde lägga till en projektgrupp med ett för långt namn",
				GROUP_ADMIN_URL, page.getUrl().toString());
		
		//TODO kontrollera att databsen är tom
	}

	@Test
	public void FT4_4_2() {
		// förberedelser
		String username = "leader";
		String password = "leader";
		String group = null;
		String projectGroupName1 = "gouda";
		String projectGroup2 = "mozzarella";
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
		HtmlAnchor anchor = page.getAnchorByHref("ProjectGroupAdmin");
		try {
			page = anchor.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin hamnade inte på projektadministrationssidan",
				GROUP_ADMIN_URL, page.getUrl().toString());
		
		//lägg till projektgrupp
		final HtmlForm form1 = page.getFormByName("input");
		final HtmlSubmitInput button1 = form1.getInputByValue("Add project");
		final HtmlTextInput projectNameField1 = form1
				.getInputByName("projectname");
		projectNameField1.setValueAttribute(projectGroupName1);
		try {
			page = button1.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Admin kunde inte skapa projektgrupp med namnet: " + projectGroupName1,
				GROUP_HANDLING_URL, page.getUrl().toString());
		final HtmlForm form2 = page.getFormByName("input");
		List<HtmlRadioButtonInput> radioButtons = form2.getRadioButtonsByName("selectedradiouser");
		for(HtmlRadioButtonInput radioButton : radioButtons) {
			if(userId == Integer.parseInt(radioButton.getValueAttribute())) {
				try {
					radioButton.click();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
	}

}
