import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


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


    public Task(String input) {
        rawInfo = input;
        isCompleted = false;

        DateParser parser = new DateParser(input);

        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        type = determineType(parsedDates);

        initDateAndTime(type, parsedDates);

        String parsedWords = parser.getParsedWords();
        description = extractDescription(input, parsedWords);
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
    }

    public void setTime(LocalTime inputStartTime, LocalTime inputEndTime) {
        if (inputStartTime.isBefore(inputEndTime)) {
            if (startTime == null && endTime == null) {
                setType(Type.TIMED);
            }
            startTime = inputStartTime;
            endTime = inputEndTime;
        }
    }

    public void markAsComplete() {
        isCompleted = true;
    }
    

    // ================================================================
    // Private setters
    // ================================================================

    private void setType(Type newType) {
        type = newType;
    }
    

    // ================================================================
    // Initialisation Methods
    // ================================================================

    // Determines type of task using the number of dates parsed.
    private Type determineType(ArrayList<LocalDateTime> parsedDates) {
        int numDates = parsedDates.size();
        switch (numDates) {
            case 2 :
                return Type.TIMED;
            case 1 :
                return Type.DEADLINE;
            case 0 :
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
        return description;
    }


    // ================================================================
    // Utility Methods
    // ================================================================

    // Format the elements in the ArrayList to one single string
    private String stringFormatter(ArrayList<String> strList) {
        String result = "";
        for (String word : strList) {
            result += word + " ";
        }
        return result.trim();
    }
}
