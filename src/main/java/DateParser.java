package main.java;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.commons.lang.StringUtils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;


/**
 * This class helps parse date inputs using Natty.
 * new main.java.DateParser("do homework from 1600 - 1700 on 12 mar").getDates() will
 * return an arraylist with 2 dates.
 *
 * new main.java.DateParser("do homework on 12 mar").getDates() will return an arraylist
 * with
 * 1 date.
 *
 * new main.java.DateParser("do homework").getDates() will return an empty arraylist.
 *
 * @author Sebastian
 *
 */
public class DateParser {
    private static final Logger logger = Logger.getLogger("Veto");

    private static final char ESCAPE_CHAR = '"';
    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_SECOND_DATE = 1;

    private static final String OFFENDING_NATTY_KEY = "hours";

    private ArrayList<LocalDateTime> dates;
    private String parsedWords; // words used when determining the date/s

    public DateParser(String input) {
        logger.setLevel(Level.INFO);
        dates = new ArrayList<LocalDateTime>();

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(input);

        boolean isIncorrectlyParsingWords = false;
        String incorrectlyParsedWord = "";

        // Natty uses DateGroups and the dates we want must be obtained using
        // getDates().
        for (DateGroup group : groups) {
            // Natty sometimes incorrectly uses words in the input. When numbers
            // exist in the input, such as "create 20 word poem", Natty takes
            // the numbers thinking it's part of a date.

            // Natty generates several fields and if the "hour" field appears,
            // it should have 2 elements to indicate a start and end time.
            if (group.getParseLocations().containsKey(OFFENDING_NATTY_KEY) &&
                group.getParseLocations().get(OFFENDING_NATTY_KEY).size() == 1) {

                isIncorrectlyParsingWords = true;
                incorrectlyParsedWord = group.getParseLocations()
                                             .get(OFFENDING_NATTY_KEY)
                                             .get(0)
                                             .toString();
                logger.log(Level.INFO, "Offending word: {0}, Input string: {1}", new Object[] {incorrectlyParsedWord, input});
                break;
            }

            parsedWords = group.getText();
            List<Date> listOfDates = group.getDates();
            for (Date d : listOfDates) {
                // create new LocalDateTime objects which are added to the dates
                // arraylist.
                dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                                  ZoneId.systemDefault()));
            }
        }

        if (isIncorrectlyParsingWords) {
            // if the constructor has been called 2 times, stop recursing.
            if (StringUtils.countMatches(input, ESCAPE_CHAR + "") > 2) {
                return;
            }

            // add a " before and after the incorrectly used word so that it's
            // ignored by Natty.
            String modifiedWord = ESCAPE_CHAR + incorrectlyParsedWord + ESCAPE_CHAR;
            String newInput = input.replaceFirst(incorrectlyParsedWord,
                                                 modifiedWord);
            logger.log(Level.INFO, "Modified input: {0}", newInput);
            DateParser fixed = new DateParser(newInput);
            dates = fixed.getDates();
            parsedWords = fixed.getParsedWords();
        }

        fixIncorrectDates();
    }

    // ensures the dates are in chronological order and on the same day
    private void fixIncorrectDates() {
        if (dates.size() == 2) {
            LocalDateTime firstDateTime = dates.get(POSITION_FIRST_DATE);
            LocalDateTime secondDateTime = dates.get(POSITION_SECOND_DATE);
            if (firstDateTime.isAfter(secondDateTime)) {
                dates.remove(POSITION_SECOND_DATE);
            }
        }
    }

    public String getParsedWords() {
        return parsedWords;
    }

    public ArrayList<LocalDateTime> getDates() {
        return dates;
    }
}
