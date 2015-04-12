package main.java;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

//@author A0121520A
/**
 * <h1>This class helps parse date inputs using Natty.</h1>
 * 
 * <p>Usage:
 * 
 * <p><tt>DateParser dateParser = DateParser.getInstance();</tt>
 * <p><tt>dateParser.parse("do homework from 4pm to 6pm on 15 mar");</tt>
 * <p><tt>ArrayList&lt;LocalDateTime&gt; dates = dateParser.getDates();</tt>
 * <p><tt>String parsedWords = dateParser.getParsedWords();</tt>
 * <p><tt>String notParsedWords = dateParser.getNotParsedWords();</tt>
 */
public class DateParser {
   
    // ================================================================
    // Constants
    // ================================================================
    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_FIRST_DATE_GROUP = 0;
    private static final String FORMAT_PATTERN_TIME_DAY_MONTH = "h.mma, d MMMM";
    private static final String REGEX_PATTERN_DATE_WITH_PERIOD = "\\d+[.]\\d+";
    private static final String ESCAPE_CHAR = "\"";
    private static final String STRING_ONE_SPACING = " ";
    private static final String STRING_EMPTY = "";
    private static final String EXCEPTION_NON_CHRONO_DATES = "%s is not after %s";


    // ================================================================
    // Variables
    // ================================================================
    private static Logger logger;
    private static DateParser dateParser;

    private Parser parser;
    private String rawInput;
    private ArrayList<LocalDateTime> dates;
    private String parsedWords;
    private String notParsedWords;


    // ================================================================
    // Constructor
    // ================================================================
    private DateParser() {
        logger = Logger.getLogger("DateParser");
        logger.setLevel(Level.OFF);
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

    public void parse(String input) throws DateTimeException {
        initInstanceVariables();
        input = modifyInputBeforeParsing(input);
        List<DateGroup> groups = parser.parse(input);

        if (hasParsedDates(groups)) {
            input = fixInputFirstPass(input, groups);
            groups = parser.parse(input);
        }

        if (hasParsedDates(groups)) {
            input = fixInputSecondPass(input, groups);
            groups = parser.parse(input);
        }

        generateInstanceVariables(input, groups);
        removeNonChronologicalDates();
    }

    public String getParsedWords() {
        return parsedWords;
    }

    public String getNotParsedWords() {
        return notParsedWords;
    }

    public ArrayList<LocalDateTime> getDates() {
        assert dates != null;
        return dates;
    }


    // ================================================================
    // Methods used before parsing the input
    // ================================================================
    private void initInstanceVariables() {
        dates = new ArrayList<LocalDateTime>();
        parser = new Parser();
        parsedWords = STRING_EMPTY;
        notParsedWords = STRING_EMPTY;
    }

    /**
     * Returns modified input ready to be parsed.
     * Ensures timings are properly formatted for interpretation by Natty and
     * ensures words encapsulated by the escape characters are not parsed.
     *
     * @param input
     * @return modified input
     */
    private String modifyInputBeforeParsing(String input) {
        input = fixTimeFormatting(input);
        input = hideWordsNotToBeParsed(input);
        logger.log(Level.INFO, "Input before parsing: " + input);
        return input;
    }

    private String fixTimeFormatting(String input) {
        String modifiedInput = replacePeriodsWithColons(input);
        rawInput = modifiedInput;
        return modifiedInput;
    }

    /**
     * Escapes every word that's within two escape characters.
     * 
     * @param input
     * @return modified input
     */
    private String hideWordsNotToBeParsed(String input) {
        boolean isTimeToEscapeWords = false;
        List<String> inputWords = Arrays.asList(input.split(STRING_ONE_SPACING));
        ArrayList<String> splitInput = new ArrayList<String>(inputWords);
        
        int i = 0;
        for (String word : splitInput) {
            if (isSurroundedByEscapeChars(word)) {
                isTimeToEscapeWords = false;
                
            } else if (word.startsWith(ESCAPE_CHAR)) {
                isTimeToEscapeWords = true;
                splitInput.set(i, word + ESCAPE_CHAR);
                
            } else if (word.endsWith(ESCAPE_CHAR)) {
                isTimeToEscapeWords = false;
                splitInput.set(i, ESCAPE_CHAR + word);
                
            } else if (isTimeToEscapeWords) {
                splitInput.set(i, ESCAPE_CHAR + word + ESCAPE_CHAR);
            }
            i++;
        }
        
        return StringUtils.join(splitInput, STRING_ONE_SPACING);
    }

    private String replacePeriodsWithColons(String input) {
        Pattern pattern = Pattern.compile(REGEX_PATTERN_DATE_WITH_PERIOD);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String matchedTime = matcher.group();
            String modifiedTime = matchedTime.replace(".", ":");
            input = input.replace(matchedTime, modifiedTime);
        }
        return input;
    }


    // ================================================================
    // Methods for the first pass of fixing the user's input
    // ================================================================
    private String fixInputFirstPass(String input, List<DateGroup> groups) {
        return escapePartiallyParsedWords(input, groups);
    }

    /**
     * Escapes words that are partially parsed by Natty.
     * E.g. Natty parses "fri" when given "fries".
     * 
     * @param input             User's input.
     * @param groups            Parsed DateGroups.
     * @return modified input
     */
    private String escapePartiallyParsedWords(String input,
                                              List<DateGroup> groups) {
        DateGroup group = groups.get(POSITION_FIRST_DATE_GROUP);
        int parsePosition = group.getPosition();

        List<String> parsedWords = Arrays.asList(group.getText()
                                                      .split(STRING_ONE_SPACING));
        ArrayList<String> splitParsed = new ArrayList<String>(parsedWords);

        List<String> inputWords = Arrays.asList(input.split(STRING_ONE_SPACING));
        ArrayList<String> splitInput = new ArrayList<String>(inputWords);

        // Compare words between the two arrays from the back as dates are
        // typically found towards the back of an input
        Collections.reverse(splitParsed);
        Collections.reverse(splitInput);

        for (String parsedWord : splitParsed) {
            int numChars = 0;
            int parsePositionFromBack = input.length() - parsePosition - 1;

            for (String inputWord : splitInput) {
                numChars += inputWord.length() + 1;

                if (inputWord.contains(parsedWord)) {
                    if (inputWord.equals(parsedWord)) {
                        // inputWord is correctly parsed, so is not partially parsed.
                        splitInput.remove(inputWord);
                        break;
                        
                    } else if (numChars >= parsePositionFromBack) {
                        // if current character num is after the position
                        // of the character that marks the start of words that
                        // were parsed (from the back).
                        int position = getActualIndexOfWord(input,
                                                            parsePosition,
                                                            parsedWord);
                        logger.log(Level.INFO, "Word partially parsed: " +
                                               parsedWord + ", position: " +
                                               position);
                        input = escapeWordAtPosition(input, position);
                    }
                }
            }
        }
        return input;
    }
    

    // ================================================================
    // Methods for the second pass of fixing the user's input
    // ================================================================
    /**
     * Second pass to catch several inaccuracies by Natty's parsing and fix them
     * accordingly.
     * 
     * @param input
     * @param groups
     * @return modified input
     */
    private String fixInputSecondPass(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(POSITION_FIRST_DATE_GROUP);
        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();

        input = catchExplicitTimeFalseMatch(input, pLocations);
        input = catchOptionalPrefix(input, pLocations);
        input = catchHolidays(input, group, pLocations);

        return input;
    }

    /**
     * Catches words that are incorrectly parsed as a time.
     * Uses Natty's explicit_time key to determine the words that were parsed as
     * a time. If these words are shorter than 3 characters, there's a high
     * likelihood that the word isn't meant to be a time.
     * E.g. "2pm" and "1400" are valid but "1", "12" are not.
     * i.e Timings without "am" or "pm" will not be parsed.
     * 
     * @param input
     * @param parsedLocations
     * @return modified input
     */
    private String catchExplicitTimeFalseMatch(String input,
                                               Map<String, List<ParseLocation>> parsedLocations) {
        String offendingKey = "explicit_time";
        if (parsedLocations.containsKey(offendingKey)) {
            for (ParseLocation parsedWord : parsedLocations.get(offendingKey)) {
                if (parsedWord.getText().length() < 3) {
                    logger.log(Level.INFO, "Caught time false match: " +
                                           parsedWord + ", position: " +
                                           parsedWord.getStart());
                    input = escapeWordAtPosition(input, parsedWord.getStart());
                }
            }
        }
        return input;
    }

    /**
     * Catches words that are incorrectly parsed as a date or time when a day is
     * already specified.
     * E.g. "2 friday" & "2 thursday", the "2" will be escaped as it isn't meant
     * to be parsed.
     * 
     * @param input
     * @param parsedLocations
     * @return modified input
     */
    private String catchOptionalPrefix(String input,
                                       Map<String, List<ParseLocation>> parsedLocations) {
        String offendingKey = "spelled_or_int_optional_prefix";
        if (parsedLocations.containsKey(offendingKey)) {
            for (ParseLocation parsedWord : parsedLocations.get(offendingKey)) {
                logger.log(Level.INFO, "Caught option prefix: " + parsedWord +
                                       ", position: " + parsedWord.getStart());
                input = escapeWordAtPosition(input, parsedWord.getStart());
            }
        }
        return input;
    }


    /**
     * Catches words that are incorrectly parsed as holidays.
     * E.g. "find easter eggs" will create a task on easter rather than a
     * floating task.
     * 
     * @param input
     * @param group
     * @param parsedLocations
     * @return modified input
     */
    private String catchHolidays(String input,
                                 DateGroup group,
                                 Map<String, List<ParseLocation>> parsedLocations) {
        String offendingKey = "holiday";
        if (parsedLocations.containsKey(offendingKey)) {
            ParseLocation parsedWords = parsedLocations.get(offendingKey).get(0);
            String[] parsedHolidayWords = parsedWords.getText()
                                                     .split(STRING_ONE_SPACING);
            int startPosition = parsedWords.getStart();

            for (String parsedWord : parsedHolidayWords) {
                int position = getActualIndexOfWord(input,
                                                    startPosition,
                                                    parsedWord);
                input = escapeWordAtPosition(input, position);
            }
        }
        return input;
    }


    // ================================================================
    // Methods to generate the variables for public use
    // ================================================================
    private void generateInstanceVariables(String input, List<DateGroup> groups) {
        DateGroup group = null;

        if (hasParsedDates(groups)) {
            group = groups.get(POSITION_FIRST_DATE_GROUP);
            generateDates(group);
            generateParsedAndNotParsedWords(group);
        } else {
            notParsedWords = rawInput;
        }
    }

    private void generateParsedAndNotParsedWords(DateGroup group) {
        String parsedWordsFromModifiedInput = group.getText();

        String[] splitParsedWords = parsedWordsFromModifiedInput.split(STRING_ONE_SPACING);
        ArrayList<String> splitParsedWordsArr = new ArrayList<String>(Arrays.asList(splitParsedWords));

        String[] splitRawInput = rawInput.split(STRING_ONE_SPACING);
        ArrayList<String> notParsedWordsArr = new ArrayList<String>(Arrays.asList(splitRawInput));

        ArrayList<String> parsedWordsArr = new ArrayList<String>();

        // Compare words between the two arrays from the back as dates are
        // typically found towards the back of an input
        Collections.reverse(splitParsedWordsArr);
        Collections.reverse(notParsedWordsArr);

        for (String parsedWord : splitParsedWordsArr) {
            for (String inputWord : notParsedWordsArr) {
                if (parsedWord.equalsIgnoreCase(inputWord)) {
                    notParsedWordsArr.remove(inputWord);
                    parsedWordsArr.add(inputWord);
                    break;
                }
            }
        }

        // Revert back to their original order
        Collections.reverse(parsedWordsArr);
        Collections.reverse(notParsedWordsArr);

        parsedWords = StringUtils.join(parsedWordsArr, STRING_ONE_SPACING);
        notParsedWords = StringUtils.join(notParsedWordsArr, STRING_ONE_SPACING);

        logger.log(Level.INFO, "Parsed words: " + parsedWords);
        logger.log(Level.INFO, "Not parsed words: " + notParsedWords);
    }

    private void generateDates(DateGroup group) {
        List<Date> listOfDates = group.getDates();
        addNormalDates(listOfDates);
        addRecurringUntilDate(group);

        logger.log(Level.INFO, "Generated dates: " + dates);
    }

    private void addRecurringUntilDate(DateGroup group) {
        if (group.isRecurring() && group.getRecursUntil() != null) {
            dates.add(LocalDateTime.ofInstant(group.getRecursUntil()
                                                   .toInstant(),
                                              ZoneId.systemDefault()));
        }
    }

    private void addNormalDates(List<Date> listOfDates) {
        for (Date d : listOfDates) {
            dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                              ZoneId.systemDefault()));
        }
    }


    // ================================================================
    // Methods to fix irregularities in generated output
    // ================================================================

    /**
     * Ensures the generated dates are in chronological order, if not throw
     * exception.
     * 
     * @throws DateTimeException
     */
    private void removeNonChronologicalDates() throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN_TIME_DAY_MONTH);

        if (!dates.isEmpty()) {
            LocalDateTime referenceDateTime = dates.get(POSITION_FIRST_DATE);

            for (LocalDateTime dateTime : dates) {
                if (dateTime.isBefore(referenceDateTime)) {
                    dates.clear();
                    String dateTimeString = dateTime.format(formatter);
                    String referenceDateTimeString = referenceDateTime.format(formatter);
                    throw new DateTimeException(String.format(EXCEPTION_NON_CHRONO_DATES,
                                                              dateTimeString,
                                                              referenceDateTimeString));
                }
                referenceDateTime = dateTime;
            }
        }

        logger.log(Level.INFO, "After removing non chronological dates: " +
                               dates);
    }


    // ================================================================
    // Utility methods
    // ================================================================
    private boolean hasParsedDates(List<DateGroup> groups) {
        return !groups.isEmpty();
    }
    
    private int getActualIndexOfWord(String input,
                                     int parsePosition,
                                     String parsedWord) {
        int position = parsePosition +
                       input.substring(parsePosition)
                            .indexOf(parsedWord);
        return position;
    }

    private String escapeWordAtPosition(String input, int position) {
        List<String> inputWords = Arrays.asList(input.split(STRING_ONE_SPACING));
        ArrayList<String> splitInput = new ArrayList<String>(inputWords);

        int i = getIndexOfWordInSplitInput(splitInput, input, position);
        assert i != -1;
        String word = splitInput.get(i);

        if (!isSurroundedByEscapeChars(word)) {
            splitInput.set(i, ESCAPE_CHAR + word + ESCAPE_CHAR);
        }

        return StringUtils.join(splitInput, STRING_ONE_SPACING);
    }

    private int getIndexOfWordInSplitInput(ArrayList<String> splitInput,
                                           String input,
                                           int index) {
        assert index >= 0;
        int numChars = 0;
        int currentSplitInputPosition = 0;
        for (String word : splitInput) {
            if (index < numChars + word.length() + 1) {
                return currentSplitInputPosition;
            }
            numChars += word.length() + 1;
            currentSplitInputPosition++;
        }
        return -1;
    }

    private boolean isSurroundedByEscapeChars(String word) {
        return word.startsWith(ESCAPE_CHAR) && word.endsWith(ESCAPE_CHAR);
    }
}
