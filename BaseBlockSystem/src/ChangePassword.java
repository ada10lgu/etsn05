import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/ChangePassword")
public class ChangePassword extends servletBase {

	private static final long serialVersionUID = 1L;

	/**
	 * generates a form for changing password
	 * @return HTML code for the form
	 */
	private String changePasswordForm() {
		String html;
		html = "<p> <form name=" + formElement("input");
		html += " method=" + formElement("get");
		html += "<p> Old password: <input type=" + formElement("text") + " name=" + formElement("oldpw") + '>';     	
		html += "<p> New password: <input type=" + formElement("text") + " name=" + formElement("newpw") + '>';  
		html += "<input type=" + formElement("submit") + "value=" + formElement("Change") + '>';
		html += "</form>";
		return html;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		access.updateLog(null, null);
		
		PrintWriter out = response.getWriter();
		out.println(getPageIntro());
		out.println(printMainMenu());
		String myName = "";
		HttpSession session = request.getSession(true);
		Object nameObj = session.getAttribute("name");
		Object idObj = session.getAttribute("userID");
		int id =(int)idObj;
		System.out.println(id);
		String oldPw = request.getParameter("oldpw");
		String newPw = request.getParameter("newpw");
		
		if (nameObj != null) {
			myName = (String)nameObj;  // if the name exists typecast the name to a string
		}

		// check that the user is logged in
		if (!loggedIn(request)) {
			response.sendRedirect("LogIn");
		} else {
			if (myName.equals("admin")) { 
				out.println("<p>Error: Admin is not allowed to change password</p>");
			} else {
				if (oldPw!=null&&newPw!=null) {
					Statement stmt;
					try {
						stmt = conn.createStatement();
						String statement = "select * from users where ID=" + id; 
						ResultSet rs= stmt.executeQuery(statement);
						String pw = null;
						while (rs.next( )) {
							pw = rs.getString("password");
						}
						if (pw.equals(oldPw)) {
							stmt = conn.createStatement();
							statement = "Update users SET password='"+newPw+"' where ID=" + id; 
							stmt.executeUpdate(statement);
							out.println("<p>Successfully changed password</p>");
						} else {
							out.println("<p>Error: entered old password does not match password in database</p>");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				out.println(changePasswordForm());
			}
		}
	}

}



