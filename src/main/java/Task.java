package main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;

//@author A0121813U
/**
 * This class contains the information of a task element.
 *
 * Types of tasks:
 *
 * Floating task: no keywords required.
 * Example: "do assignment".
 * 
 * Deadline task: one date must be provided. It is optional to include a timing.
 * Example: "do assignment by 23 Mar" or "complete homework by 9pm today"
 * 
 * Timed-task: two times must be provided for one date.
 * Example: "attend meeting at 12pm to 2pm on 20 Apr"
 *
 * NOTE: For deadline and timed tasks, time should be typed first, then date.
 * 		 May not work if the order is reversed
 * 
 * API:
 *
 * Getters: getRawInfo(), getType(), getDescription(), getDate(),
 * getStartTime(), getEndTime(), isCompleted(), isOverdue(), getId(),
 * isRecurring(), getExceptionDates(), getFormattedTimeAndDate(boolean)
 *
 * Setters: setDescription(String), setDate(LocalDate), setTime(LocalTime,
 * LocalTime), markAsComplete(), markAsIncomplete(), setId(String), setRawInfo(String),
 * setException(ArrayList<LocalDate>)
 */
public class Task implements Cloneable {
    public static enum Type {
        FLOATING, DEADLINE, TIMED
    };

    private static final String ESCAPE_CHAR = "\"";
    private static final String STRING_EMPTY = "";
    private static final String STRING_EMPTY_SPACE = " ";
    private static final String STRING_TIME_FORMAT = "h.mma";
    private static final String STRING_DATE_FORMAT = "EEEE, d MMMM y";
    private static final String STRING_TIME_SEPARATOR = " to ";
    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_SECOND_DATE = 1;
    private static final String[] KEYWORDS = { "by", "on", "at", "from",
            "until", "till" , "except"};
    
    private String rawInfo;
    private Type type;
    private String description; // arguments without the date and time
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isCompleted;
    private String recurId;
    private ArrayList<LocalDate> exceptionDates;

    //@author A0122393L
    public Task(String input, ArrayList<LocalDateTime> parsedDates,
                String parsedWords, String notParsedWords) {
        markAsIncomplete();
        rawInfo = input;
        type = determineType(parsedDates);
        initDateAndTime(type, parsedDates);
        description = extractDescription(notParsedWords);
        while (description.substring(description.length() - 1).equals(STRING_EMPTY_SPACE)) {
            description = description.trim();
        }
    }

    //@author A0121813U
    // ================================================================
    // Public getters
    // ================================================================
    public Type getType() {
        return type;
    }
    
    public String getRawInfo() {
        return rawInfo;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getId() {
        return recurId;
    }
    
    public boolean isRecurring() {
        return getId() != null;
    }
    
    // Checks whether the task is overdue
    public boolean isOverdue() {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        return checkOverdue(nowDate, nowTime);
    }

    //@author A0122393L
    // ================================================================
    // Public setters
    // ================================================================
    public void setId(String input) {
        recurId = input;
    }
    
    public void setRawInfo(String input) {
        rawInfo = input;
    }

    public void setDescription(String input) {
        description = input;
    }

    public void setTime(LocalTime inputStartTime, LocalTime inputEndTime) {
        if (inputStartTime == null && inputEndTime == null) {
            startTime = null;
            endTime = null;
        } else if (inputStartTime.isBefore(inputEndTime)) {
            setType(Type.TIMED);
            startTime = inputStartTime;
            endTime = inputEndTime;
        }
    }

    public void markAsCompleted() {
        isCompleted = true;
    }

    public void markAsIncomplete() {
        isCompleted = false;
    }

    public void setException(ArrayList<LocalDate> dates) {
        exceptionDates = dates;
    }
    
    public void addException(LocalDate date){
        exceptionDates.add(date);
    }

    public ArrayList<LocalDate> getExceptionDates() {
        return exceptionDates;
    }

    //@author A0121520A
    // ================================================================
    // Utility Methods
    // ================================================================
    @Override
    public String toString() {
        String result = getDescription() + STRING_EMPTY_SPACE +
                        getFormattedTimeAndDate(true);
        return result.trim();
    }
    
    public String getFormattedTimeAndDate(boolean includeDate) {
        String result = STRING_EMPTY;
        if (getStartTime() != null) {
            result += addFormattedTime() + STRING_EMPTY_SPACE;
        }
        if (includeDate) {
            result += addFormattedDate();
        }
        return result.trim();
    }
    
    //@author A0122081X
    @Override
    public Task clone() throws CloneNotSupportedException {
        Task cloned = (Task) super.clone();

        // Set all the attributes
        cloned.setType(cloned.getType());
        cloned.setDescription(cloned.getDescription());
        cloned.setIsCompleted(cloned.isCompleted());
        cloned.setDate(cloned.getDate());
        cloned.setStartTime(cloned.getStartTime());
        cloned.setEndTime(cloned.getEndTime());
        return cloned;
    }
    
    
    //@author A0121520A
    // ================================================================
    // Initialization Methods
    // ================================================================

    // Determines type of task using the number of dates parsed.
    private Type determineType(ArrayList<LocalDateTime> parsedDates) {
        int numDates = parsedDates.size();
        switch (numDates) {
            case 2 :
                return Type.TIMED;
            case 1 :
                return Type.DEADLINE;
            default :
                return Type.FLOATING;
        }
    }

    private void initDateAndTime(Type type, ArrayList<LocalDateTime> parsedDates) {
        switch (type) {
            case TIMED :
                LocalDateTime firstDate = parsedDates.get(POSITION_FIRST_DATE);
                LocalDateTime secondDate = parsedDates.get(POSITION_SECOND_DATE);
                date = firstDate.toLocalDate();
                startTime = firstDate.toLocalTime();
                endTime = secondDate.toLocalTime();
                break;
            case DEADLINE :
                firstDate = parsedDates.get(POSITION_FIRST_DATE);
                date = firstDate.toLocalDate();
                LocalTime time = firstDate.toLocalTime();
                if (isUserEnteredTime(time)) {
                    startTime = time;
                }
                break;
            default :
                break;
        }
    }

    private boolean isUserEnteredTime(LocalTime time) {
        return time.getNano() == 0;
    }

    /**
     * Get the description of the task
     * 
     * @param notParsedWords
     * @return description
     */
    private String extractDescription(String notParsedWords) {
        String[] wordArr = notParsedWords.split(STRING_EMPTY_SPACE);
        ArrayList<String> wordArrayList = new ArrayList<String>(Arrays.asList(wordArr));

        removeKeywords(wordArrayList);

        String description = StringUtils.join(wordArrayList, STRING_EMPTY_SPACE);
        return description.replace(ESCAPE_CHAR, STRING_EMPTY);
    }

    private void removeKeywords(ArrayList<String> wordArrayList) {
        // reverse as we want to delete words from the back
        Collections.reverse(wordArrayList);

        // delete keywords that do not make up the description of tasks
        for (String word : KEYWORDS) {
            wordArrayList.remove(word);
        }

        Collections.reverse(wordArrayList);
    }
    
    //@author A0122081X
    // ================================================================
    // Private setters
    // ================================================================

    private void setDate(LocalDate date) {
        this.date = date;
    }

    private void setType(Type type) {
        this.type = type;
    }

    private void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    private void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    private void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
	// @author A0121813U
	// Change the format of the LocalTime object to our preference
	// javadoc reference: http://goo.gl/GCyd5E
	private String addFormattedTime() {
		DateTimeFormatter timeFormatter = DateTimeFormatter
				.ofPattern(STRING_TIME_FORMAT);
		LocalTime startTime = getStartTime();
		LocalTime endTime = getEndTime();
		if (startTime != null && endTime != null) {
			return startTime.format(timeFormatter).toLowerCase()
					+ STRING_TIME_SEPARATOR
					+ endTime.format(timeFormatter).toLowerCase();
		} else if (startTime != null) {
			return startTime.format(timeFormatter).toLowerCase();
		}
		return STRING_EMPTY;
	}
    
	// Change the format of the LocalDate object to our preference
	// javadoc reference: http://goo.gl/GCyd5E
	private String addFormattedDate() {
		DateTimeFormatter dateFormatter = DateTimeFormatter
				.ofPattern(STRING_DATE_FORMAT);
		if (getDate() != null) {
			return getDate().format(dateFormatter);
		}
		return STRING_EMPTY;
	}
    
	// Given a date and time, checks whether the task is overdue
	private boolean checkOverdue(LocalDate dateNow, LocalTime timeNow) {
		if (getDate() == null) {
			return false;
		} else if (getDate().isBefore(dateNow)) {
			return true;
		} else if (getDate().isAfter(dateNow)) {
			return false;
		} else {
			if (getStartTime() == null) {
				return false;
			} else if (getStartTime().isBefore(timeNow)) {
				return true;
			} else if (getStartTime().isAfter(timeNow)) {
				return false;
			} else {
				return true;
			}
		}
	}
}
