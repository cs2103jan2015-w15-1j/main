import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Let us standardize what kind of keywords we accept for commands for V0.1
 * 
 * Floating task : no keywords required
 * 
 * Deadline task: "by" followed by a number representing day and the short-form name for month
 *                (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
 *                Example: "do assignment by 23 Mar"
 * 
 * Timed-task:    "at" followed by two 24-hour notation representing start and end (seperated by "-" without spacing,
 * 				  "on" followed by number representing day and the short-form name for month
 *			      (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
 *                Example: "attend meeting at 1200-1400 on 20 Apr"
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
		isCompleted = false;
		// need to process the information to check whether it is floating or not
		
		// if got "by" and any month from the array = deadline task
		// if got "at", "on" and any month from the array = timed-task
		// else it is floating
	}
	
	public void taskCompleted() {
		isCompleted = true;
	}
	
	public boolean getTaskStatus() {
		return isCompleted;
	}
	
	public boolean isDeadline() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("by")) {
			return isDeadlineHelper(stringList.subList(stringList.lastIndexOf("by"), arrayLength));
		}
		return false;
	}
	
	public boolean isTimed() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("at")) {
			return isTimedHelper(stringList.subList(stringList.lastIndexOf("at"), arrayLength));
		}
		return false;
	}
	
	private boolean isDeadlineHelper(List<String> list) {
		if (list.size() != 3) {
			return false;
		} try {
			Integer.parseInt(list.get(1));
		} catch (NumberFormatException e) {
			return false;
		} if (!monthsArray.contains(list.get(2))) {
			return false;
		} 
		return true;
	}

	private boolean isTimedHelper(List<String> list) {
		if (list.size() != 5) {
			return false;
		}
		String[] array = list.get(1).split("-");
		if (array.length != 2) {
			return false;
		} try {
			Integer.parseInt(array[0]);
			Integer.parseInt(array[1]);
		} catch (NumberFormatException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		} if (!list.get(2).equals("on")) {
			return false;
		} try {
			Integer.parseInt(list.get(3));
		} catch (NumberFormatException e) {
			return false;
		} if (!monthsArray.contains(list.get(4))) {
			return false;
		}
		return true;
	}
}
