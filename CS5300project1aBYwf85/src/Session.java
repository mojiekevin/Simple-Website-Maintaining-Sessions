import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * Manage the user's session data which is stored in a ConcurrentHashMap 
 */
public class Session {
	private String sessionID;
	private Integer version;
	private Timestamp expiration = new Timestamp(0);
	private ConcurrentHashMap<String, String[]> sessionDataTable = new ConcurrentHashMap<String, String[]>();
	private long timeOut = 1000;
	
	/**Creates session object*/
	public Session() {
		setExpiration();
		version = 0;
	}
	
	/** return session data table */
	public ConcurrentHashMap<String, String[]> getSessionDataTable() {
		return sessionDataTable;
	}	
	
	/** return timeOut value */
	public long getTimeOut() {
		return timeOut;
	}	

	/** return sessionID */
	public String getSessionID() {
		return sessionID;
	}
	
	/** return current version */
	public int getVersionNumber() {
		return version;
	}	
	
	/** return message */
	public String getMessage() {
		try{
			return sessionDataTable.get(sessionID)[1];
		}
		catch(NullPointerException e){
			return "Hello User";
		}
	}	
	
	/** return expiration time */
	public Timestamp getExpirationTime() {
		return expiration;
	}
	
	/** Set sessionID to given value */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}	
	
	/** Set current version to given value */
	public void setVersionNumber(Integer versionNumber) {
		this.version = versionNumber;
	}	

	/** Set expiration timestamp */
	public void setExpiration() {
		Date date = new Date();
		expiration.setTime(date.getTime() + timeOut);
	}	
	
	/** Update message in session table */
	public void updateSessionTable(String message){
		// Limit the size of the session state value
		message = message.length()>256?message.substring(0,256):message;
		//Increment the version number
		version++;
		String[] sessionTable = {version.toString(), message, expiration.toString(), String.valueOf(expiration.getTime())};
		sessionDataTable.put(sessionID, sessionTable);
	}
	
	/** Create cookie with session attributes*/
	public String createCookie() {
		return this.getSessionID() + "_" + this.getVersionNumber();
	}
	
}