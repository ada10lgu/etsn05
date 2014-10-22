package test;



import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebAssert;
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

public class TidrapporteringTest extends PussTest{
	
	private HtmlPage addStandardTimeReport(HtmlPage page, String week) throws IOException{		
		HtmlPage newPage = switchPage(page, "TimeReporting?function=new");
		HtmlForm form = newPage.getFormByName("input");
		HtmlSubmitInput button = form.getInputByValue("Submit");
		HtmlTextInput weekField = form.getInputByName("week");
		weekField.setValueAttribute(week);
		page = button.click();
		
		if (newPage.toString().equals(page.toString())){
			return newPage;
		}
		
		form = page.getForms().get(0);
		
		HtmlSubmitInput reportButton = form.getInputByValue("Save");
		HtmlTextInput inputField = form.getInputByName("SVVR_U");
		
		inputField.setValueAttribute("30");

		page = reportButton.click();
		
		
		return page;
	}
	
	private HtmlPage addTimeReport(HtmlPage page, String week, String input) throws IOException{
		HtmlPage newPage = switchPage(page, "TimeReporting?function=new");
		HtmlForm form = newPage.getFormByName("input");
		HtmlSubmitInput button = form.getInputByValue("Submit");
		HtmlTextInput weekField = form.getInputByName("week");
		weekField.setValueAttribute(week);
		page = button.click();
		

		
		form = page.getForms().get(0);
		
		HtmlSubmitInput reportButton = form.getInputByValue("Save");
		HtmlTextInput inputField = form.getInputByName("SVVR_U");
		
		inputField.setValueAttribute(input);

		page = reportButton.click();
		
		
		return page;
	}
	
	private void newUserAddTimeReport(String name, String password, String groupName, Boolean newGroup, String role) throws SQLException, FailingHttpStatusCodeException, MalformedURLException, IOException{			
		int groupId;
		HtmlPage page;
		if (newGroup){
			groupId = addGroup(groupName);
		} else{
			ResultSet rs = sendSQLQuery("select id from groups where name = '" + groupName + "';");
			rs.next();
			groupId = rs.getInt(1);
		}
		int userId =addUser(name,password, 0);
		assignGroup(userId, groupId, role);
		
		page = login(name, password, groupName);
		page = addStandardTimeReport(page, "5");
		switchPage(page, "LogIn");
	}
	
	private int addMemberA(String groupName, Boolean newGroup) throws SQLException{
		int groupId;
		if (newGroup){
			groupId = addGroup(groupName);
		} else{
			ResultSet rs = sendSQLQuery("select id from groups where name = '" + groupName + "';");
			rs.next();
			groupId = rs.getInt(1);
		}
		int userId =addUser("kallekal","kallekal", 0);
		int userGroupId = assignGroup(userId, groupId, "t1");
		
		return userGroupId;
	}
	
	private void addMember(String name, String password, String groupName, Boolean newGroup) throws SQLException{
		int groupId;
		if (newGroup){
			groupId = addGroup(groupName);
		} else{
			ResultSet rs = sendSQLQuery("select id from groups where name = '" + groupName + "';");
			rs.next();
			groupId = rs.getInt(1);
		}
		int userId =addUser(name,password, 0);
		assignGroup(userId, groupId, "t1");
	}
	
	private void addLeaderA(String groupName, Boolean newGroup) throws SQLException{
		int groupId;
		if (newGroup){
			groupId = addGroup(groupName);
		} else{
			ResultSet rs = sendSQLQuery("select id from groups where name = '" + groupName + "';");
			rs.next();
			groupId = rs.getInt(1);
		}
		int userId =addUser("leaderal","leaderal", 0);
		assignGroup(userId, groupId, "Project Leader");
	}
	
	private HtmlPage loginMemberA() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		return login("kallekal", "kallekal", "Projekt1");
	}	
	
	
	private HtmlPage loginLeaderA() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		return login("leaderal", "leaderal", "Projekt1");
	}
	
	private void logOut(HtmlPage page) throws IOException{
		switchPage(page, "LogIn");
	}
	

	@Test
	public void FT3_1_1() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem lyckas skapa en egen osignerad tidrapport
		
		//MySQL skapa projektmedlem A: kallekal
		String name = "kallekal";
		String password = "kallekal";
		String groupName = "Project1";
		//int groupId = 302;
		
		int userId =addUser(name,password, 0);
		int groupId = addGroup(groupName);
		int userGroupId = assignGroup(userId, groupId, "t1");
		

		//Projektmedlem A loggar in		
		HtmlPage page =null;
		
		HtmlPage page2 = login(name, password, groupName);

		HtmlAnchor tidrapportpage = page2.getAnchorByHref("TimeReporting?function=view");
		final HtmlPage page3 = tidrapportpage.click();

		assertEquals("A could not access Tidrapportering", TIMEREPORTING_URL, page3.getUrl().toString());

		
		//Starttillstånd färdigt


		//Testfall inleds
		////////////////
		////////////////

		final HtmlPage page4 = page3.getAnchorByHref("TimeReporting?function=new").click();
		
		final HtmlForm form2 = page4.getFormByName("input");
		
		final HtmlSubmitInput button2 = form2.getInputByValue("Submit");
		final HtmlTextInput weekField = form2.getInputByName("week");

		weekField.setValueAttribute("5");
		
		final HtmlPage page5 = button2.click();
		
		final HtmlForm form3 = page5.getForms().get(0);
		
		
		final HtmlSubmitInput button3 = form3.getInputByValue("Save");
		button3.click();
		

		//Mysql kod kolla att tidrapport skapad
		//Mysql kolla att tidrapport är osignerad

		ResultSet rs= signedReports(userGroupId);

		rs.next();

		assertTrue("Tidrapport ej skapad", rs.getInt(1)== 0);


		///////////////////
		////////////////////
		////////////////////
		//Testfall avslutad

	
	
			
	
//			reports = sendSQLQuery("Select * from reports where user_group_id = " + userGroupId);
//			while(reports.next()){
//			sendSQLQuery("Delete from report_times where report_id = " + reports.getInt("id"));
//			}
//			sendSQLQuery("Delete from reports where user_group_id = " + userGroupId);
//	
//			deleteUser("kallekal");
			
			webClient.closeAllWindows();

	}	

	@Test
	public void FT3_1_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem lyckas uppdatera sin egen osignerade tidrapport

		//MySQL skapa projektmedlem A: kallekal
		clearDatabase();
		
		String name = "kallekal";
		String password = "kallekal";
		String groupName = "Projekt1";
		
		int userId =addUser(name,password, 0);
		int groupId = addGroup(groupName);
		int userGroupId = assignGroup(userId, groupId, "t1");
		//Projektmedlem A loggar in
		
		HtmlPage page =null;
		
		HtmlPage page2 = login(name, password, groupName);

		HtmlPage newTid = switchPage(page2, "TimeReporting?function=new");

		HtmlForm weekForm = newTid.getFormByName("input");

		HtmlSubmitInput button2 = weekForm.getInputByValue("Submit");
		HtmlTextInput weekField = weekForm.getInputByName("week");

		weekField.setValueAttribute("5");

		HtmlPage newRapport = button2.click();

		
		//Redigera tidrapport
		HtmlForm editForm = newRapport.getForms().get(0);
		
		HtmlSubmitInput reportButton = editForm.getInputByValue("Save");
		HtmlTextInput inputField = editForm.getInputByName("SVVR_U");
		
		inputField.setValueAttribute("30");

		reportButton.click();

		ResultSet rs = sendSQLQuery("select id from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		int reportId = rs.getInt(1);
		ResultSet rs2 = sendSQLQuery("select count(*) from report_times where SVVR_U=30 and report_id = " + reportId + ";");
		rs2.next();
		
		assertTrue("Tidrapport ej ändrad", rs2.getInt(1)== 1);


		
		
		webClient.closeAllWindows();

	}
	
	@Test
	public void FT3_1_3() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		
		//Projektmedlem lyckas ta bort sin egna tidrappport
		//MySQL skapa projektmedlem A: kallekal
		
		//MySQL skapa projektmedlem A: kallekal
		clearDatabase();
		
		String name = "kallekal";
		String password = "kallekal";
		String groupName = "Projekt1";
		
		int userId =addUser(name,password, 0);
		int groupId = addGroup(groupName);
		int userGroupId = assignGroup(userId, groupId, "t1");
		//Projektmedlem A loggar in
		
		HtmlPage page =null;
		
		HtmlPage page2 = login(name, password, groupName);

		HtmlPage newTid = switchPage(page2, "TimeReporting?function=new");

		HtmlForm weekForm = newTid.getFormByName("input");

		HtmlSubmitInput button2 = weekForm.getInputByValue("Submit");
		HtmlTextInput weekField = weekForm.getInputByName("week");

		weekField.setValueAttribute("5");

		HtmlPage newRapport = button2.click();

		
		//Redigera tidrapport
		HtmlForm editForm = newRapport.getForms().get(0);
		
		HtmlSubmitInput reportButton = editForm.getInputByValue("Save");
		HtmlTextInput inputField = editForm.getInputByName("SVVR_U");
		
		inputField.setValueAttribute("30");

		HtmlPage page3 = reportButton.click();
		
		HtmlPage updatePage = switchPage(page3, "TimeReporting?function=update");

		HtmlForm updateForm = updatePage.getFormByName("input");
		
		//List<HtmlRadioButtonInput> radioList = updateForm.getRadioButtonsByName("reportID");
		List<DomElement> radioList = updatePage.getElementsByIdAndOrName("reportID");
		
		HtmlRadioButtonInput radio = null;
		DomElement dom = radioList.get(0);
		radio = (HtmlRadioButtonInput) dom;
		radio.click();
		System.out.println(updatePage.asXml());//TODO remove
		HtmlSubmitInput deleteButton = updateForm.getInputByValue("Delete");
		
		ResultSet rs = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		assertTrue("blabla", rs.getInt(1) != 0 );
				
		deleteButton.click();
		System.out.println(updatePage.asXml()); //TODO remove
		
		ResultSet rs2 = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs2.next();
		assertTrue("blabla", rs2.getInt(1) == 0 );
		
//		ResultSet rs = sendSQLQuery("select id from reports where user_group_id = " + userGroupId + ";");
//		rs.next();
		
		webClient.closeAllWindows();
		
	}
	
	@Test
	public void FT3_1_4() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{
		//En projektmedlem kan endast se sina egna tidrapporter
		clearDatabase();
		
		//Skapa Projektmedlem A kallekal i ny projekt "Projekt1"
		
		String name = "kallekal";
		String password = "kallekal";
		String groupName = "Projekt1";
		int userId =addUser(name,password, 0);
		int groupId = addGroup(groupName);
		int userGroupId = assignGroup(userId, groupId, "t1");
		
		newUserAddTimeReport("sallesal", "sallesal", "Projekt2", true, "t1");
		newUserAddTimeReport("talletal", "talletal", "Projekt1", false, "t1");

		//Projektmedlem A loggar in
		
		HtmlPage page =null;
		HtmlForm form = null;
		
		page = login(name, password, groupName);
		
		page = addStandardTimeReport(page, "4");
		page = addStandardTimeReport(page, "5");
		page = addStandardTimeReport(page, "6");
		page = addStandardTimeReport(page, "7");
	
		
		//Kolla vilka rapporter kallekal kan se
		page = switchPage(page, "TimeReporting?function=view");
		form = page.getFormByName("input");		
		List<DomElement> radioList = page.getElementsByIdAndOrName("reportID");
		
		int size = radioList.size();
		
		assertTrue("A kan inte se endast sina egna tidrapporter", size == 4);

		
		webClient.closeAllWindows();
		
		
	}
	

	//MANUELLT
	@Ignore
	public void FT3_1_5() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem A försöker ta bort en av sina signerade tidrapporter
		
		addMemberA("Projekt1", true);
		addLeaderA("Projekt1", false);
		HtmlPage page = loginMemberA();		
		page = addStandardTimeReport(page,"4");
		
		switchPage(page, "TimeReporting?function=update"); //TODO remove
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		
		logOut(page);
		
		page = loginLeaderA();
		page = switchPage(page, "ReportHandling");
		
		HtmlForm form = page.getFormByName("input");		
		List<DomElement> radioList = page.getElementsByIdAndOrName("reportID");
		
		HtmlRadioButtonInput radio;
		DomElement dom = radioList.get(0);
		radio = (HtmlRadioButtonInput) dom;		
		radio.click();
		System.out.print(page.asText()); //TODO remove
		HtmlSubmitInput button = form.getInputByValue("View");
		System.out.println(page.toString());
		page = button.click();
		System.out.print(page.asText()); //TODO remove
		System.out.println(page.toString());
		
		
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		form = page.getFormByName("input");
		
		
		System.out.print(page.asXml()); //TODO remove
		System.out.print(page.asText()); //TODO remove
		
		HtmlSubmitInput button2 = form.getInputByValue("Sign");
		
		page = button2.click();
		logOut(page);
		
		page = loginMemberA();		
		page = switchPage(page, "TimeReporting?function=update");
		
		WebAssert.assertFormNotPresent(page, "input");
		

	}
	
	//MANUELLT
	@Ignore
	public void FT3_1_6() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem försöker redigera en signerad tidrapport
		
		addMemberA("Projekt1", true);
		addLeaderA("Projekt1", false);
		HtmlPage page = loginMemberA();		
		page = addStandardTimeReport(page,"4");
		
		switchPage(page, "TimeReporting?function=update"); //TODO remove
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		
		logOut(page);
		
		page = loginLeaderA();
		page = switchPage(page, "ReportHandling");
		
		HtmlForm form = page.getFormByName("input");		
		List<DomElement> radioList = page.getElementsByIdAndOrName("reportID");
		
		HtmlRadioButtonInput radio;
		DomElement dom = radioList.get(0);
		radio = (HtmlRadioButtonInput) dom;		
		radio.click();
		HtmlSubmitInput button = form.getInputByValue("View");
		page = button.click();
		
		System.out.println(page.toString()); //TODO remove
		
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		form = page.getFormByName("input");
		
		HtmlSubmitInput button2 = form.getInputByValue("Sign");
		
		page = button2.click();
		logOut(page);
		
		page = loginMemberA();		
		page = switchPage(page, "TimeReporting?function=update");
		
		WebAssert.assertFormNotPresent(page, "input");
	}
	
	@Test
	public void FT3_2_1() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektledaren har tillgång till samtliga projektmedlemmars tidrapporter i sin projekt-
		//grupp
		
		addLeaderA("Projekt1", true);
		addMember("asdfgh", "asdfgh", "Projekt1", false);
		addMember("qwerty", "qwerty", "Projekt1", false);
		addMember("zxcvbn", "zxcvbn", "Projekt1", false);
		
		HtmlPage page = login("qwerty", "qwerty", "Projekt1");
		page = addStandardTimeReport(page,"4");
		logOut(page);
		page = login("asdfgh", "asdfgh", "Projekt1");
		page = addStandardTimeReport(page,"4");
		logOut(page);
		page = login("zxcvbn", "zxcvbn", "Projekt1");
		page = addStandardTimeReport(page,"4");
		logOut(page);
		
		page = loginLeaderA();
		
		page = switchPage(page, "ReportHandling");
		List<DomElement> radioList = page.getElementsByIdAndOrName("reportID");
		
		int size = radioList.size();
		
		assertTrue("A kan inte se alla tidrapporter", size==3);
	}
	
	//MANUELLT
	@Ignore
	public void FT3_2_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektledaren lyckas godkänna en ej tidigare signerad tidrapport från en medlem i sin
		//projektgrupp
		
		int userGroupId = addMemberA("Projekt1", true);
		addLeaderA("Projekt1", false);
		HtmlPage page = loginMemberA();		
		page = addStandardTimeReport(page,"4");
		
		switchPage(page, "TimeReporting?function=update"); //TODO remove
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		
		logOut(page);
		
		page = loginLeaderA();
		page = switchPage(page, "ReportHandling");
		
		HtmlForm form = page.getFormByName("input");		
		List<DomElement> radioList = page.getElementsByIdAndOrName("reportID");
		
		HtmlRadioButtonInput radio;
		DomElement dom = radioList.get(0);
		radio = (HtmlRadioButtonInput) dom;		
		radio.click();
		HtmlSubmitInput button = form.getInputByValue("View");
		page = button.click();
		
		System.out.println(page.toString()); //TODO remove
		
		WebAssert.assertFormPresent(page, "input"); //TODO remove
		form = page.getFormByName("input");
		
		HtmlSubmitInput button2 = form.getInputByValue("Sign");
		
		page = button2.click();
		
		ResultSet rs = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		
		
		assertTrue("ble", rs.getInt(1)==0);

	}
	
	
	//////////////////////////
	//FT3.2.3-13 GÖRS MANUELLT
	//////////////////////////
	
	//FT3.3.1 MANUELLT
	
	
	@Test
	public void FT3_3_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Ogiltig tidinformation
		
		int userGroupId = addMemberA("Projekt1", true);
		HtmlPage page = loginMemberA();
		
		page = addTimeReport(page, "4", "123456");
		page = addTimeReport(page, "5", "123:5");
		page = addTimeReport(page, "5", "/");
		ResultSet rs = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		
		assertTrue("SDs", rs.getInt(1)==0);
	}
	
	@Test
	public void FT3_3_3() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Giltig tidinformation
		
		int userGroupId = addMemberA("Projekt1", true);
		HtmlPage page = loginMemberA();
		
		page = addTimeReport(page, "4", "01239");
		page = addTimeReport(page, "5", "0");
		ResultSet rs = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		
		assertTrue("SDs", rs.getInt(1)==2);
	}
	
	@Test
	public void FT3_3_4() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Ogiltig vecka
		
		int userGroupId = addMemberA("Projekt1", true);
		HtmlPage page = loginMemberA();
		
		page = addStandardTimeReport(page, "123");
		page = addStandardTimeReport(page, "1:" );
		page = addStandardTimeReport(page, "/");
		ResultSet rs = sendSQLQuery("select count(*) from reports where user_group_id = " + userGroupId + ";");
		rs.next();
		
		assertTrue("SDs", rs.getInt(1)==0);
	}
	
	@Test
	public void FT3_3_5() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	

	}
	
	@Test
	public void FT3_3_6() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	

	}
	
	@Test
	public void FT3_3_7() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	

	}
	
	@Test
	public void FT3_3_8() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	

	}
	
	@Test
	public void FT3_3_9() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	

	}
	
	
	
}