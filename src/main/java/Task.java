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
        while (description.substring(description.length() - 1).equals(" ")) {
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

    public boolean isOverdue() {
        LocalDate nowDate = LocalDate.now();
        return getDate() != null && nowDate.isAfter(getDate());
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

    public void update(String input, ArrayList<LocalDateTime> parsedDates,
                          String parsedWords, String notParsedWords) {
        type = determineType(parsedDates);
        initDateAndTime(type, parsedDates);
        // substring to get rid of the index being part of description
        description = extractDescription(notParsedWords).substring(2);

        updateAll(description, determineType(parsedDates), parsedDates);
    }
    
    public void setException(ArrayList<LocalDate> dates) {
        exceptionDates = dates;
    }
    
    public void addException(LocalDate date){
        exceptionDates.add(date);
    }

    // ================================================================
    // Initialization Methods
    // ================================================================

    //@author A0121520A    
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
            LocalTime time = parsedDates.get(POSITION_FIRST_DATE).toLocalTime();
            if (time.getNano() == 0) {
                startTime = time;
            }
            break;
        default:
            break;
        }
    }

    //@author A0122081X
    private void unsetAllProperties() {
        setType(null);
        setDescription(null);
        setDate(null);
        setTime(null, null);
    }

    private void updateAll(String newDesc, Type newType,
                              ArrayList<LocalDateTime> parsedDates) {

        unsetAllProperties();
        setDescription(newDesc);
        setType(newType);

        switch (newType) {
            case FLOATING :
                break;
            case DEADLINE :
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
                break;
            case TIMED :
                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
        }

        // KEPT FOR THE MOMENT IN CASE THERE ARE CHANGES TO EDIT AGAIN
//        // parsedDates correspond to the parsedDates of the update
//        // type refers to CURRENT type
//        switch (newType) {
//        case FLOATING:
//            setDate(null);
//            setStartTime(null);
//            setEndTime(null);
//            break;
//        case DEADLINE:
//            if (type.equals(Type.FLOATING)) {
//                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
//            } else if (type.equals(Type.DEADLINE)) {
//                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
//            } else { // current: TIMED
//                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
//                return true;
//            }
//            break;
//        case TIMED:
//            if (type.equals(Type.FLOATING)) {
//                setDate(parsedDates.get(POSITION_FIRST_DATE).toLocalDate());
//                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
//                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
//            } else if (type.equals(Type.DEADLINE)) {
//                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
//                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
//            } else { // current: TIMED
//                setStartTime(parsedDates.get(POSITION_FIRST_DATE).toLocalTime());
//                setEndTime(parsedDates.get(POSITION_SECOND_DATE).toLocalTime());
//            }
//            break;
//        default:
//            break;
//        }
//        setType(newType);
//        return true;
    }

    //@author A0121520A    
    /**
     * Get the description of the task
     * @param notParsedWords
     * @return description
     */
    private String extractDescription(String notParsedWords) {
        String[] wordArr = notParsedWords.split(" ");
        ArrayList<String> wordArrayList = new ArrayList<String>(Arrays.asList(wordArr));

        // reverse as we want to delete words from the back
        Collections.reverse(wordArrayList);

        // delete keywords that do not make up the description of tasks
        for (String word : KEYWORDS) {
            wordArrayList.remove(word);
        }

        Collections.reverse(wordArrayList);

        String description = StringUtils.join(wordArrayList, ' ');
        return description.replace("\"", "");
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
    
    //@author A0121520A 
    // ================================================================
    // Utility Methods
    // ================================================================
    @Override
    public String toString() {
        String result = getDescription() + " " + getFormattedTimeAndDate(true);
        return result.trim();
    }
    
    public String getFormattedTimeAndDate(boolean includeDate) {
        String result = "";
        if (getStartTime() != null) {
            result += addFormattedTime() + " ";
        }
        if (includeDate) {
            result += addFormattedDate();
        }
        return result.trim();
    }
    
    //@author A0121813U 
    private String addFormattedTime() {
        // formats the time for the time label, eg 2:00PM to 4:00PM
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h.mma");
        LocalTime startTime = getStartTime();
        LocalTime endTime = getEndTime();
        if (startTime != null) {
            if (endTime != null) {
                return startTime.format(timeFormatter).toLowerCase()
                        + " to " + endTime.format(timeFormatter).toLowerCase();
            } else {
                return startTime.format(timeFormatter).toLowerCase();
            }
        }
        return "";
    }

    private String addFormattedDate() {
        // javadoc reference: http://goo.gl/GCyd5E
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern("EEEE, d MMMM y");
        if (getDate() != null) {
            return getDate().format(dateFormatter);
        }
        return "";
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
}
