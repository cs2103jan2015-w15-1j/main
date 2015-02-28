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
 *                
 * NOTE THAT IF THE INFORMATION DOES NOT FOLLOW FORMAT OF DEADLINE AND TIMED TASK, IT WILL BE DEEMED AS FLOATING TASK
 * 
 * API: getInfo(), getDay(), getMonth(), getTime(), getTaskStatus(), markAsComplete()
 */

public class Task {
	
	private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static ArrayList<String> monthsArray = new ArrayList<String>(Arrays.asList(months));
	
	private String info;
	private boolean isCompleted;

	public Task (String information) {
		info = information;
		isCompleted = false;
	}
	
	// Get the description of the task
	public String getInfo() {
		if (isDeadline()) {
			return extractInfoDeadline();
		} else if (isTimed()) {
			return extractInfoTimed();
		} else {
			return extractInfoFloat();
		}
	}
	
	// Get the day of the timed or deadline task. Return null if it is a floating task
	public String getDay() {
		if (!(isDeadline() || isTimed())) {
			return null;
		}
		String[] stringArr = info.split(" ");
		int length = stringArr.length;
		return stringArr[length-2];
	}

	// Get the month of the timed or deadline task. Return null if it is a floating task
	public String getMonth() {
		if (!(isDeadline() || isTimed())) {
			return null;
		}
		String[] stringArr = info.split(" ");
		int length = stringArr.length;
		return stringArr[length-1];
	}
	
	// Get the time of the timed task. Return null if it is a deadline or floating task
	// Note that this method will return something like "1200-1400"
	// You can use String.split("-") to separate the two numbers and store them in String[]
	public String getTime() {
		if (!isTimed()) {
			return null;
		}
		String[] stringArr = info.split(" ");
		int length = stringArr.length;
		return stringArr[length-4];
	}

	// Checks whether task has been done or not
	public boolean getTaskStatus() {
		return isCompleted;
	}
	
	// Mark a task as complete
	public void markAsComplete() {
		isCompleted = true;
	}
	
	// Checks whether the task is a deadline task
	private boolean isDeadline() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("by")) {
			return isDeadlineHelper(stringList.subList(stringList.lastIndexOf("by"), arrayLength));
		}
		return false;
	}
	
	// Checks whether the task is a timed task
	private boolean isTimed() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		int arrayLength = stringList.size();
		if (0 < stringList.lastIndexOf("at")) {
			return isTimedHelper(stringList.subList(stringList.lastIndexOf("at"), arrayLength));
		}
		return false;
	}
	
	// Extract out the main info of a deadline task
	private String extractInfoDeadline() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		elementDeleter(stringList, 3);
		return stringFormatter(stringList);
	}
	
	// Extract out the main info of a timed task
	private String extractInfoTimed() {
		String[] stringArr = info.split(" ");
		ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(stringArr));
		elementDeleter(stringList, 5);
		return stringFormatter(stringList);
	}
	
	// Delete the number of elements from behind an ArrayList
	private void elementDeleter(ArrayList<String> array, int amountFromBack) {
		for (int i = 1; i <= amountFromBack; i++) {
			array.remove(array.size() - i);
		}
	}
	
	// Extract out the main info for a floating task
	private String extractInfoFloat() {
		return info;
	}
	
	// Helper function for "isDeadline" method 
	private boolean isDeadlineHelper(List<String> list) {
		if (list.size() != 3) {
			return false;
		} else if (!monthsArray.contains(list.get(2))) {
			return false;
		} else {
			try {
				Integer.parseInt(list.get(1));
			} catch (NumberFormatException e) {
				return false;
			}  
		} 
		return true;
	}
	
	// Helper function for "isTimed" method
	private boolean isTimedHelper(List<String> list) {
		if (list.size() != 5) {
			return false;
		} else if (!monthsArray.contains(list.get(4))) {
			return false;
		} else if (!list.get(2).equals("on")) {
			return false;
		} else {
			String[] array = list.get(1).split("-");
			if (array.length != 2) {
				return false;
			} else {
				try {
					Integer.parseInt(array[0]);
					Integer.parseInt(array[1]);
					Integer.parseInt(list.get(3));
				} catch (NumberFormatException e) {
					return false;
				} catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Format the elements in the ArrayList to one single string
	private String stringFormatter(ArrayList<String> strList) {
		String result = "";
		for (String word: strList) {
			result += word + " ";
		}
		result.trim();
		return result;
	}
}
