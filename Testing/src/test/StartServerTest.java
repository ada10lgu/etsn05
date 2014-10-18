package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;





import org.junit.Assert;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class StartServerTest {
	private static final String path = "tomcat/apache-tomcat-7.0.55/bin/";

	public static void main(String[] args) {
		StartServer();
//		runTest();
		test2();
		StopServer();
	}

	public static void StartServer() {
		try {
			Process process = Runtime.getRuntime().exec(path + "startup.sh");
			System.out.println("server startad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runTest() {
		try {
			URL url = new URL("http://localhost:8080/BaseBlockSystem/LogIn");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);


			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			out.write("user=" + "admin");
			out.write("password=" + "adminpw");
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("running test");
	}
	
	public static void test2() {
		final WebClient webClient = new WebClient();
	    HtmlPage page = null;
		try {
//			page = webClient.getPage("http://localhost:8080/BaseBlockSystem/LogIn");
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
	    Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));

	    webClient.closeAllWindows();
	}

	public static void StopServer() {
		try {
			Process process = Runtime.getRuntime().exec(path + "shutdown.sh");
			System.out.println("server stoppad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
