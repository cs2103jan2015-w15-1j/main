import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;


/**
 * This class helps parse date inputs using Natty.
 * new DateParser("do homework from 1600 - 1700 on 12 mar").getDates() will
 * return an arraylist with 2 dates.
 * 
 * new DateParser("do homework on 12 mar").getDates() will return an arraylist with
 * 1 date.
 * 
 * new DateParser("do homework").getDates() will return an empty arraylist.
 * 
 * @author Sebastian
 *
 */
public class DateParser {
    private static final int POSITION_FIRST_DATE = 0;
    private static final int POSITION_SECOND_DATE = 1;
    
    private ArrayList<LocalDateTime> dates;
    private String parsedWords; // words used when determining the date/s

    public DateParser(String input) {
        dates = new ArrayList<LocalDateTime>();

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(input);

        // Natty uses DateGroups and the dates we want must be obtained using
        // getDates().
        for (DateGroup group : groups) {
            parsedWords = group.getText();
            List<Date> listOfDates = group.getDates();
            for (Date d : listOfDates) {
                // create new LocalDateTime objects which are added to the dates
                // arraylist.
                dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                                  ZoneId.systemDefault()));
            }
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
