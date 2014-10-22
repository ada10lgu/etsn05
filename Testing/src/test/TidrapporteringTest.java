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
	
	private HtmlPage loginAdmin() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		return login("admin", "adminpw", null);
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
}