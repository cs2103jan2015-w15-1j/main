package main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang.StringUtils;


/**
 * This class contains the information of a task element.
 *
 * Types of tasks:
 *
 * Floating task: no keywords required. Example: "do assignment"
 * Deadline task: one date must be provided. Example: "do assignment by 23 Mar"
 * Timed-task: two times must be provided for one date. Example:
 * "attend meeting at 1200 - 1400 on 20 Apr"
 *
 * NOTE THAT IF THE INFORMATION DOES NOT FOLLOW THE ABOVE FORMAT, IT WILL BE
 * DEEMED AS A FLOATING TASK.
 *
 * API:
 *
 * Getters: getRawInfo(), getType(), getDescription(), getDate(),
 * getStartTime(), getEndTime(), isCompleted()
 *
 * Setters: setDescription(String), setDate(LocalDate), setTime(LocalTime,
 * LocalTime), markAsComplete()
 */

public class Task {
    public static enum Type {
        FLOATING, TIMED, DEADLINE
    };

    private static final String HEADER_DESC = "Description: ";
    private static final String HEADER_TIME = "Time: ";
    private static final String HEADER_DATE = "Deadline: ";
    private static final String HEADER_NOT_APPL = "Not applicable";

    private static final char ESCAPE_CHAR = '"';

    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_SECOND_DATE = 1;
    private static final String[] KEYWORDS = {"by", "on", "at", "from"};

    private Type type;
    private String rawInfo; // Unformatted arguments
    private String description; // arguments without the date and time
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isCompleted;

    // ================================================================
    // Start of MX's edits
    // ================================================================
    // Methods
    // Note: Property attributes are created on the fly because there was some problem with serialization

    public StringProperty getTaskDesc() {
        return new SimpleStringProperty(getDescription());
    }

    public ObjectProperty<LocalDate> getTaskDate() {
        return new SimpleObjectProperty<LocalDate>(getDate());
    }

    public StringProperty getStringPropertyTaskDate() {
        return new SimpleStringProperty(getDate().toString());
    }

    // ================================================================
    // End of MX's edits
    // ================================================================

    public Task(String input) {
        rawInfo = input;
        isCompleted = false;

        if (hasTwoEscapeChars(input)) {
            input = getWordsOutsideEscapeChars(input);
        }

        DateParser parser = new DateParser(input);

        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        type = determineType(parsedDates);

        initDateAndTime(type, parsedDates);

        String parsedWords = parser.getParsedWords();
        description = extractDescription(rawInfo, parsedWords);

        // MX edits within the constructor

    }

    // ================================================================
    // Public getters
    // ================================================================

    public String getRawInfo() {
        return rawInfo;
    }

    public Type getType() {
        return type;
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


    // ================================================================
    // Public setters
    // ================================================================

    public void setDescription(String input) {
        description = input;
    }

    public void setDate(LocalDate inputDateObj) {
        date = inputDateObj;
        if (type == Type.FLOATING) {
        	setType(Type.DEADLINE);
        }
    }

    public void setTime(LocalTime inputStartTime, LocalTime inputEndTime) {
        if (inputStartTime.isBefore(inputEndTime)) {
            setType(Type.TIMED);
            startTime = inputStartTime;
            endTime = inputEndTime;
        }
    }

    public void markAsComplete() {
        isCompleted = true;
    }

    public void markAsIncomplete() {
    	isCompleted = false;
    }


    // ================================================================
    // Private setters
    // ================================================================

    private void setType(Type newType) {
        type = newType;
    }


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
                date = parsedDates.get(POSITION_FIRST_DATE).toLocalDate();
                startTime = parsedDates.get(POSITION_FIRST_DATE).toLocalTime();
                endTime = parsedDates.get(POSITION_SECOND_DATE).toLocalTime();
                break;
            case DEADLINE :
                date = parsedDates.get(POSITION_FIRST_DATE).toLocalDate();
                break;
            default :
                break;
        }
    }

    /**
     * Get the description of the task
     *
     * @param input - user's raw input
     * @param parsedWords - words that were used to obtain the dates from user input
     * @return description
     */
    private String extractDescription(String input, String parsedWords) {
        if (hasTwoEscapeChars(input)) {
            return getWordsWithinEscapeChars(input);
        } else if (parsedWords != null) {
            // convert input arguments to string arrays
            String[] parsedWordsArr = parsedWords.split(" ");
            String[] inputArr = input.split(" ");

            // convert input string array to arraylist of strings
            ArrayList<String> inputArrayList = new ArrayList<String>(Arrays.asList(inputArr));

            // reverse as we want to delete words from the back
            Collections.reverse(inputArrayList);

            // delete words that were used to obtain the dates
            for (String word : parsedWordsArr) {
                inputArrayList.remove(word);
            }

            // delete keywords that do not make up the description of tasks
            for (String word : KEYWORDS) {
                inputArrayList.remove(word);
            }

            Collections.reverse(inputArrayList);

            String description = stringFormatter(inputArrayList);
            return description.replace("\"", "");
        } else {
            return input.replace("\"", "");
        }
    }


    // ================================================================
    // Utility Methods
    // ================================================================

    private boolean hasTwoEscapeChars(String input) {
        return StringUtils.countMatches(input, ESCAPE_CHAR + "") == 2;
    }

    private String getWordsOutsideEscapeChars(String input) {
        String output = "";
        boolean withinEscapeChar = false;
        for (char c : input.toCharArray()) {
            if (c == ESCAPE_CHAR) {
                if (withinEscapeChar) {
                    withinEscapeChar = false;
                } else {
                    withinEscapeChar = true;
                }
            } else if (!withinEscapeChar) {
                output += c;
            }
        }
        return output;
    }

    private String getWordsWithinEscapeChars(String input) {
        String output = "";
        boolean withinEscapeChar = false;
        for (char c : input.toCharArray()) {
            if (c == ESCAPE_CHAR) {
                if (withinEscapeChar) {
                    withinEscapeChar = false;
                } else {
                    withinEscapeChar = true;
                }
            } else if (withinEscapeChar) {
                output += c;
            }
        }
        return output;
    }

    // Format the elements in the ArrayList to one single string
    private String stringFormatter(ArrayList<String> strList) {
        String result = "";
        for (String word : strList) {
            result += word + " ";
        }
        return result.trim();
    }

    public String toString() {
    	String result = HEADER_DESC + getDescription() +"\n";
    	if (getDate() == null) {
    		result += HEADER_DATE + HEADER_NOT_APPL + "\n";
    	} else {
    		result += HEADER_DATE + getDate() + "\n";
    	}
    	if (getStartTime() == null || getEndTime() == null) {
    		result += HEADER_TIME + HEADER_NOT_APPL + "\n\n";
    	} else {
    		result += HEADER_TIME + getStartTime() + " to " + getEndTime() + "\n\n";
    	}
    	return result;
    }
}