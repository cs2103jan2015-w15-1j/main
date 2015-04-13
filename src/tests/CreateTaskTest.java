package tests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.CreateTask;
import main.java.DateParser;
import main.java.Task;

import org.junit.Test;

//@author A0122393L
public class CreateTaskTest {
    DateParser dateParser = DateParser.getInstance();
    String[] frequencyStrings = { "everyday", "every 3 day", "daily",
            "nothing", "monthly", "every 2 weeks", "yearly", "weekly",
            "every 2 year", "every 3 month" };
    String[] exceptionStrings = { "except 13 apr", "except 14 apr",
            "except 24 may", "except 30 jun, 23 jun", "except 32 jan" };

    // ================================================================
    // test getting the right rate and frequency
    // ================================================================
    @Test
    public void frequencyTest() {
        assertEquals("1 daily", CreateTask.testFrequency(frequencyStrings[0]));
        assertEquals("3 daily", CreateTask.testFrequency(frequencyStrings[1]));
        assertEquals("1 daily", CreateTask.testFrequency(frequencyStrings[2]));
        assertEquals("", CreateTask.testFrequency(frequencyStrings[3]));
        assertEquals("1 monthly", CreateTask.testFrequency(frequencyStrings[4]));
        assertEquals("2 weekly", CreateTask.testFrequency(frequencyStrings[5]));
        assertEquals("1 yearly", CreateTask.testFrequency(frequencyStrings[6]));
        assertEquals("1 weekly", CreateTask.testFrequency(frequencyStrings[7]));
        assertEquals("2 yearly", CreateTask.testFrequency(frequencyStrings[8]));
        assertEquals("3 monthly", CreateTask.testFrequency(frequencyStrings[9]));
    }

    // ================================================================
    // test getting the exception dates
    // ================================================================
    @Test
    public void exceptionTest() {
        assertEquals("2015-04-13 ",
                CreateTask.testException(exceptionStrings[0]));
        assertEquals("2015-04-14 ",
                CreateTask.testException(exceptionStrings[1]));
        assertEquals("2015-05-24 ",
                CreateTask.testException(exceptionStrings[2]));
        assertEquals("2015-06-30 2015-06-23 ",
                CreateTask.testException(exceptionStrings[3]));
        assertEquals("2015-01-01 ",
                CreateTask.testException(exceptionStrings[4]));
    }

    // ================================================================
    // test getting the exception dates
    // ================================================================
    @Test
    public void datesTesting() {
        String input ="";
        ArrayList<String> result;
        
        input = "add watch anime every day 2pm to 5pm from 10 may until 30 jun";
        result = CreateTask.testNeededDates(input);
        assertEquals("2015-05-10", result.get(0));
        assertEquals("2015-06-30", result.get(1));
        assertEquals("2016-05-10", result.get(2));
        

        input = "add go overseas every year from 30 may";
        result = CreateTask.testNeededDates(input);
        assertEquals("2015-05-30", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("2025-05-30", result.get(2));
        

        input = "add attend boring meeting every month";
        result = CreateTask.testNeededDates(input);
        assertEquals(LocalDate.now().toString(), result.get(0));
        assertEquals("", result.get(1));
        assertEquals("2018-04-13", result.get(2));
    }
}
