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
 * API: getRawInfo (), getInfo(), getDay(), getMonth(), getTime(), getTaskStatus(), markAsComplete()
 */

public class Task {
    public static enum Type {
        FLOATING, TIMED, DEADLINE
    };
	
	private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static ArrayList<String> monthsArray = new ArrayList<String>(Arrays.asList(months));
	
	private String rawInfo; // Unformatted argumuents
	private String info; // arguments without the date and time
	private String month;
	private String day;
	private boolean isCompleted;
	
	private transient ArrayList<String> stringList; // transient so that gson won't convert it to json
	private transient int sizeOfStringList;
	
	public Task (String information) {
	    initListOfInputs(information);
		rawInfo = information;
		isCompleted = false;
		info = extractInfo();
		month = extractMonth();
		day = extractDay();
	}

    // Get the raw info of the task
	public String getRawInfo() {
		return rawInfo;
	}
	
	public String getInfo() {
		return info;
	}
	
	public String getMonth() {
		return month;
	}
	
	public String getDay() {
		return day;
	}

	// Get the time of the timed task. Return null if it is a deadline or floating task
	// Note that this method will return something like "1200-1400"
	// You can use String.split("-") to separate the two numbers and store them in String[]
	public String getTime() {
		if (!isTimed()) {
			return null;
		}
		return getWord(4);
	}

	// Checks whether task has been done or not
	public boolean getTaskStatus() {
		return isCompleted;
	}
	
	// Mark a task as complete
	public void markAsComplete() {
		isCompleted = true;
	}
	
    private void initListOfInputs(String information) {
        String[] stringArr = information.split(" ");
        stringList = new ArrayList<String>(Arrays.asList(stringArr));
        sizeOfStringList = stringList.size();
    }
	
	// Get the description of the task
	private String extractInfo() {
		if (isDeadline()) {
			return extractInfoDeadline();
		} else if (isTimed()) {
			return extractInfoTimed();
		} else {
			return extractInfoFloat();
		}
	}

	// Get the month of the timed or deadline task. Return null if it is a floating task
	private String extractMonth() {
		if (isDeadline() || isTimed()) {
		    return getWord(1);
		}
		return null;
	}

	// Get the day of the timed or deadline task. Return null if it is a floating task
	private String extractDay() {
		if (isDeadline() || isTimed()) {
		    return getWord(2);
		}
		return null;
	}

	// Get the word which correspond with the index from behind
	private String getWord(int indexFromBehind) {
		String[] stringArr = rawInfo.split(" ");
		int length = stringArr.length;
		return stringArr[length-indexFromBehind];
	}
	
	// Checks whether the task is a deadline task
	private boolean isDeadline() {
	    int index = stringList.lastIndexOf("by"); 
		if (0 < index) {
			return isDeadlineHelper(stringList.subList(index, sizeOfStringList));
		}
		return false;
	}
	
	// Checks whether the task is a timed task
	private boolean isTimed() {
	    int index = stringList.lastIndexOf("at");
		if (0 < index) {
			return isTimedHelper(stringList.subList(index, sizeOfStringList));
		}
		return false;
	}
	
	// Extract out the main info of a deadline task
	private String extractInfoDeadline() {
        ArrayList<String> localStringList = new ArrayList<String>(stringList);
		elementDeleter(localStringList, 3);
		return stringFormatter(localStringList);
	}
	
	// Extract out the main info of a timed task
	private String extractInfoTimed() {
	    ArrayList<String> localStringList = new ArrayList<String>(stringList);
		elementDeleter(localStringList, 5);
		return stringFormatter(localStringList);
	}
	
	// Delete the number of elements from behind an ArrayList
	private void elementDeleter(ArrayList<String> array, int amountFromBack) {
		for (int i = 1; i <= amountFromBack; i++) {
			array.remove(array.size() - 1);
		}
	}
	
	// Extract out the main info for a floating task
	private String extractInfoFloat() {
		return rawInfo;
	}
	
	// Checks the last 3 words of the rawInfo. It has to follow format in order to be
	// considered a deadline task
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
	
	// Check the last 5 words of the rawInfo. It has to follow the format in order to be
	// considered a timed task
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
		result = result.trim();
		return result;
	}
}
