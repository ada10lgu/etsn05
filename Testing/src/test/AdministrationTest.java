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

public class AdministrationTest extends PussTest{

	@Test
	public void FT4_1_1() throws FailingHttpStatusCodeException, MalformedURLException, IOException{	
		
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
	    

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();
	    
	    assertEquals("admin could not log in", START_URL, page2.getUrl().toString());
	    
	    HtmlAnchor administrationpage = page2.getAnchorByHref("Administration");
	    final HtmlPage page3 = administrationpage.click();

	    assertEquals("Administration are not acces to administrations page", ADMINISTRATION_URL, page3.getUrl().toString());

	    HtmlAnchor logout = page3.getAnchorByHref("LogIn");
	    logout.click();
	    webClient.closeAllWindows();
	    
	}
		
}

