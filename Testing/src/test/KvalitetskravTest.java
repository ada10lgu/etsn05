package test;

import org.junit.Test;

import static org.junit.Assert.*;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class KvalitetskravTest extends PussTest {
	@Test
	public void ST5_1_1() throws Exception {
		//förberedelser
		String usernameTemplate = "user";
		String password = "pass12";
		String role = "t1";
		String groupName = "group1";
		int numberOfUsers = 51;
		addGroup(groupName);
		WebClient[] webClients = new WebClient[numberOfUsers];
		HtmlPage[] pages = new HtmlPage[numberOfUsers];

		for (int i = 0; i < numberOfUsers; i++) {
			webClients[i] = new WebClient();
			String username = usernameTemplate + i;
			addUser(username, password, 0);
			addUserToGroup(username, groupName, role);
		}
		
		//logga in alla
		for (int i = 0; i < numberOfUsers; i++) {
			String username = usernameTemplate + i;
			HtmlPage page = webClients[i].getPage(LOGIN_URL);

			HtmlForm form = page.getFormByName("input");
			HtmlSubmitInput button = form.getInputByValue("Submit");
			HtmlTextInput userField = form.getInputByName("user");
			HtmlPasswordInput passwordField = form.getInputByName("password");

			userField.setValueAttribute(username);
			passwordField.setValueAttribute(password);
			final HtmlSelect groupList = form.getSelectByName("groupID");
			groupList.setSelectedAttribute(groupList.getOptionByText(groupName), true);
			pages[i] = button.click();
		}
		
		//kontrollera vem som är inloggad
		for (int i = 0; i < numberOfUsers; i++) {
			HtmlPage page = pages[i];
			page.refresh();
			if (i < numberOfUsers - 1) {
				assertEquals("användare nummer " + (i + 1) + " blev inte inloggad", START_URL, page.getUrl().toString());
			} else {
				assertEquals("användare nummer " + (i + 1) + " blev inloggad", LOGIN_URL, page.getUrl().toString());
			}
		}
		
		//stäng alla fönster
		for (int i = 0; i < numberOfUsers; i++) {
			webClients[i].closeAllWindows();
		}
	}

	@Test
	public void ST5_1_2() throws Exception {
		int tooLongTimeCounter = 0;
		long tooLongTime = 1000;
		int limit = 2;
		for (int i = 0; i < 20; i++) {
			long startTime = System.currentTimeMillis();
			login("admin", "adminpw", null);
			long endTime = System.currentTimeMillis();
			if (endTime - startTime > tooLongTime) {
				tooLongTimeCounter++;
			}
		}
		assertTrue("för många anrop tog för lång tid", tooLongTimeCounter <= limit);
	}
	
}
