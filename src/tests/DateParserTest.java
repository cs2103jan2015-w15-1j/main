package tests;

import static org.junit.Assert.*;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import main.java.DateParser;

import org.junit.Test;

//@author A0121520A
public class DateParserTest {

    private ArrayList<LocalDateTime> getParsedDates(String input) {
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        return dateParser.getDates();
    }
    
    private String getParsedWords(String input) {
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        return dateParser.getParsedWords();
    }
    
    private String getNotParsedWords(String input) {
        DateParser dateParser = DateParser.getInstance();
        dateParser.parse(input);
        return dateParser.getNotParsedWords();
    }

    private LocalDate constructDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    private LocalDateTime constructDateTime(int year,
                                            int month,
                                            int day,
                                            int hour,
                                            int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
    
    private LocalDate getDateOfComingDay(DayOfWeek day) {
        return LocalDate.now().with(TemporalAdjusters.next(day));
    }

    @Test
    public void inputWithNoDateNoDuration() {
        String input = "do homework";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        assertEquals("Not parsed words", input, getNotParsedWords(input));
        assertEquals("Parsed words", "", getParsedWords(input));
    }

    @Test
    public void inputWithNumericDateNoDuration() {
        String input = "15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "", getNotParsedWords(input));
        assertEquals("Parsed words", "15 mar", getParsedWords(input));

        input = "mar 15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "", getNotParsedWords(input));
        assertEquals("Parsed words", "mar 15", getParsedWords(input));

        input = "15 march";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());

        input = "15 march 2018";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2018, 3, 15),
                     dates.get(0).toLocalDate());
        
        input = "03/15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());
    }
    
    @Test
    public void inputWithRelaxedDateNoDuration() {
        String input = "today";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     LocalDate.now(),
                     dates.get(0).toLocalDate());

        input = "tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());

        input = "wednesday";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     getDateOfComingDay(DayOfWeek.WEDNESDAY),
                     dates.get(0).toLocalDate());

        input = "next week";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     LocalDate.now().plusWeeks(1),
                     dates.get(0).toLocalDate());
        
        input = "next month";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     LocalDate.now().plusMonths(1),
                     dates.get(0).toLocalDate());
        
        input = "next year";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     LocalDate.now().plusYears(1),
                     dates.get(0).toLocalDate());
    }

    @Test
    public void inputWithNumericDateAndTime() {
        String input = "15 mar 6pm";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "6pm 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "1800 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "15 mar 2015 1800";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "1800 15 mar 2015";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "1800 03/15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "6pm 03/15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
        
        input = "read harry potter on 12 apr at 1200";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     constructDateTime(2015, 4, 12, 12, 0),
                     dates.get(0));
        assertEquals("Not parsed words", "read harry potter on", getNotParsedWords(input));
        assertEquals("Parsed words", "12 apr at 1200", getParsedWords(input));
    }
    
    @Test
    public void inputWithRelaxedDateAndTime() {
        String input = "today 6pm";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().atTime(18, 0),
                     dates.get(0));
        
        input = "6pm tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusDays(1).atTime(18, 0),
                     dates.get(0));
        
        input = "6pm next week";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusWeeks(1).atTime(18, 0),
                     dates.get(0));
        
        input = "next week 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusWeeks(1).atTime(18, 0),
                     dates.get(0));
        
        input = "2359 today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().atTime(23, 59),
                     dates.get(0));
        
        input = "today 2359";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().atTime(23, 59),
                     dates.get(0));
    }

    
    @Test
    public void inputWithNumericDateAndDuration() {
        String input = "4pm to 6pm on 15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(1));
        
        input = "15 mar 4pm to 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(1));

        input = "15 mar 4.30pm to 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 30),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(1));

        input = "15 mar 4.30pm to 6.30pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 30),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 30),
                     dates.get(1));

        input = "15 mar 4pm - 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(1));

        input = "1600 - 1800 on 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(1));

        input = "4pm to 6:30pm on 15 mar";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 15, 18, 30),
                     dates.get(1));
        
        input = "attend meeting 1200 - 1400 on 20 Feb";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 2, 20, 12, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 2, 20, 14, 0),
                     dates.get(1));
        assertEquals("Not parsed words", "attend meeting", getNotParsedWords(input));
        assertEquals("Parsed words", "1200 - 1400 on 20 Feb", getParsedWords(input));
    }
    
    @Test
    public void inputWithRelaxedDateAndDuration() {
        String input = "4pm to 6pm today";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().atTime(16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().atTime(18, 0),
                     dates.get(1));
        
        input = "4pm to 6pm tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().plusDays(1).atTime(16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().plusDays(1).atTime(18, 0),
                     dates.get(1));
        
        input = "4pm to 6pm next week";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().plusWeeks(1).atTime(16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().plusWeeks(1).atTime(18, 0),
                     dates.get(1));
    }

    @Test
    public void inputWithDateAndNonChronologicalTime() {
        String input = "6pm to 5pm on 15 mar";
        try {
            getParsedDates(input);
        } catch (DateTimeException e){
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void inputWithNumbers() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "do assignment 2 tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "do assignment 2", getNotParsedWords(input));
        assertEquals("Parsed words", "tomorrow", getParsedWords(input));
        
        input = "do CS1231 tutorial 8";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        assertEquals("Not parsed words", "do CS1231 tutorial 8", getNotParsedWords(input));
        assertEquals("Parsed words", "", getParsedWords(input));

        input = "do CS1231 tutorial 2 by 2pm tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     LocalDate.now().plusDays(1).atTime(14, 0),
                     dates.get(0));
        assertEquals("Not parsed words", "do CS1231 tutorial 2 by", getNotParsedWords(input));
        assertEquals("Parsed words", "2pm tomorrow", getParsedWords(input));

        input = "create 20 word poem";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        assertEquals("Not parsed words", "create 20 word poem", getNotParsedWords(input));
        assertEquals("Parsed words", "", getParsedWords(input));

        input = "add assignment 2 from 12pm to 6pm today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().atTime(12, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().atTime(18, 0),
                     dates.get(1));

        input = "add attend meeting 20 from 1200 - 1400 23 march";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 23, 12, 00),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 23, 14, 00),
                     dates.get(1));
        assertEquals("Not parsed words", "add attend meeting 20 from", getNotParsedWords(input));
        assertEquals("Parsed words", "1200 - 1400 23 march", getParsedWords(input));
        
        input = "add attend meeting 20 from 12pm - 2pm on 23 march";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 23, 12, 00),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 23, 14, 00),
                     dates.get(1));
        assertEquals("Not parsed words", "add attend meeting 20 from", getNotParsedWords(input));
        assertEquals("Parsed words", "12pm - 2pm on 23 march", getParsedWords(input));
        
        input = "2 friday";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Not parsed words", "2", getNotParsedWords(input));
        assertEquals("Parsed words", "friday", getParsedWords(input));
    }

    @Test
    public void inputWithPastDateTimes() {
        String input;
        ArrayList<LocalDateTime> dates;
        
        input = "do this 25 mar 2014";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     constructDate(2014, 3, 25),
                     dates.get(0).toLocalDate());
        
        input = "do this 6pm 25 mar 2014";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     constructDateTime(2014, 3, 25, 18, 0),
                     dates.get(0));
        
    }
    
    @Test
    public void inputWithFalseMatchingWords() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "fries";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        
        input = "find girlfriend";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
    }
    
    @Test
    public void inputWithHolidays() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "good friday";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        
        input = "find easter eggs by friday";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date", getDateOfComingDay(DayOfWeek.FRIDAY), dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "find easter eggs by", getNotParsedWords(input));
        assertEquals("Parsed words", "friday", getParsedWords(input));
    }
    
    @Test
    public void inputWithEscapeChar() {
        String input;
        ArrayList<LocalDateTime> dates;
        
        input = "watch \"day after tomorrow\" today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date", LocalDate.now(), dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "watch \"day after tomorrow\"", getNotParsedWords(input));
        assertEquals("Parsed words", "today", getParsedWords(input));
        
        input = "read \"today\" today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date", LocalDate.now(), dates.get(0).toLocalDate());
        assertEquals("Not parsed words", "read \"today\"", getNotParsedWords(input));
        assertEquals("Parsed words", "today", getParsedWords(input));
    }
}
