package test;



import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

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
int id =addUser("kallekal", "kallekal", 0);
assignGroup(id, 91, "t1");
//Projektmedlem A loggar in
clearSessions();
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
   userField.setValueAttribute("kallekal");
   passwordField.setValueAttribute("kallekal");
   groupList.setSelectedAttribute(LOGIN_T3, true);
   

   // Now submit the form by clicking the button and get back the second page.
   final HtmlPage page2 = button.click();
   
   assertEquals("user could not log in", START_URL, page2.getUrl().toString());
   
   HtmlAnchor tidrapportpage = page2.getAnchorByHref("Tidrapportering");
   final HtmlPage page3 = tidrapportpage.click();

   assertEquals("A could not access Tidrapportering", TIMEREPORTING_URL, page3.getUrl().toString());
   
   
   //Starttillstånd färdigt
   
   
   //Testfall inleds
   ////////////////
   ////////////////
   
   HtmlAnchor nytidrapportpage = page3.getAnchorByHref("Ny tidrapport");
   
   final HtmlPage page4 = nytidrapportpage.click();
   
   //Mysql kod kolla att tidrapport skapad
   //Mysql kolla att tidrapport är osignerad
   
   
   ///////////////////
   ////////////////////
   ////////////////////
   //Testfall avslutad
  
   
   


   clearSessions();
   
   webClient.closeAllWindows();
   
}	

@Test
public void FT3_1_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
//Projektmedlem lyckas uppdatera sin egen osignerade tidrapport
//Projektmedlem A loggar in
clearSessions();
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
   userField.setValueAttribute("kallekal");
   passwordField.setValueAttribute("kallekal");
   groupList.setSelectedAttribute(LOGIN_T3, true);
   

   // Now submit the form by clicking the button and get back the second page.
   final HtmlPage page2 = button.click();
   
   assertEquals("user could not log in", START_URL, page2.getUrl().toString());
   
   HtmlAnchor tidrapportpage = page2.getAnchorByHref("Tidrapportering");
   final HtmlPage page3 = tidrapportpage.click();

   assertEquals("A could not access Tidrapportering", TIMEREPORTING_URL, page3.getUrl().toString());
   
   
   
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

   clearSessions();
   
   webClient.closeAllWindows();
   
}
}