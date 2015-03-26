package tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.DateParser;

import org.junit.Test;


public class DateParserTest {

    @Test
    public void inputWithDateAndDuration() {
        String input = "do homework from 4pm to 6pm on 15 mar";
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        ArrayList<LocalDateTime> dates = dateParser.getDates();
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time", "2015-03-15T16:00", dates.get(0)
                                                                   .toString());
        assertEquals("End date & time", "2015-03-15T18:00", dates.get(1)
                                                                 .toString());
    }

    @Test
    public void inputWithDateNoDuration() {
        String input = "do homework on 15 mar";
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        ArrayList<LocalDateTime> dates = dateParser.getDates();
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15", dates.get(0)
                                                             .toLocalDate()
                                                             .toString());
    }

    @Test
    public void inputWithNoDateNoDuration() {
        String input = "do homework";
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        ArrayList<LocalDateTime> dates = dateParser.getDates();
        assertEquals("Number of dates", 0, dates.size());
    }

    @Test
    public void inputWithDateAndIncorrectDuration() {
        String input = "do homework from 6pm to 2pm on 15 mar";
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        ArrayList<LocalDateTime> dates = dateParser.getDates();
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time", "2015-03-15T18:00", dates.get(0)
                                                                   .toString());

    }

    @Test
    public void inputWithNumbers() {
        String input;
        DateParser dateParser = DateParser.getInstance();
        ArrayList<LocalDateTime> dates;
        
        input = "do assignment 2 tomorrow";
        dateParser.parse(input);
        dates = dateParser.getDates();
        assertEquals("Number of dates", 1, dates.size());

        input = "create 20 word poem";
        dateParser.parse(input);
        dates = dateParser.getDates();
        assertEquals("Number of dates", 0, dates.size());

        input = "add finish SR for assignment 2 from 12pm to 6pm today";
        dateParser.parse(input);
        dates = dateParser.getDates();
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Parsed words", "12pm to 6pm today", dateParser.getParsedWords());

        input = "add attend meeting 23 march 1200 - 1400";
        dateParser.parse(input);
        dates = dateParser.getDates();
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline", "2015-03-23", dates.get(0)
                                                    .toLocalDate()
                                                    .toString());
    }
    
    @Test
    public void inputWithFalseMatchingWords() {
        String input;
        DateParser dateParser = DateParser.getInstance();
        ArrayList<LocalDateTime> dates;
        
        input = "fries";
        dateParser.parse(input);
        dates = dateParser.getDates();
        assertEquals("Number of dates", 0, dates.size());
    }
    
}
