package tests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.DateParser;

import org.junit.Test;


public class DateParserTest {

    private ArrayList<LocalDateTime> getParsedDates(String input) {
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        ArrayList<LocalDateTime> dates = dateParser.getDates();
        return dates;
    }
    
    @Test
    public void inputWithNoDateNoDuration() {
        String input = "do homework";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
    }

    @Test
    public void inputWithDateNoDuration() {
        String input = "15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
        
        input = "mar 15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
        
        input = "15 march";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
        
        input = "15 march 2018";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2018-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
    }
    
    @Test
    public void inputWithDateAndTime() {
        String input = "15 mar 6pm";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time", "2015-03-15T18:00", dates.get(0)
                                                                   .toString());
    }
    
    @Test
    public void inputWithDateAndDuration() {
        String input = "4pm to 6pm on 15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
        
        input = "15 mar 4pm to 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
        
        input = "15 mar 4.30pm to 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:30", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
        
        input = "15 mar 4.30pm to 6.30pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:30", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:30", dates.get(1)
                                                                 .toString());
        
        input = "15 mar 4pm - 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
        
        input = "1600 - 1800 on 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
        
        input = "4pm to 6:30pm on 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:30", dates.get(1)
                                                                 .toString());
    }

    @Test
    public void inputWithDateAndNonChronologicalTime() {
        String input = "6pm to 2pm on 15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15T18:00", dates.get(0)
                                                                   .toString());
    }
    


    @Test
    public void inputWithNumbers() {
        String input;
        ArrayList<LocalDateTime> dates;
        
        input = "do assignment 2 tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline", LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());
        
        input = "do CS1231 tutorial by tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline", LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());

        input = "create 20 word poem";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());

        input = "add finish SR for assignment 2 from 12pm to 6pm today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", LocalDate.now().atTime(12, 0),
                     dates.get(0));
        assertEquals("End date & time", LocalDate.now().atTime(18, 0),
                     dates.get(1));

        input = "add attend meeting 23 march 1200 - 1400";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Deadline", "2015-03-23", dates.get(0)
                                                    .toLocalDate()
                                                    .toString());
        assertEquals("Start Time", "12:00", dates.get(0)
                                                 .toLocalTime()
                                                 .toString());
        assertEquals("End Time", "14:00", dates.get(1).toLocalTime().toString());
    }
    
    @Test
    public void inputWithFalseMatchingWords() {
        String input;
        ArrayList<LocalDateTime> dates;
        
        input = "fries";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
    }
    
    @Test
    public void inputWithRecurringDate() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "watch tv every monday 8pm to 9pm until 6 jun";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 3, dates.size());
        assertEquals("Start date & time", LocalDate.of(2015, 4, 6)
                                                   .atTime(20, 0), dates.get(0));
        assertEquals("End date & time", LocalDate.of(2015, 4, 6).atTime(21, 0),
                     dates.get(1));
        assertEquals("Recur until date", LocalDate.of(2015, 6, 6),
                     dates.get(2).toLocalDate());
    }

}
