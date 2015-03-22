package main.java;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;


/**
 * This class helps parse date inputs using Natty.
 * Usage:
 * 
 * DateParser dp = DateParser.getInstance();
 * dp.parse("do homework from 4pm to 6pm on 15 mar");
 * ArrayList<LocalDateTime> dates = dp.getDates();
 * String parsedWords = dp.getParsedWords;
 */

//@author A0121520A
public class DateParser {
    private static final Logger logger = Logger.getLogger("Veto");

    private static DateParser dateParser;

    private static final char ESCAPE_CHAR = '"';
    private static final int POSITION_FIRST_DATE = 0;
    private static final String OFFENDING_NATTY_KEY = "hours";

    private ArrayList<LocalDateTime> dates;
    private String parsedWords; // words used when determining the date/s
    private Parser parser;
    private boolean isIncorrectlyParsingWords;
    private String incorrectlyParsedWord;

    private DateParser() {
        logger.setLevel(Level.INFO);
    }

    
    // ================================================================
    // Public methods
    // ================================================================

    public static DateParser getInstance() {
        if (dateParser == null) {
            dateParser = new DateParser();
        }
        return dateParser;
    }

    public void parse(String input) {
        initVariables();
        logger.log(Level.INFO, "Input: " + input);
        input = getWordsOutsideEscapeChars(input);

        List<DateGroup> groups = parser.parse(input);

        findErrorCausingWords(groups);

        if (isIncorrectlyParsingWords) {
            input = modifyInput(input);
            parse(input);
        } else {
            generateDatesAndParsedWords(groups);
            removeNonChronologicalDates();
        }
    }

    public String getParsedWords() {
        return parsedWords;
    }

    public ArrayList<LocalDateTime> getDates() {
        return dates;
    }

    
    // ================================================================
    // Methods to initialise and generate variables
    // ================================================================

    private void initVariables() {
        dates = new ArrayList<LocalDateTime>();
        parsedWords = "";
        parser = new Parser();
        isIncorrectlyParsingWords = false;
        incorrectlyParsedWord = "";
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
        logger.log(Level.INFO, "New input: " + output);
        return output;
    }

    private void generateDatesAndParsedWords(List<DateGroup> groups) {
        for (DateGroup group : groups) {

            parsedWords = group.getText();
            logger.log(Level.INFO, "Parsed words: " + parsedWords);
            List<Date> listOfDates = group.getDates();
            for (Date d : listOfDates) {
                // create new LocalDateTime objects
                dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                                  ZoneId.systemDefault()));
            }
            logger.log(Level.INFO, "Generated dates: " + dates);
        }
    }
    
    
    // ================================================================
    // Methods to fix irregularities in user input
    // ================================================================

    private void findErrorCausingWords(List<DateGroup> groups) {
        for (DateGroup group : groups) {

            // Natty sometimes incorrectly uses words in the input. When numbers
            // exist in the input, such as "create 20 word poem", Natty takes
            // the numbers thinking it's part of a date.

            if (hasErrorCausingWord(group)) {
                isIncorrectlyParsingWords = true;
                incorrectlyParsedWord = getIncorrectlyParsedWord(group);
                logger.log(Level.INFO, "Offending word: {0}",
                           incorrectlyParsedWord);
                break;
            }
        }
    }

    private boolean hasErrorCausingWord(DateGroup group) {

        // Natty generates several fields and if the "hour" field appears,
        // it should have 2 elements to indicate a start and end time. The
        // presense of only 1 element indicates an error.

        return (group.getParseLocations().containsKey(OFFENDING_NATTY_KEY) && group.getParseLocations()
                                                                                   .get(OFFENDING_NATTY_KEY)
                                                                                   .size() == 1);
    }

    private String getIncorrectlyParsedWord(DateGroup group) {
        return group.getParseLocations()
                    .get(OFFENDING_NATTY_KEY)
                    .get(0)
                    .toString();
    }

    private String modifyInput(String input) {
        // add the escape character before and after the incorrectly used word
        // so that it's ignored by Natty.
        String modifiedWord = ESCAPE_CHAR + incorrectlyParsedWord + ESCAPE_CHAR;
        String newInput = input.replaceFirst(incorrectlyParsedWord,
                                             modifiedWord);
        logger.log(Level.INFO, "Modified input: {0}", newInput);
        return newInput;
    }
    
    
    // ================================================================
    // Methods to fix irregularities in generated output
    // ================================================================
    
    // ensures the dates are in chronological order
    private void removeNonChronologicalDates() {
        ArrayList<LocalDateTime> editedDates = new ArrayList<LocalDateTime>();

        if (!dates.isEmpty()) {
            LocalDateTime referenceDateTime = dates.get(POSITION_FIRST_DATE);
            editedDates.add(referenceDateTime);

            for (LocalDateTime dateTime : dates) {
                if (dateTime.equals(referenceDateTime)) {
                    continue;
                }
                if (dateTime.isAfter(referenceDateTime)) {
                    editedDates.add(dateTime);
                    referenceDateTime = dateTime;
                }
            }
        }

        dates = editedDates;
        logger.log(Level.INFO, "Removed non chronological dates: " + dates);
    }
}
