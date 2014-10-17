package test;

import java.io.IOException;

public class StartServerTest {
//	private static final String path = "/h/d2/n/atf10aul/tomcat/apache-tomcat-7.0.55/bin/";
	private static final String path = "tomcat/apache-tomcat-7.0.55/bin/";
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		try {
			Process process = Runtime.getRuntime ().exec (path + "startup.sh");
//			Process process = new ProcessBuilder(
//					"C:\\PathToExe\\MyExe.exe","param1","param2").start();
			System.out.println("server startad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
