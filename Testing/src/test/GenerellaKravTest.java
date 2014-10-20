package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GenerellaKravTest extends PussTest{
	
	@Test
	public void FT1_1_5(){
		String g = "SouthPark";
		String pOne = "Cartman";
		String passpOne = "pass12";
		String pTwo = "Kenny";
		String passpTwo = "pass34";
		String mOne = "Garrison";
		String passmOne = "pass56";
		
		int idpOne = -1;
		int idpTwo = -1;
		int idmOne = -1;
		int idg = -1;
		
		String pl = "Project Leader";
		String t1 = "t1";
		String t4 = "t4";
		
		try {
			idg = addGroup(g);
			idpOne = addUser(pOne, passpOne, 0);
			idpTwo = addUser(pTwo, passpTwo, 0);
			idmOne = addUser(mOne, passmOne, 0);
			assignGroup(idpOne, idg, pl);
			assignGroup(idpTwo, idg, pl);
			assignGroup(idmOne, idg, t1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HtmlPage page = null;
		try {
			page = login("admin", "adminpw", g);
			HtmlAnchor anchor = page.getAnchorByHref("ProjectLeader");
			anchor.click();
			anchor = page.getAnchorByHref("ProjectLeader?groupID=" + idg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			deleteGroup(g);
			deleteUser(mOne);
			deleteUser(pOne);
			deleteUser(pTwo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
