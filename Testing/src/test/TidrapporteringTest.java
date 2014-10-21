package test;



import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.AfterClass;
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

public class TidrapporteringTest extends PussTest{
	


	@Test
	public void FT3_1_1() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem lyckas skapa en egen osignerad tidrapport
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



		


		clearDatabase();

		webClient.closeAllWindows();

	}	

	@Ignore
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

		HtmlAnchor tidrapportpage = page2.getAnchorByHref("TimeReporting?function=view");
		final HtmlPage page3 = tidrapportpage.click();

		assertEquals("A could not access Tidrapportering", TIMEREPORTING_URL, page3.getUrl().toString());


		//Starttillstånd färdigt


		//Testfall inleds
		////////////////
		////////////////

		HtmlPage page4 = page3.getAnchorByHref("TimeReporting?function=new").click();
		
		HtmlForm form2 = page4.getFormByName("input");
		
		HtmlSubmitInput button2 = form2.getInputByValue("Submit");
		HtmlTextInput weekField = form2.getInputByName("week");

		weekField.setValueAttribute("5");
		
		HtmlPage page5 = button2.click();
		
		HtmlForm form3 = page5.getForms().get(0);
		
		
		final HtmlSubmitInput button3 = form3.getInputByValue("Save");
		button3.click();

		clearDatabase();

		webClient.closeAllWindows();

	}
	
	@Ignore
	public void FT3_1_3() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		//Projektmedlem lyckas ta bort sin egna tidrapport

		//Projektmedlem lyckas skapa en egen osignerad tidrapport
		//MySQL skapa projektmedlem A: kallekal
		int id =addUser("kallekal", "kallekal", 0);
		assignGroup(id, 91, "t1");
		//Projektmedlem A loggar in
		clearDatabase();
		final WebClient webClient = new WebClient();

		// Get the first page
		final HtmlPage page1 = webClient.getPage("91");

		// Get the form that we are dealing with and within that form, 
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("input");

		final HtmlSubmitInput button = form.getInputByValue("Submit");
		final HtmlTextInput userField = form.getInputByName("user");
		final HtmlPasswordInput passwordField = form.getInputByName("password");
		final HtmlSelect groupList = form.getSelectByName("groupID");

		// Change the value of the text field
		userField.setValueAttribute("kallekal");
		passwordField.setValueAttribute("kallekal");
		groupList.setSelectedAttribute("91", true);


		// Now submit the form by clicking the button and get back the second page.
		final HtmlPage page2 = button.click();

		assertEquals("user could not log in", START_URL, page2.getUrl().toString());

		HtmlAnchor tidrapportpage = page2.getAnchorByHref("Tidrapportering");
		final HtmlPage page3 = tidrapportpage.click();

		assertEquals("A could not access Tidrapportering", TIMEREPORTING_URL, page3.getUrl().toString());

		page3.getAnchorByHref("New").click();

		//Starttillstånd färdigt


		//Testfall inleds
		////////////////
		////////////////

		HtmlAnchor upptidrapportpage = page3.getAnchorByHref("Uppdatera tidrapport");

		final HtmlPage page4 = upptidrapportpage.click();

		//SKRIV IN 30 I RUTA




		//Mysql kod kolla att tidrapport skapad
		//Mysql kolla att tidrapport är osignerad


		///////////////////
		////////////////////
		////////////////////
		//Testfall avslutad




		//Mysql ta bort As tidrapport
		//Mysql ta bort A 

		clearDatabase();

		webClient.closeAllWindows();

	}
}