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
 * return 2 arraylist with 2 dates.
 * new DateParser("do homework on 12 mar").getDates() will return arraylist with
 * 1 date.
 * new DateParser("do homework").getDates() will return empty arraylist.
 * 
 * @author Sebastian
 *
 */
public class DateParser {
    private ArrayList<LocalDateTime> dates;

    public DateParser(String input) {
        dates = new ArrayList<LocalDateTime>();

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(input);

        // Natty uses DateGroups and the dates we want must be obtained using
        // getDates().
        for (DateGroup group : groups) {
            List<Date> listOfDates = group.getDates();
            for (Date d : listOfDates) {
                // create new LocalDateTime objects which are added to the dates
                // arraylist.
                dates.add(LocalDateTime.ofInstant(d.toInstant(),
                                                  ZoneId.systemDefault()));
            }
        }
    }

    public ArrayList<LocalDateTime> getDates() {
        return dates;
    }
}
