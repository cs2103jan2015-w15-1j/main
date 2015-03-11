import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;


public class DateParserTest {

    @Test
    public void testInputWithDateAndDuration() {
        String input = "do homework from 4pm to 6pm on 15 mar";
        ArrayList<LocalDateTime> dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
    }

    @Test
    public void testInputWithDateNoDuration() {
        String input = "do homework on 15 mar";
        ArrayList<LocalDateTime> dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
    }

    @Test
    public void testInputWithNoDateNoDuration() {
        String input = "do homework";
        ArrayList<LocalDateTime> dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 0, dates.size());
    }

    @Test
    public void testInputWithDateAndIncorrectDuration() {
        String input = "do homework from 6pm to 2pm on 15 mar";
        ArrayList<LocalDateTime> dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15T18:00", dates.get(0)
                                                                   .toString());

    }
    
    @Test
    public void testInputWithNumbers() {
        String input = "do assignment 2 tomorrow";
        ArrayList<LocalDateTime> dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 1, dates.size());
        
        input = "create 20 word poem";
        dates = new DateParser(input).getDates();
        assertEquals("Number of dates", 0, dates.size());
    }
}
