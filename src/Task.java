import java.util.ArrayList;
import java.util.Arrays;

/**
 * Let us standardize what kind of keywords we accept for commands for V0.1
 * 
 * Floating task : no keywords required
 * 
 * Deadline task: "by" followed by a number representing day and the short-form name for month
 *                (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
 *                Example: "do assignment by Mar"
 * 
 * Timed-task:    "at" followed by two 24-hour notation representing start and end, "on" followed by number
 * 			      representing day and the short-form name for month
 *			      (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
 *                Example: "attend meeting at 1200 1400 on Apr"
 */

public class Task {
	
	private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static ArrayList<String> monthsArray = new ArrayList<String>(Arrays.asList(months));
	
	private String info;
	private String taskType;
	private boolean isCompleted;
	private int day;
	private int month;
	
	public Task (String information) {	
		info = information;
		// need to process the information to check whether it is floating or not
		
		// if got "by" and any month from the array = deadline task
		// if got "at", "on" and any month from the array = timed-task
		// else it is floating
	}
	
	public boolean isDeadline() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("by")) {
			if (stringList.lastIndexOf("by")+1 == arrayLength-1) {
				if (monthsArray.contains(stringList.get(stringList.lastIndexOf("by")+1))) {
					return true;
				}
			}
		}
		return false;
	}
}
