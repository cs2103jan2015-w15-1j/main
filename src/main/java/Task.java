package main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

public class Task implements Cloneable {
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
        if (getDate() != null) {
            return new SimpleStringProperty(getDate().toString());
        } else {
            return new SimpleStringProperty("Not Applicable");
        }
    }

    // ================================================================
    // End of MX's edits
    // ================================================================

    public Task(String input, ArrayList<LocalDateTime> parsedDates, String parsedWords) {
        markAsIncomplete();
        type = determineType(parsedDates);
        initDateAndTime(type, parsedDates);
        description = extractDescription(input, parsedWords);
    }

    // ================================================================
    // Public getters
    // ================================================================

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
    
    public boolean isOverdue() {
    	LocalDate nowDate = LocalDate.now();
    	return getDate().isBefore(nowDate) || getDate().isEqual(nowDate);
    }


    // ================================================================
    // Public setters
    // ================================================================

    public void setDescription(String input) {
        description = input;
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

    public void setTypeDateTime(ArrayList<LocalDateTime> parsedDates) {
        type = determineType(parsedDates);
        initDateAndTime(type, parsedDates);
    }

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
//        String result = HEADER_DESC + getDescription() +"\n";
//        if (getDate() == null) {
//            result += HEADER_DATE + HEADER_NOT_APPL + "\n";
//        } else {
//            result += HEADER_DATE + getDate() + "\n";
//        }
//        if (getStartTime() == null || getEndTime() == null) {
//            result += HEADER_TIME + HEADER_NOT_APPL + "\n\n";
//        } else {
//            result += HEADER_TIME + getStartTime() + " to " + getEndTime() + "\n\n";
//        }
        String result = Character.toUpperCase(getDescription().charAt(0)) + getDescription().substring(1);
        if (getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");
            // javadoc reference: http://goo.gl/GCyd5E
            result += " on " + getDate().format(formatter);
        }
        if (getStartTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ha");
            result += " from " + getStartTime().format(formatter) + " to " + getEndTime().format(formatter);
        }
        return result;
    }

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
}
