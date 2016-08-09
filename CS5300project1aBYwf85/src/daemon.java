import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This is for removing timed-out sessions from the session table.
 */
public class daemon extends TimerTask {

	@Override
	public void run() {
		Session S = new Session();
		Date date = new Date();
		long time = date.getTime()/1000;
		ConcurrentHashMap<String, String[]> table = S.getSessionDataTable();
		Set<String> keyset = table.keySet();
		Iterator<String> iter = keyset.iterator();
		System.out.println("Daemon thread starts");
		while(iter.hasNext()){
			String[] str = table.get(iter.next());
			// Compute the difference between current time and timestamp,
			// if it is bigger than the timeout interval, the corresponding
			// session state should be removed.
			if(time-Long.parseLong(str[3], 10)>S.getTimeOut()){
				table.remove(iter.next());			
			}
		}
		System.out.print("Daemon thread ends");
	}

}
