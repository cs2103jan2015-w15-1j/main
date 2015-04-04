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
    private static final String REGEX_PATTERN_DATE_WITH_PERIOD = "\\d+[.]\\d+";

    private static final String DEFAULT_MONTH_AND_DAY = "1/1/";

    private static final int POSITION_FIRST_DATE_GROUP = 0;

    private static Logger logger;

    private static DateParser dateParser;

    private static final String ESCAPE_CHAR = "\"";
    private static final int POSITION_FIRST_DATE = 0;
    //    private static final String OFFENDING_NATTY_KEY = "hours";

    private ArrayList<LocalDateTime> dates;
    private String parsedWords; // words used when determining the date/s
    private String notParsedWords;
    private Parser parser;
    //    private boolean isIncorrectlyParsingWords;
    //    private String incorrectlyParsedWord;

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

        input = fixTimeFormatting(input);
        rawInput = input;
        logger.log(Level.INFO, "Input: " + input);
        //        input = getWordsOutsideEscapeChars(input);

        List<DateGroup> groups = parser.parse(input);

        if (!groups.isEmpty()) {
            input = fixInputFirstPass(input, groups);
            groups = parser.parse(input);
        }

        if (!groups.isEmpty()) {
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
    // Methods to initialise and generate variables
    // ================================================================

    private void initVariables(String input) {
        dates = new ArrayList<LocalDateTime>();
        parser = new Parser();
        //        isIncorrectlyParsingWords = false;
        //        incorrectlyParsedWord = "";
    }


    /**
     * First pass to fix the given user input.
     * 
     * @param input
     * @param groups
     * @return
     */
    private String fixInputFirstPass(String input, List<DateGroup> groups) {
        return escapePartiallyParsedWords(input, groups);
    }

    private String fixTimeFormatting(String input) {
        return replacePeriodsWithColons(input);
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

        for (String parsedWord : splitParsed) {
            int c = 0;

            for (String inputWord : splitInput) {
                c += inputWord.length() + 1;

                if (inputWord.contains(parsedWord)) {
                    if (inputWord.equals(parsedWord)) {
                        break;
                    } else if (c >= parsePosition) {
                        int position = parsePosition +
                                       input.substring(parsePosition)
                                            .indexOf(parsedWord);
                        System.out.println("ERROR5: " + input + ", word: " +
                                           parsedWord + ", position: " +
                                           position);
                        input = escapeWordAtPosition(input, position);
                    }
                }
            }
        }
        return input;
    }

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
        int parsePosition = group.getPosition();

        input = catchExplicitTimeFalseMatch(input, pLocations);

        input = catchOptionalPrefix(input, pLocations);

        input = catchRelaxedYearBeforeToday(input, pLocations);

        input = catchExplicitTimeWithoutDate(input, pLocations, parsePosition);

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
                    System.out.println("ERROR1: " + input + ", word: " +
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
                System.out.println("ERROR2: " + input + ", word: " +
                                   parsedWord + ", position: " +
                                   parsedWord.getStart());
                input = escapeWordAtPosition(input, parsedWord.getStart());
            }
        }
        return input;
    }

    /**
     * Catches the parsing of years that are before this year.
     * E.g. "1980" & "1521" will not be parsed.
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
                    System.out.println("ERROR3: " + input + ", word: " +
                                       parsedWord + ", position: " +
                                       parsedWord.getStart());
                    input = addWordsBeforeWordAtPosition(input,
                                                         parsedWord.getStart(),
                                                         Year.now().toString() +
                                                                 " ");
                }
            }
        }
        return input;
    }

    /**
     * Catches words that are parsed as a timing when it should be parsed as a
     * year.
     * A time with no date is taken as a year.
     * E.g. "2016" is incorrectly parsed as 8:16pm, but should be the year 2016.
     * 
     * @param input
     * @param parsedLocations
     * @param parsePosition
     * @return
     */
    private String catchExplicitTimeWithoutDate(String input,
                                                Map<String, List<ParseLocation>> parsedLocations,
                                                int parsePosition) {
        if (parsedLocations.containsKey("explicit_time") &&
            !parsedLocations.containsKey("date")) {
            if (parsedLocations.get("explicit_time").size() == 1) {
                String parsedWord = parsedLocations.get("explicit_time")
                                                   .get(0)
                                                   .getText();
                int position = parsePosition +
                               input.substring(parsePosition)
                                    .indexOf(parsedWord);
                System.out.println("ERROR4: " + input + ", word: " +
                                   parsedWord + ", position: " + position);
                input = addWordsBeforeWordAtPosition(input,
                                                     position,
                                                     DEFAULT_MONTH_AND_DAY);
            }
        }
        return input;
    }

    private String addWordsBeforeWordAtPosition(String input,
                                                int position,
                                                String words) {
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
            splitInput.set(i, ESCAPE_CHAR + word + ESCAPE_CHAR);
        }

        return StringUtils.join(splitInput, ' ');
    }

    private boolean isSurroundedByEscapeChars(String word) {
        return word.startsWith(ESCAPE_CHAR) && word.endsWith(ESCAPE_CHAR);
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

    private void generateInstanceVariables(String input, List<DateGroup> groups) {
        DateGroup group = null;
        if (!groups.isEmpty()) {
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
                    if (parsedWord.contains(inputWord)) {
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
        for (Date d : listOfDates) {
            dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                              ZoneId.systemDefault()));
        }

        if (group.isRecurring() && group.getRecursUntil() != null) {
            dates.add(LocalDateTime.ofInstant(group.getRecursUntil()
                                                   .toInstant(),
                                              ZoneId.systemDefault()));

        }
        logger.log(Level.INFO, "Generated dates: " + dates);
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

}
