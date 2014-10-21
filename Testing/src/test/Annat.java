package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class Annat extends PussTest {
	
//	public static void main(String args[]) throws SQLException{
//		new Annat().run();
//	}
//	
//	public Annat(){
//		
//	}

	
	
	@Test
	public void addSpecificUser() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
		int a = addUser("johant", "johant",0);
		int b = addGroup("testare");
		assignGroup(a, b, "Project Leader");
		System.out.println("före");
		System.in.read();
		System.out.println("efter");
	}
}
