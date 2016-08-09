import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class management
 */
@WebServlet(description = "Manage session cookies back and forth between client and server", urlPatterns = { "/management" })
public class management extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// Generate a session state for the client
	Session client = new Session();
	// Name of my session cookie
	private static String nameOfcookie = "CS5300PROJ1SESSION";
	// Initialize message in session state table
	private String message = "";
	// Thread for removing timed-out sessions
	private Timer removeTime = new Timer();
	private daemon removeThread = new daemon();
	
	public management(){
		removeTime.schedule(removeThread, 5000, 50000);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		// Initialize sessionID and cookie
		String sessionID = "";
		Cookie cookie = null;	
		// Login state of client
		boolean logOut = false;
		// Get action the client is executing
		String act = (String)request.getParameter("command");
		// Get cookie objects the client sent with this request
		Cookie[] cookies = request.getCookies();
		
		// Retrieve existed cookies for the client request
		if (cookies != null) {
			for (Cookie C : cookies) {
				String cookieName = C.getName();
				String cookieValue = C.getValue();
				if (cookieName.equals(nameOfcookie)) {
					// Set the existed cookie to our cookie object
					cookie = C;					
					// Get sessionID from the existed session
					client.setSessionID(cookieValue.split("_")[0]);
					sessionID = client.getSessionID();
					client.setExpiration();
					message = client.getMessage();
				}
			}
		}
		// Create cookie for the first client request
		else {
			// Generate a unique session ID
			client.setSessionID(client.getExpirationTime().toString());
			client.setVersionNumber(0);
			// Initialize a new cookie 
			cookie = new Cookie(nameOfcookie, "");
			sessionID = client.getSessionID();
			message = "Hello User";			
		}
		
		out.println("NetID: wf85");
		out.println("Session: " + client.getSessionID());
		out.println("Version: " + client.getVersionNumber());
		out.println("Date: " + new Date());
		
		// Response differently based on action the client executes
		if (act != null) {
			if (act.equals("Logout")) {
				// The cookie expires Immediately
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				logOut = true;
				message = "Session Terminates";
			} 
			else if (act.equals("Replace")) {
				// Replace the current message in the session with the new one in the client request
				message = request.getParameter("message");
			} 			
		}	
		
		out.println("<br /><br />");
		out.println("<html><head></head><body>");
		out.println("<h1>" + message + "</h1>");
		out.println("<br /><br />");
		
		// As long as the client does not log out, the session table needs to be updated
		if (!logOut) {
			// update the session table
			client.updateSessionTable(message);
			// Cookie expires with the session expiration
			cookie.setMaxAge((int) client.getTimeOut());
			// create the data string for the cookie and save it
			cookie.setValue(client.createCookie());
			response.addCookie(cookie);
			out.println("<form method=\"get\" action=\"management\">");
			out.println("<input type=\"submit\" name=\"command\" value=\"Replace\">");
			out.println("<input type=\"text\" maxlength=\"256\" name=\"message\">");
			out.println("<br /><br />");
			out.println("<input type=\"submit\" name=\"command\" value=\"Refresh\">");
			out.println("<br /><br />");
			out.println("<input type=\"submit\" name=\"command\" value=\"Logout\">");
			out.println("<br /><br />");
			out.println("</form>");
		}		
		out.println("Cookies: " + client.createCookie());
		out.println("Expires: " + client.getExpirationTime());
		
	}

}
