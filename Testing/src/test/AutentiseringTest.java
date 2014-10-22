package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
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
		assertEquals(username + " was still logged in despite restart", LOGIN_URL, page.getUrl().toString());
		System.out.println("FT2_1_2");
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

	//Ignored because it takes time to run
	@Ignore
	public void FT2_2_2(){
		String username = "MrSlave";
		String password = "slaves";
		String group = "SPElementary";
		
		int userId = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(group);
			assignGroup(userId, groupId, "t1");
			

			page = login(username, password, group);
			TimeUnit.MINUTES.sleep(20);
			page = webClient.getPage(TIMEREPORTING_URL);
			assertEquals("Still has access to TIMEREPORTING_URL even after 20 min of inactivity",LOGIN_URL, page.getUrl().toString());
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(page.asText().contains("Error"));
		System.out.println("FT2_2_2");
	}

	@Test
	public void FT2_3_1(){
		String user1 = "Satan";
		String pass1 = "myhell";
		String role1 = "Project Leader";
		
		String user2 = "Johnsson";
		String pass2 = "siiiir";
		String role2 = "t1";
		
		String group = "LadderToHeaven";
		
		int userId1 = -1;
		int userId2 = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		HtmlAnchor anch = null;
		
		try {
			userId1 = addUser(user1, pass1, 0);
			userId2 = addUser(user2, pass2, 0);
			groupId = addGroup(group);
			assignGroup(userId1, groupId, role1);
			assignGroup(userId2, groupId, role2);
			
			page = login(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GROUP);
			page = webClient.getPage(ADMINISTRATION_URL);

			anch = page.getAnchorByHref("Administration?deletename="+user2+"&deleteid="+userId2);
			page = anch.click();
			
			anch = page.getAnchorByHref("Administration?deletename="+user1+"&deleteid="+userId1);
			page = anch.click();
			
			ResultSet rs = sendSQLQuery("select * from users;");
			while (rs.next()){
				if(rs.getString("username").equals(user1) || rs.getString("username").equals(user2)){
					fail(user1 + " or " + user2 + " was not removed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_3_1");
		
	}
	
	@Test
	public void FT2_3_2(){
		
		int adminId = -1;
		
		HtmlPage page = null;
		
		try {
			adminId = getUserId("admin");
			page = login("admin", "adminpw", null);
			
			page = webClient.getPage(ADMINISTRATION_URL + "?deletename=admin&deleteid=" + adminId);
			
			ResultSet rs = sendSQLQuery("select * from users;");
			Boolean deleted = true;
			
			while(rs.next()){
				if(rs.getString("username").equals("admin")){
					deleted = false;
					break;
				}
			}
			assertTrue("Admin has been deleted!",!deleted);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_3_2");
		
	}

	@Test
	public void FT2_4_1(){
		String user1 = "Jesus";
		String pass1 = "lookat";
		String role = "t1";
		
		String group = "Redskins";
		
		int userId = -1;
		int groupId = -1;
		
		HtmlPage page = null;
		try {
			login(user1, pass1, group);
			fail("Non registered user could log in");
		} catch (Exception e) {
		}
		try {
			userId = addUser(user1, pass1, 0);
			groupId = addGroup(group);
			assignGroup(userId, groupId, role);
			page = login(user1, pass1, group);
			assertEquals("Not redirected to function page", START_URL, page.getUrl().toString());
			page = webClient.getPage(TIMEREPORTING_URL);
			assertEquals("No access to logged in functionallity", TIMEREPORTING_URL, page.getUrl().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FT2_4_1");
	}
	
	//Check again
	@Test
	public void FT2_5_1(){
		
		HtmlPage page = null;
		try {
			page = login("admin", "adminp", null);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			page = webClient.getPage(CHANGE_PASSWORD_URL);
			assertEquals("Could reach logged in pages without logging in", LOGIN_URL, page.getUrl().toString());
		} catch (Exception e) {
			assertEquals("Could reach logged in pages without logging in", LOGIN_URL, page.getUrl().toString());
			e.printStackTrace();
		}
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		System.out.println("FT2_5_1");	
	}
	
	@Test
	public void FT2_5_2(){
		String group1 = "HellOnearth";
		String group2 = "LadderToHeaven";
		String group3 = "FightersZaron";
		
		int groupId1 = -1;
		int groupId2 = -1;
		int groupId3 = -1;
		
		HtmlPage page = null;
		HtmlForm form = null;
		HtmlSelect select = null;
		
		try {
			groupId1 = addGroup(group1);
			groupId2 = addGroup(group2);
			groupId3 = addGroup(group3);
			webClient = new WebClient();
			
			page = webClient.getPage(LOGIN_URL);
			form = page.getFormByName("input");
			select = form.getSelectByName("groupID");
			
			List<HtmlOption> list = select.getOptions();
			int counter = 0;
			
			for(HtmlOption option: list){
				if(option.asText().equals(group1) || option.asText().equals(group2) || option.asText().equals(group3)){
					counter++;
				}
			}
			assertEquals("Not all opotions represented", 3, counter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_5_2");
	}
	
	@Test
	public void FT2_5_3(){
		String username = "MrMarsh";
		String password = "easter";
		
		String group = "HairClubForMen";
		
		int groupId = -1;
		int userId = -1;
		
		HtmlPage page = null;
		
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(group);
			assignGroup(userId, groupId, "t1");
			
			page = login(username, password, null);
			assertEquals("Managed to log in without selecting a group",  LOGIN_URL,page.getUrl().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_5_3");
		
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
		assertEquals(username + " could not log in", START_URL, page.getUrl().toString());
		System.out.println("FT2_5_4");
	}
	
	@Test
	public void FT2_5_5(){
		String username = "MrMarsh";
		String password = "easter";
		
		String group1 = "HairClubForMen";
		String group2 = "HairClubForWomen";
		
		int groupId = -1;
		int userId = -1;
		
		HtmlPage page = null;
		
		try {
			userId = addUser(username, password, 0);
			groupId = addGroup(group1);
			addGroup(group2);
			assignGroup(userId, groupId, "t1");
			page = login(username, password, group2);
			assertEquals("Managed to log in without selecting a group",  LOGIN_URL, page.getUrl().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_5_5");
		
	}

	@Test
	public void FT2_5_6(){
		String group1 = "HairClubForMen";
		String group2 = "HairClubForWomen";
		String group3 = "HairClubForGoats";
		String group4 = "HairClubForCows";
		
		ArrayList<String> groups = new ArrayList<String>();
		
		groups.add(group1);
		groups.add(group2);
		groups.add(group3);
		groups.add(group4);
		
		
		HtmlPage page = null;
		HtmlAnchor anchor = null;
		
		try {
			for(String group: groups){
				addGroup(group);
			}
			
			for(String group: groups){
				page = login(ADMIN_USERNAME, ADMIN_PASSWORD, group2);
				assertEquals("Managed to log in without selecting a group",  START_URL, page.getUrl().toString());
				anchor = page.getAnchorByHref("LogIn");
				page = anchor.click();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FT2_5_5");
		
	}	
}
