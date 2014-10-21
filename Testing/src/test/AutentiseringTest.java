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
		String password = "passwo";
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
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String password = "passwo";		
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
		restartServer();
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
			
			addName.setValueAttribute("Stan");
			page = addUser.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			
			while(!rs.isLast()){
				rs.next();
				if(rs.getString(2).equals("Stan")){
					fail("Stan has been added!");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(page.asText().contains("Error: Suggesten name not allowed"));
		System.out.println("FT2_1_3");
	}
	
	@Test
	public void FT2_1_4(){
		
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
			
			addName.setValueAttribute("EricCartman");
			page = addUser.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			
			while(!rs.isLast()){
				rs.next();
				if(rs.getString(2).equals("EricCartman")){
					fail("EricCartman has been added!");
				}
			}
			assertTrue(page.asText().contains("Error: Suggesten name not allowed"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_1_4");
	}
	
	@Test
	public void FT2_1_5(){
		
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
			
			addName.setValueAttribute("Mr Garrison");
			page = addUser.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			
			while(!rs.isLast()){
				rs.next();
				if(rs.getString(2).equals("Mr Garrison")){
					fail("Mr Garrison has been added!");
				}
			}
			assertTrue(page.asText().contains("Error: Suggesten name not allowed"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_1_5");
	}
	
	@Test
	public void FT2_1_6(){
		
		String admin = "admin";
		String adminpw = "adminpw";
		
		String username = "MrHat";
		String password = "passwo";
		
		HtmlPage page = null;
		HtmlAnchor anchor = null;
		HtmlForm form = null;
		HtmlTextInput addName = null;
		HtmlSubmitInput addUser = null;
		
		try {
			addUser(username, password, 0);
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
			
			addName.setValueAttribute(username);
			page = addUser.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			
			int counter = 0;
			while(!rs.isLast()){
				rs.next();
				if(rs.getString(2).equals(username)){
					counter++;
				}
				if(counter == 2){
					fail(username + " has been added twice!");
				}
			}
			assertTrue(page.asText().contains("Error: Suggested user name not possible to add"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_1_6");
	}
	
	@Test
	public void FT2_1_7(){
		
		String admin = "admin";
		String adminpw = "adminpw";
		
		String username = "MrHat";
		String password = "passwo";
		String role = "t1";
		String groupName = "SouthPark";
		
		String newPassword = "tolongp";
		
		int userId = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		HtmlAnchor anchor = null;
		HtmlForm form = null;
		HtmlTextInput oldPass = null;
		HtmlTextInput newPass = null;
		HtmlSubmitInput changePass = null;
		
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(groupName);
			assignGroup(userId, groupId, role);
			page = login(username, password, groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		anchor = page.getAnchorByHref("ChangePassword");
		try {
			page = anchor.click();
			form = page.getFormByName("input");
			oldPass = form.getInputByName("oldpw");
			newPass = form.getInputByName("newpw");
			changePass = form.getInputByValue("Change");
			
			oldPass.setValueAttribute(password);
			newPass.setValueAttribute(newPassword);
			page = changePass.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");

			while(rs.next()){
				if(rs.getString(3).equals(newPassword)){
					fail("Changed to too long password!");
				}
			}
			
			assertTrue(page.asText().contains("Error"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_1_7");
	}
	
	@Test
	public void FT2_1_8(){
		
		String admin = "admin";
		String adminpw = "adminpw";
		
		String username = "MrHat";
		String password = "passwo";
		String role = "t1";
		String groupName = "SouthPark";
		
		String newPassword = "sym ol";
		
		int userId = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		HtmlAnchor anchor = null;
		HtmlForm form = null;
		HtmlTextInput oldPass = null;
		HtmlTextInput newPass = null;
		HtmlSubmitInput changePass = null;
		
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(groupName);
			assignGroup(userId, groupId, role);
			page = login(username, password, groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		anchor = page.getAnchorByHref("ChangePassword");
		try {
			page = anchor.click();
			form = page.getFormByName("input");
			oldPass = form.getInputByName("oldpw");
			newPass = form.getInputByName("newpw");
			changePass = form.getInputByValue("Change");
			
			oldPass.setValueAttribute(password);
			newPass.setValueAttribute(newPassword);
			page = changePass.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");

			while(rs.next()){
				if(rs.getString(3).equals(newPassword)){
					fail("Changed to illegal symbol password!");
				}
			}
			
			assertTrue(page.asText().contains("Error"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_1_8");
	}

	//One page has to be done maually see below
	@Test
	public void FT2_2_1(){
		
		String logOutLink = "LogIn";
		
		String group = "Lorde";
		
		String projectLeader = "Timmy";
		String projectLeaderPass = "TTimmy";
		String projectLeaderRole = "Project Leader";
		
		String projectMember = "Jimmy";
		String projectMemberPass = "Jjjjii";
		String memberRole = "t1";
		
		int projLeaderId = -1;
		int projMemberId = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		HtmlAnchor logOutButton = null;
		try {
			projLeaderId = addUser(projectLeader, projectLeaderPass, 0);
			projMemberId = addUser(projectMember, projectMemberPass, 0);
			groupId = addGroup(group);
			
			assignGroup(projLeaderId, groupId, projectLeaderRole);
			assignGroup(projMemberId, groupId, memberRole);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			//Admin pages - logout links
			page = login(ADMIN_USERNAME,ADMIN_PASSWORD, ADMIN_GROUP);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(ADMINISTRATION_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(GROUP_ADMIN_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
//			Do Manually doesn't work
//			page = webClient.getPage(GROUP_HANDLING_URL);
//			logOutButton = page.getAnchorByHref(logOutLink);
//			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(PROJECT_LEADER_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(REPORT_HANDLING_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(STATISTICS_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = logOutButton.click();
			assertEquals("Logout button does not work!", LOGIN_URL,page.getUrl().toString());

			page = login(projectLeader, projectLeaderPass, group);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(TIMEREPORTING_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(CHANGE_PASSWORD_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(PROJECT_LEADER_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(REPORT_HANDLING_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(STATISTICS_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			
			page = logOutButton.click();
			assertEquals("Logout button not working from statistics", LOGIN_URL, page.getUrl().toString());
			
			page = login(projectMember, projectMemberPass, group);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(TIMEREPORTING_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(TIMEREPORTING_URL_UPDATE);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(TIMEREPORTING_URL_NEW);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(TIMEREPORTING_URL_STATISTICS);
			logOutButton = page.getAnchorByHref(logOutLink);
			assertNotEquals(null, logOutButton);
			
			page = webClient.getPage(CHANGE_PASSWORD_URL);
			logOutButton = page.getAnchorByHref(logOutLink);
			page = logOutButton.click();
			assertEquals("Logout button not working", LOGIN_URL, page.getUrl().toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_2_1");
		
	}

	public void FT2_2_2(){
		
	}
	
	@Test
	
	public void FT2_5_4(){
		
		String groupname = "groupz";
		String username = "Cartman";
		String password = "passwo";

		
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
		} catch (Exception e) {
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
