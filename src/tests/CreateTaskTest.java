package tests;

import static org.junit.Assert.*;

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

    // ================================================================
    // test getting the right rate and frequency
    // ================================================================
    @Test
    public void frequencyTest() {
        assertEquals("1 daily", CreateTask.frequencyTest(frequencyStrings[0]));
        assertEquals("3 daily", CreateTask.frequencyTest(frequencyStrings[1]));
        assertEquals("1 daily", CreateTask.frequencyTest(frequencyStrings[2]));
        assertEquals("", CreateTask.frequencyTest(frequencyStrings[3]));
        assertEquals("1 monthly", CreateTask.frequencyTest(frequencyStrings[4]));
        assertEquals("2 weekly", CreateTask.frequencyTest(frequencyStrings[5]));
        assertEquals("1 yearly", CreateTask.frequencyTest(frequencyStrings[6]));
        assertEquals("1 weekly", CreateTask.frequencyTest(frequencyStrings[7]));
        assertEquals("2 yearly", CreateTask.frequencyTest(frequencyStrings[8]));
        assertEquals("3 monthly", CreateTask.frequencyTest(frequencyStrings[9]));
    }
    
    public Task createNewTask(String input) {
        DateParser parser = DateParser.getInstance();
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String nonParsedWords = parser.getNotParsedWords();
        return new Task(input, parsedDates, parsedWords, nonParsedWords);
    }
}
