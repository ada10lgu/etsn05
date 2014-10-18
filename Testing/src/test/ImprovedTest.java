package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

import org.junit.*;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class ImprovedTest extends PussTest{
	
	/**
	@BeforeClass
	public static void StartServer() {
		try {
			Runtime.getRuntime().exec(TOMCAT_PATH + STARTUP_SHELL);
			System.out.println("server startad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void StopServer() {
		try {
			Runtime.getRuntime().exec(TOMCAT_PATH + SHUTDOWN_SHELL);
			System.out.println("server stoppad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	@Ignore
	public void example() {
		final WebClient webClient = new WebClient();
		HtmlPage page = null;
		try {
			// page =
			// webClient.getPage("http://localhost:8080/BaseBlockSystem/LogIn");
			page = webClient.getPage("http://htmlunit.sourceforge.net");
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

		final String pageAsXml = page.asXml();
		Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));

		final String pageAsText = page.asText();
		Assert.assertTrue(pageAsText
				.contains("Support for the HTTP and HTTPS protocols"));

		webClient.closeAllWindows();
	}
	
//	@Ignore
	@Test
	public void login() throws Exception {
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
	    userField.setValueAttribute("jonatan");
	    passwordField.setValueAttribute("jonatan");
	    groupList.setSelectedAttribute(LOGIN_T3, true);

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();

	    assertEquals("jonatan could not log in", START_URL, page2.getUrl().toString());

	    HtmlAnchor logout = page2.getAnchorByHref("LogIn");
	    logout.click();
	    webClient.closeAllWindows();
	}
	
	@Test
	public void dbTest() {
//		StartServer();
		try {
//			addUser("olle", "olle", 0);
			deleteUser("olle");
			assertTrue(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
