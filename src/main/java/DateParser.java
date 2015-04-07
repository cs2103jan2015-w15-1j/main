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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    // ================================================================
    // Constants
    // ================================================================
    private static final String REGEX_PATTERN_DATE_WITH_PERIOD = "\\d+[.]\\d+";
    private static final String ESCAPE_CHAR = "\"";
    private static final String ONE_SPACING = " ";
    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_FIRST_DATE_GROUP = 0;


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

    public void parse(String input) {
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
        return dates;
    }


    // ================================================================
    // Methods used before parsing the input
    // ================================================================
    private void initInstanceVariables() {
        dates = new ArrayList<LocalDateTime>();
        parser = new Parser();
    }

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

    private String hideWordsNotToBeParsed(String input) {
        boolean isTimeToEscapeWords = false;
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(ONE_SPACING)));
        int i = 0;
        for (String word : splitInput) {
            if (isSurroundedByEscapeChars(word)) {
                isTimeToEscapeWords = false;
                break;
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
        return StringUtils.join(splitInput, ONE_SPACING);
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
     * @param input
     * @param groups
     * @return
     */
    private String escapePartiallyParsedWords(String input,
                                              List<DateGroup> groups) {
        DateGroup group = groups.get(POSITION_FIRST_DATE_GROUP);
        int parsePosition = group.getPosition();

        ArrayList<String> splitParsed = new ArrayList<String>(Arrays.asList(group.getText()
                                                                                 .split(" ")));
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        Collections.reverse(splitInput);
        Collections.reverse(splitParsed);

        for (String parsedWord : splitParsed) {
            int c = 0;

            for (String inputWord : splitInput) {
                c += inputWord.length() + 1;

                if (inputWord.contains(parsedWord)) {
                    if (inputWord.equals(parsedWord)) {
                        splitInput.remove(inputWord);
                        break;
                    } else if (c >= parsePosition) {
                        int position = parsePosition +
                                       input.substring(parsePosition)
                                            .indexOf(parsedWord);
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
     * @return
     */
    private String fixInputSecondPass(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(POSITION_FIRST_DATE_GROUP);
        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();

        input = catchExplicitTimeFalseMatch(input, pLocations);
        input = catchOptionalPrefix(input, pLocations);
        input = catchRelaxedYearBeforeToday(input, pLocations);
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
     * @return
     */
    private String catchExplicitTimeFalseMatch(String input,
                                               Map<String, List<ParseLocation>> parsedLocations) {
        if (parsedLocations.containsKey("explicit_time")) {
            for (ParseLocation parsedWord : parsedLocations.get("explicit_time")) {
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
     * @return
     */
    private String catchOptionalPrefix(String input,
                                       Map<String, List<ParseLocation>> parsedLocations) {
        if (parsedLocations.containsKey("spelled_or_int_optional_prefix")) {
            for (ParseLocation parsedWord : parsedLocations.get("spelled_or_int_optional_prefix")) {
                logger.log(Level.INFO, "Caught option prefix: " + parsedWord +
                                       ", position: " + parsedWord.getStart());
                input = escapeWordAtPosition(input, parsedWord.getStart());
            }
        }
        return input;
    }

    /**
     * Catches the parsing of years that are before this year.
     * E.g. "1231" & "1521" will not be parsed.
     * 
     * @param input
     * @param parsedLocations
     * @return
     */
    private String catchRelaxedYearBeforeToday(String input,
                                               Map<String, List<ParseLocation>> parsedLocations) {
        if (parsedLocations.containsKey("relaxed_year")) {
            for (ParseLocation parsedWord : parsedLocations.get("relaxed_year")) {
                if (Year.parse(parsedWord.getText()).isBefore(Year.now())) {
                    logger.log(Level.INFO, "Caught parsing year wrongly: " +
                                           parsedWord + ", position: " +
                                           parsedWord.getStart());
                    input = addYearToInput(input, parsedWord);
                }
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
     * @return
     */
    private String catchHolidays(String input,
                                 DateGroup group,
                                 Map<String, List<ParseLocation>> parsedLocations) {
        if (parsedLocations.containsKey("holiday")) {
            ParseLocation parsedWords = parsedLocations.get("holiday").get(0);
            String[] parsedHolidayWords = parsedWords.getText()
                                                     .split(ONE_SPACING);
            int startPosition = parsedWords.getStart();

            for (String parsedWord : parsedHolidayWords) {
                int position = input.substring(startPosition)
                                    .indexOf(parsedWord) + startPosition;
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
        }

        generateParsedAndNotParsedWords(group);
    }

    private void generateParsedAndNotParsedWords(DateGroup group) {
        if (group != null) {
            String parsedWordsFromModifiedInput = group.getText();

            String[] splitParsedWords = parsedWordsFromModifiedInput.split(" ");
            String[] splitRawInput = rawInput.split(" ");
            ArrayList<String> splitParsedWordsArr = new ArrayList<String>(Arrays.asList(splitParsedWords));
            ArrayList<String> parsedWordsArr = new ArrayList<String>();
            ArrayList<String> notParsedWordsArr = new ArrayList<String>(Arrays.asList(splitRawInput));

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

            Collections.reverse(parsedWordsArr);
            Collections.reverse(notParsedWordsArr);
            parsedWords = StringUtils.join(parsedWordsArr, ' ');
            notParsedWords = StringUtils.join(notParsedWordsArr, ' ');
        } else {
            notParsedWords = rawInput;
        }
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

    // ensures the dates are in chronological order
    private void removeNonChronologicalDates() {
        ArrayList<LocalDateTime> editedDates = new ArrayList<LocalDateTime>();

        if (!dates.isEmpty()) {
            LocalDateTime referenceDateTime = dates.get(POSITION_FIRST_DATE);
            editedDates.add(referenceDateTime);

            for (LocalDateTime dateTime : dates) {
                if (dateTime.isAfter(referenceDateTime)) {
                    editedDates.add(dateTime);
                    referenceDateTime = dateTime;
                }
            }
        }

        dates = editedDates;
        logger.log(Level.INFO, "After removing non chronological dates: " +
                               dates);
    }


    // ================================================================
    // Utility methods
    // ================================================================
    private boolean hasParsedDates(List<DateGroup> groups) {
        return !groups.isEmpty();
    }

    private String addYearToInput(String input, ParseLocation parsedWord) {
        input = addWordsBeforeWordAtPosition(input,
                                             parsedWord.getStart(),
                                             Year.now().toString() +
                                                     ONE_SPACING);
        return input;
    }

    private String addWordsBeforeWordAtPosition(String input,
                                                int position,
                                                String words) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(ONE_SPACING)));
        modifyWordAtPosition(input, position, words, splitInput);
        return StringUtils.join(splitInput, ONE_SPACING);
    }


    private void modifyWordAtPosition(String input,
                                      int position,
                                      String words,
                                      ArrayList<String> splitInput) {
        int i = getIndexOfWordInSplitInput(splitInput, input, position);
        String word = splitInput.get(i);

        splitInput.set(i, words + word);
    }

    private String escapeWordAtPosition(String input, int position) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(ONE_SPACING)));

        int i = getIndexOfWordInSplitInput(splitInput, input, position);
        String word = splitInput.get(i);

        if (!isSurroundedByEscapeChars(word)) {
            splitInput.set(i, ESCAPE_CHAR + word + ESCAPE_CHAR);
        }

        return StringUtils.join(splitInput, ONE_SPACING);
    }

    private int getIndexOfWordInSplitInput(ArrayList<String> splitInput,
                                           String input,
                                           int index) {
        int currentInputPosition = 0;
        int currentSplitInputPosition = 0;
        for (String word : splitInput) {
            if (index >= currentInputPosition &&
                index < currentInputPosition + word.length() + 1) {
                return currentSplitInputPosition;
            }
            currentInputPosition += word.length() + 1;
            currentSplitInputPosition++;
        }
        return -1;
    }

    private boolean isSurroundedByEscapeChars(String word) {
        return word.startsWith(ESCAPE_CHAR) && word.endsWith(ESCAPE_CHAR);
    }
}
