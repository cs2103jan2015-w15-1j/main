package tests;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
    }

    @Test
    public void inputWithNumericDateNoDuration() {
        String input = "15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());

        input = "mar 15";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date",
                     constructDate(2015, 3, 15),
                     dates.get(0).toLocalDate());

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
    }
    
    @Test
    public void inputWithRelaxedDateAndTime() {
        String input = "today 6pm";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now(),
                     dates.get(0).toLocalDate());
        
        input = "6pm tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());
        
        input = "6pm next week";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusWeeks(1),
                     dates.get(0).toLocalDate());
        
        input = "next week 6pm";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date & time",
                     LocalDate.now().plusWeeks(1),
                     dates.get(0).toLocalDate());
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
        
        input = "do homework 4pm to 6pm today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().atTime(16, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().atTime(18, 0),
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
        
        input = "attend meeting from \"20\" 1200 - 1400 on 20 Feb";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 2, 20, 12, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 2, 20, 14, 0),
                     dates.get(1));
    }

    @Test
    public void inputWithDateAndNonChronologicalTime() {
        String input = "6pm to 2pm on 15 mar";
        ArrayList<LocalDateTime> dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 15, 18, 0),
                     dates.get(0));
    }


    @Test
    public void inputWithNumbers() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "\"do assignment 2\" tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());

        input = "do CS1231 tutorial by tomorrow";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Deadline",
                     LocalDate.now().plusDays(1),
                     dates.get(0).toLocalDate());

        input = "create 20 word poem";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());

        input = "add finish SR for assignment 2 from 12pm to 6pm today";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().atTime(12, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().atTime(18, 0),
                     dates.get(1));

        input = "add attend meeting 23 march 1200 - 1400";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 2, dates.size());
        assertEquals("Start date & time",
                     constructDateTime(2015, 3, 23, 12, 00),
                     dates.get(0));
        assertEquals("End date & time",
                     constructDateTime(2015, 3, 23, 14, 00),
                     dates.get(1));
        
        input = "2 friday";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
    }

    @Test
    public void inputWithFalseMatchingWords() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "fries";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 0, dates.size());
        
        input = "find girlfriend in 2016";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
    }

    @Test
    public void inputWithRecurringDate() {
        String input;
        ArrayList<LocalDateTime> dates;

        String today = LocalDate.now().getDayOfWeek().toString();
        input = String.format("watch tv every %s 8pm to 9pm until 6 jun", today);
        dates = getParsedDates(input);
        assertEquals("Number of dates", 3, dates.size());
        assertEquals("Start date & time",
                     LocalDate.now().plusWeeks(1).atTime(20, 0),
                     dates.get(0));
        assertEquals("End date & time",
                     LocalDate.now().plusWeeks(1).atTime(21, 0),
                     dates.get(1));
        assertEquals("Recur until date",
                     constructDate(2015, 6, 6),
                     dates.get(2).toLocalDate());
    }
    
    @Test
    public void inputWithHolidays() {
        String input;
        ArrayList<LocalDateTime> dates;

        input = "\"good bye today thursday and friday\" today ";
        dates = getParsedDates(input);
        assertEquals("Number of dates", 1, dates.size());
        assertEquals("Date", constructDate(2015, 4, 7), dates.get(0).toLocalDate());
    }

}
