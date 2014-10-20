package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class AutentiseringTest extends PussTest{
	
	@Test
	public void FT2_1_1(){
		String groupname = "groupz";
		String username = "Cartman";
		String password = "pass12";
		String role = "t1";
		
		int is_admin = 0;
		int groupId = -1;
		int userId = -1;
		
		try {
			//TODO ändra till clearDatabase();
//			deleteGroup(groupname);
//			deleteUser(username);
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(username + " could not log in", START_URL, page.getUrl().toString());
		
		//Create new webclient and try to log in
		WebClient webClientTwo = new WebClient();	
		HtmlPage page1 = null;
		HtmlPage page2 = null;
		try {
			page1 = webClient.getPage(LOGIN_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    HtmlForm form = page1.getFormByName("input");

	    HtmlSubmitInput button = form.getInputByValue("Submit");
	    HtmlTextInput userField = form.getInputByName("user");
	    HtmlPasswordInput passwordField = form.getInputByName("password");
	    HtmlSelect groupList = form.getSelectByName("groupID");

	    userField.setValueAttribute(username);
	    passwordField.setValueAttribute(password);
	    groupList.setSelectedAttribute(groupList.getOptionByText(groupname), true);

	    try {
			page2 = button.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.println(page2.getUrl().toString());
	    assertEquals(username + " could log in a second time", LOGIN_URL, page2.getUrl().toString());
		
		try {
			deleteGroup(groupname);
			deleteUser(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
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
