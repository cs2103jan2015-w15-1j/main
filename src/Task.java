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
 *                Example: "do assignment by Mar"
 * 
 * Timed-task:    "at" followed by two 24-hour notation representing start and end (seperated by "-" without spacing,
 * 				  "on" followed by number representing day and the short-form name for month
 *			      (Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
 *                Example: "attend meeting at 1200-1400 on Apr"
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
	
	public boolean isTimedTask() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("at")) {
			return isTimedTaskHelper(stringList.subList(stringList.lastIndexOf("at"), arrayLength));
		}
		return false;
	}
	
	public boolean isTimedTaskHelper(List<String> list) {
		boolean answer = true;
		if (list.size() == 4) {
			for (int index = 0; index < 4; index++) {
				if (index == 1) {
					String date = list.get(index);
					String[] dateArray = date.split("-");
					if (dateArray.length == 2) {
						try {
							Integer.parseInt(dateArray[0]);
							Integer.parseInt(dateArray[1]);
						} catch (NumberFormatException e) {
							answer = false;
						}
					}
				} else if (index == 2) {
					if (!list.get(index).equals("at")) {
						answer = false;
					}
				} else {
					if (!monthsArray.contains(list.get(index))) {
						answer = false;
					}
				}
			}
		} else {
			answer = false;
		}
		return answer;
	}
}
