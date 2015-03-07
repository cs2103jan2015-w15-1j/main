import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
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
    
    private static final String KEYWORD_DEADLINE = "by";
    private static final String KEYWORD_TIMED = "on";
	
	private static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static ArrayList<String> monthsArray = new ArrayList<String>(Arrays.asList(months));
	
	private Type type;
	private String rawInfo; // Unformatted arguments
	private String info; // arguments without the date and time
	private LocalDate date;
	private Integer month;
	private Integer day;
	private boolean isCompleted;
	
	private transient ArrayList<String> stringArrayList; // transient so that gson won't convert it to json
	private transient int sizeOfStringList;
	
    public Task(String information) {
        rawInfo = information;
        isCompleted = false;
        initListOfInputs(information);
        type = determineType();
        info = extractInfo();
        assert (info != null);
        initDate();
    }

    private void initDate() {
        date = extractDate();
        if (date != null) {
            month = date.getMonthValue();
            day = date.getDayOfMonth();
        }
    }

    private LocalDate extractDate() {
        String inputDate;
        switch (type) {
            case DEADLINE :
                inputDate = getInputDate(KEYWORD_DEADLINE);
                return new Date(inputDate).getLocalDateObj();
            case TIMED :
                inputDate = getInputDate(KEYWORD_TIMED);
                return new Date(inputDate).getLocalDateObj();
            case FLOATING :
            default :
                return null;
        }
    }

    private String getInputDate(String keyword) {
        int startIndexOfDate = rawInfo.lastIndexOf(keyword) + keyword.length() +
                               1;
        String inputDate = rawInfo.substring(startIndexOfDate);
        return inputDate;
    }

    private Type determineType() {
        if (isDeadline()) {
            return Type.DEADLINE;
        } else if (isTimed()) {
            return Type.TIMED;
        } else {
            return Type.FLOATING;
        }
    }

    // Get the raw info of the task
	public String getRawInfo() {
		return rawInfo;
	}
	
	public String getInfo() {
		return info;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public Integer getDay() {
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
        stringArrayList = new ArrayList<String>(Arrays.asList(stringArr));
        sizeOfStringList = stringArrayList.size();
    }
	
	// Get the description of the task
	private String extractInfo() {
	    switch (type) {
	        case DEADLINE :
	            return extractInfoDeadline();
	        case TIMED :
	            return extractInfoTimed();
	        case FLOATING :
	            return extractInfoFloat();
	        default :
	            return null;
	    }
	}


	// Get the word which correspond with the index from behind
	private String getWord(int indexFromBehind) {
		String[] stringArr = rawInfo.split(" ");
		int length = stringArr.length;
		return stringArr[length-indexFromBehind];
	}
	
	// Checks whether the task is a deadline task
	private boolean isDeadline() {
	    int index = stringArrayList.lastIndexOf("by"); 
		if (0 < index) {
			return isDeadlineHelper(stringArrayList.subList(index, sizeOfStringList));
		}
		return false;
	}
	
	// Checks whether the task is a timed task
	private boolean isTimed() {
	    int index = stringArrayList.lastIndexOf("at");
		if (0 < index) {
			return isTimedHelper(stringArrayList.subList(index, sizeOfStringList));
		}
		return false;
	}
	
	// Extract out the main info of a deadline task
	private String extractInfoDeadline() {
        ArrayList<String> localStringList = new ArrayList<String>(stringArrayList);
		elementDeleter(localStringList, 3);
		return stringFormatter(localStringList);
	}
	
	// Extract out the main info of a timed task
	private String extractInfoTimed() {
	    ArrayList<String> localStringList = new ArrayList<String>(stringArrayList);
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
