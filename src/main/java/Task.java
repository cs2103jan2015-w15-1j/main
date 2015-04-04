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
 * Floating task: no keywords required. Example: "do assignment" Deadline task:
 * one date must be provided. Example: "do assignment by 23 Mar" Timed-task: two
 * times must be provided for one date. Example:
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
        FLOATING, DEADLINE, TIMED
    };

    // private static final String HEADER_DESC = "Description: ";
    // private static final String HEADER_TIME = "Time: ";
    // private static final String HEADER_DATE = "Deadline: ";
    // private static final String HEADER_NOT_APPL = "Not applicable";

    private static final char ESCAPE_CHAR = '"';

    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_SECOND_DATE = 1;
    private static final String[] KEYWORDS = { "by", "on", "at", "from",
            "until", "til", "every", "day", "week", "month"};

    private Type type;
    private String description; // arguments without the date and time
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isCompleted;
    private String recurID;

    // ================================================================
    // Start of MX's edits
    // ================================================================
    // Methods
    // Note: Property attributes (part of JavaFX) are created on the fly as
    // there were problems with serialization

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

    public Task(String input, ArrayList<LocalDateTime> parsedDates,
            String parsedWords, String nonParsedWords) {
        markAsIncomplete();
        type = determineType(parsedDates);
        initDateAndTime(type, parsedDates);
        description = extractDescription(input, nonParsedWords);
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

    public String getID() {
        return recurID;
    }

    public boolean isOverdue() {
        LocalDate nowDate = LocalDate.now();
        return getDate() != null && nowDate.isAfter(getDate());
    }

    // ================================================================
    // Public setters
    // ================================================================

    public void setID(String input) {
        recurID = input;
    }

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

    public boolean updateTypeDateTime(ArrayList<LocalDateTime> parsedDates) {
        return updateDateAndTime(determineType(parsedDates), parsedDates);
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
        case 2:
            return Type.TIMED;
        case 1:
            return Type.DEADLINE;
        default:
            return Type.FLOATING;
        }
    }

    private void initDateAndTime(Type type, ArrayList<LocalDateTime> parsedDates) {
        switch (type) {
        case TIMED:
            date = parsedDates.get(POSITION_FIRST_DATE).toLocalDate();
            startTime = parsedDates.get(POSITION_FIRST_DATE).toLocalTime();
            endTime = parsedDates.get(POSITION_SECOND_DATE).toLocalTime();
            break;
        case DEADLINE:
            date = parsedDates.get(POSITION_FIRST_DATE).toLocalDate();
            break;
        default:
            break;
        }
    }

    private boolean updateDateAndTime(Type newType,
            ArrayList<LocalDateTime> parsedDates) {
        // parsedDates correspond to the parsedDates of the update
        // type refers to CURRENT type
        switch (newType) {
        case FLOATING:
            setDate(null);
            setStartTime(null);
            setEndTime(null);
            break;
        case DEADLINE:
            if (type.equals(Type.FLOATING)) {
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
            } else if (type.equals(Type.DEADLINE)) {
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
            } else { // current: TIMED
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
                return true;
            }
            break;
        case TIMED:
            if (type.equals(Type.FLOATING)) {
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
            } else if (type.equals(Type.DEADLINE)) {
                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
            } else { // current: TIMED
                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
            }
            break;
        default:
            break;
        }
        setType(newType);
        return true;
    }

    /**
     * Get the description of the task
     *
     * @param input
     *            - user's raw input
     * @param nonParsedWords
     *            - words that were used to obtain the dates from user input
     * @return description
     */
    private String extractDescription(String input, String nonParsedWords) {
        if (hasTwoEscapeChars(input)) {
            return getWordsWithinEscapeChars(input);
        } else {
            // convert input arguments to string arrays
            String[] wordArr = nonParsedWords.split(" ");

            // convert input string array to arraylist of strings
            ArrayList<String> wordArrayList = new ArrayList<String>(
                    Arrays.asList(wordArr));

            // reverse as we want to delete words from the back
            Collections.reverse(wordArrayList);

            // delete keywords that do not make up the description of tasks
            for (String word : KEYWORDS) {
                wordArrayList.remove(word);
            }

            Collections.reverse(wordArrayList);

            String description = stringFormatter(wordArrayList);
            // return description;
            return description.replace("\"", "");
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
        // String result = HEADER_DESC + getDescription() +"\n";
        // if (getDate() == null) {
        // result += HEADER_DATE + HEADER_NOT_APPL + "\n";
        // } else {
        // result += HEADER_DATE + getDate() + "\n";
        // }
        // if (getStartTime() == null || getEndTime() == null) {
        // result += HEADER_TIME + HEADER_NOT_APPL + "\n\n";
        // } else {
        // result += HEADER_TIME + getStartTime() + " to " + getEndTime() +
        // "\n\n";
        // }
        String result = getDescription();
        // String result = Character.toUpperCase(getDescription().charAt(0)) +
        // getDescription().substring(1);
        if (getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("EEEE, d MMMM y");
            // javadoc reference: http://goo.gl/GCyd5E
            result += " on " + getDate().format(formatter);
        }
        if (getStartTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h.mma");
            result += " from " + getStartTime().format(formatter) + " to "
                    + getEndTime().format(formatter);
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
