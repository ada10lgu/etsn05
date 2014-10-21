package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class AutentiseringTest extends PussTest{
	
	//not working do manuall
	@Ignore
	public void FT2_1_1(){
		String groupname = "groupz";
		String username = "Cartman";
		String password = "pass12";
		String role = "t1";
		
		int is_admin = 0;
		int groupId = -1;
		int userId = -1;
		
		try {			
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
		try {
			deleteGroup(groupname);
			deleteUser(username);
			System.out.println("FT2_1_1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(username + " could not log in", START_URL, page.getUrl().toString());
	    assertEquals(username + " could log in a second time", LOGIN_URL, page2.getUrl().toString());
	}

	@Test
	public void FT2_1_2(){
		String groupname = "groupz";
		String username = "Cartman";
		String password = "pass12";		
		String role = "t1";
		
		int is_admin = 0;
		int groupId = -1;
		int userId = -1;
		try {
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
		shutDownServer();
		startServer();
		
		WebClient w2 = new WebClient();
		
		try {
			page = w2.getPage(START_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		try {
			deleteGroup(groupname);
			deleteUser(username);
			System.out.println("FT2_1_2");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(username + " could was still logged in despite restart", LOGIN_URL, page.getUrl().toString());
	}

	@Test
	public void FT2_1_3(){
		String admin = "admin";
		String adminpw = "adminpw";
		
		HtmlPage page = null;
		HtmlAnchor anchor = null;
		HtmlForm form = null;
		HtmlTextInput addName = null;
		HtmlSubmitInput addUser = null;
		
		try {
			page = login(admin, adminpw, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		anchor = page.getAnchorByHref("Administration");
		try {
			page = anchor.click();
			form = page.getFormByName("input");
			addName = form.getInputByName("addname");
			addUser = form.getInputByValue("Add user");
			
			addName.setValueAttribute("Tord");
			page = addUser.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			
			while(!rs.isLast()){
				rs.next();
				if(rs.getString(2).equals("Tord")){
					fail("Tord has been added!");
				}
			}
			
		} catch (Exception e) {
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
		try {
			deleteGroup(groupname);
			deleteUser(username);
			System.out.println("FT2_5_4");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(username + " could not log in", START_URL, page.getUrl().toString());
	}
	

}
