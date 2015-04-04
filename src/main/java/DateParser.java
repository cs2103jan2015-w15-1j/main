package main.java;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
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
    private static Logger logger;

    private static DateParser dateParser;

    private static final char ESCAPE_CHAR = '"';
    private static final int POSITION_FIRST_DATE = 0;
    private static final String OFFENDING_NATTY_KEY = "hours";

    private ArrayList<LocalDateTime> dates;
    private String parsedWords; // words used when determining the date/s
    private String nonParsedWords;
    private Parser parser;
    private boolean isIncorrectlyParsingWords;
    private String incorrectlyParsedWord;

    private String rawInput;

    private DateParser() {
        logger = Logger.getLogger("Veto");
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
        initVariables(input);
        logger.log(Level.INFO, "Input: " + input);
//        input = getWordsOutsideEscapeChars(input);

        List<DateGroup> groups = parser.parse(input);
        
        if (!groups.isEmpty()) {
            input = fixInput1(input, groups);
            groups = parser.parse(input);
        }
        
        if (!groups.isEmpty()) {
            input = fixInput(input, groups);
            groups = parser.parse(input);
        }
        
        generateInstanceVariables(input, groups);
      removeNonChronologicalDates();
//
//        for (DateGroup group : groups) {
//            String substring = input.substring(group.getPosition(),
//                                               input.length());
//            substring = substring.replace('.', ':');
//            input = input.substring(0, group.getPosition()) + substring;
//        }
//        groups = parser.parse(input);

//        findErrorCausingWords(groups);

//        if (isIncorrectlyParsingWords) {
//            input = modifyInput(input);
//            parse(input);
//        } else {
//            generateInstanceVariables(input, groups);
//            removeNonChronologicalDates();
//        }
    }

    public String getParsedWords() {
        return parsedWords;
    }

    public String getNonParsedWords() {
        return nonParsedWords;
    }

    public ArrayList<LocalDateTime> getDates() {
        return dates;
    }


    // ================================================================
    // Methods to initialise and generate variables
    // ================================================================

    private void initVariables(String input) {
        rawInput = input;
        dates = new ArrayList<LocalDateTime>();
        parsedWords = "";
        nonParsedWords = "";
        parser = new Parser();
//        isIncorrectlyParsingWords = false;
//        incorrectlyParsedWord = "";
    }

    
    private String fixInput1(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(0);
        int parsePosition = group.getPosition();
        ArrayList<String> splitParsed = new ArrayList<String>(Arrays.asList(group.getText().split(" ")));
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        for (String parsedWord : splitParsed) {
            int c = 0;
            for (String inputWord : splitInput) {
                c += inputWord.length() + 1;
                if (inputWord.contains(parsedWord)) {
                    if (inputWord.equals(parsedWord)) {
                        break;
                    } else if (c >= parsePosition) {
                        int position = parsePosition + input.substring(parsePosition).indexOf(parsedWord);
                        System.out.println("ERROR5: " + input + ", word: " + parsedWord + ", position: " +
                                position);
                        input = escapeWordAtPosition(input, position);
                    }                    
                }
            }
        }
        return input;
    }
    
    private String fixInput(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(0);
        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();
        int parsePosition = group.getPosition();
        System.out.println("INPUT: " + input);
        System.out.println(pLocations);
        
        
        if (pLocations.containsKey("explicit_time")) {
            for (ParseLocation s : pLocations.get("explicit_time")) {
                if (s.getText().length() < 3) {
                    System.out.println("ERROR1: " + input + ", word: " + s + ", position: " + s.getStart());
                    input = escapeWordAtPosition(input, s.getStart());
                }
            }
        }
        
        if (pLocations.containsKey("spelled_or_int_optional_prefix")) {
            for (ParseLocation s : pLocations.get("spelled_or_int_optional_prefix")) {
                System.out.println("ERROR2: " + input + ", word: " + s + ", position: " +
                                   s.getStart());
                input = escapeWordAtPosition(input, s.getStart());
            }
        }
        
        if (pLocations.containsKey("relaxed_year")) {
            for (ParseLocation s : pLocations.get("relaxed_year")) {
                if (Year.parse(s.getText()).isBefore(Year.now())) {
                    System.out.println("ERROR3: " + input + ", word: " + s + ", position: " +
                            s.getStart());
                    input = addWordsBeforeWordAtPosition(input, s.getStart(), Year.now().toString() + " ");
                }
            }
        }
        
        if (pLocations.containsKey("explicit_time") && !pLocations.containsKey("date")) {
            if (pLocations.get("explicit_time").size() == 1) {
                String parsedWord = pLocations.get("explicit_time").get(0).getText();
                int position = parsePosition + input.substring(parsePosition).indexOf(parsedWord);
                System.out.println("ERROR4: " + input + ", word: " + parsedWord + ", position: " +
                                position);
                input = addWordsBeforeWordAtPosition(input, position, "1/1/");
//                input = addWordsBeforePosition(input, position, LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth() + "/");
//                input += " " +  LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth();
            }
        }

        return input;
    }
    
    private String addWordsBeforeWordAtPosition(String input, int position, String words) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        
        int i = getIndexOfWordInSplitInput(splitInput, input, position);
        String word = splitInput.get(i);
        
        splitInput.set(i, words + word);
        
        return StringUtils.join(splitInput, ' ');
    }

    private String escapeWordAtPosition(String input, int position) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        
        int i = getIndexOfWordInSplitInput(splitInput, input, position);
        String word = splitInput.get(i);
        
        if (!isSurroundedByEscapeChars(word)) {
            splitInput.set(i, "\"" + word + "\"");
        }
        
        return StringUtils.join(splitInput, ' ');
    }

    private boolean isSurroundedByEscapeChars(String word) {
        return word.startsWith("\"") && word.endsWith("\"");
    }
    
    private int getIndexOfWordInSplitInput(ArrayList<String> splitInput,
                                                    String input,
                                                    int index) {
        String word = getWordAtIndex(splitInput, index);
        return splitInput.indexOf(word);
    }

    private String getWordAtIndex(ArrayList<String> splitInput, int index) {
        int c = 0;
        for (String word : splitInput) {
            if (index >= c && index < c + word.length() + 1) {
                return word;
            }
            c += word.length() + 1;
        }
        return null;
    }
    
    private String getWordsOutsideEscapeChars(String input) {
        if (StringUtils.countMatches(input, ESCAPE_CHAR + "") == 2) {
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
        return input;
    }

    private void generateInstanceVariables(String input, List<DateGroup> groups) {
        for (DateGroup group : groups) {
            parsedWords = group.getText();
            logger.log(Level.INFO, "Parsed words: " + parsedWords);
            List<Date> listOfDates = group.getDates();
            for (Date d : listOfDates) {
                // create new LocalDateTime objects
                dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                                  ZoneId.systemDefault()));
            }
            if (group.isRecurring()) {
                dates.add(LocalDateTime.ofInstant(group.getRecursUntil().toInstant(), ZoneId.systemDefault()));
            }
            logger.log(Level.INFO, "Generated dates: " + dates);
        }

        // convert input arguments to string arrays
        String[] parsedWordsArr = parsedWords.split(" ");
        String[] inputArr = rawInput.split(" ");

        // convert input string array to arraylist of strings
        ArrayList<String> inputArrayList = new ArrayList<String>(Arrays.asList(inputArr));

        // reverse as we want to delete words from the back
        Collections.reverse(inputArrayList);

        // delete words that were used to obtain the dates
        for (String word : parsedWordsArr) {
            inputArrayList.remove(word);
        }

        Collections.reverse(inputArrayList);

        nonParsedWords = stringFormatter(inputArrayList);
    }


    // ================================================================
    // Methods to fix irregularities in user input
    // ================================================================

    private void findErrorCausingWords(List<DateGroup> groups) {
        for (DateGroup group : groups) {

            // Natty sometimes incorrectly uses words in the input. When numbers
            // exist in the input, such as "create 20 word poem", Natty takes
            // the numbers thinking it's part of a date.

            incorrectlyParsedWord = getIncorrectlyParsedWord(group);
            if (incorrectlyParsedWord != null) {
                isIncorrectlyParsingWords = true;
                logger.log(Level.INFO, "Offending word: {0}",
                           incorrectlyParsedWord);
                break;
            }
        }
    }

    private String getIncorrectlyParsedWord(DateGroup group) {

        // Natty generates several fields and if the "hour" field appears,
        // it should have 2 elements to indicate a start and end time. The
        // presense of only 1 element indicates an error.

        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();
        logger.log(Level.INFO, "Parse locations: " + pLocations);
        if (pLocations.containsKey(OFFENDING_NATTY_KEY) &&
            pLocations.get(OFFENDING_NATTY_KEY).size() == 1) {
            return pLocations.get(OFFENDING_NATTY_KEY).get(0).toString();
        } else if (pLocations.containsKey("date") &&
                   !pLocations.containsKey("relaxed_month")) {
            ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(rawInput.split(" ")));
            for (ParseLocation day : pLocations.get("date")) {
                if (!splitInput.contains(day.toString())) {
                    return day.toString();
                }
            }
        }
        return null;
    }

    //    private String getIncorrectlyParsedWord(DateGroup group) {
    //        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();
    //        
    //        if (pLocations.containsKey(OFFENDING_NATTY_KEY) && pLocations.get(OFFENDING_NATTY_KEY).size() == 1) {
    //            return group.getParseLocations()
    //                        .get(OFFENDING_NATTY_KEY)
    //                        .get(0)
    //                        .toString();
    //        } else {
    //            
    //        }
    //    }

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


    // ================================================================
    // Utility methods
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
