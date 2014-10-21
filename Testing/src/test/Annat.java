package test;

import java.io.IOException;
import java.net.MalformedURLException;
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
	public void FT4_1_2() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SQLException{	
	
		deleteUser("kallekal");
	}
}
