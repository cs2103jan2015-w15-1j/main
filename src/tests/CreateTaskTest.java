package tests;

import static org.junit.Assert.*;
import main.java.CreateTask;

import org.junit.Test;

//@author A0122393L
public class CreateTaskTest {
    CreateTask taskCreate = CreateTask.getInstance();
    String[] testStrings = { "do homework every 3 day",
            "play game with friends daily",
            "buy grocery weekly from today to 23 jul",
            "i just want to simply do nothing",
            "transfer money to parents monthly",
            "consult professor every 2 weeks",
            "create a good and helpful software yearly",
            "find new android game weekly from tomorrow till 21 dec",
            "go overseas for a good get away every 2 year",
            "renew season parking for carpark every 3 month" };

    // -------------------------------------------------------------------
    // test getting the right rate and frequency
    // -------------------------------------------------------------------
    @Test
    public void frequencyTest() {
        assertEquals("3 daily", CreateTask.frequencyTest(testStrings[0]));
        assertEquals("1 daily", CreateTask.frequencyTest(testStrings[1]));
        assertEquals("1 weekly", CreateTask.frequencyTest(testStrings[2]));
        assertEquals("", CreateTask.frequencyTest(testStrings[3]));
        assertEquals("1 monthly", CreateTask.frequencyTest(testStrings[4]));
        assertEquals("2 weekly", CreateTask.frequencyTest(testStrings[5]));
        assertEquals("1 yearly", CreateTask.frequencyTest(testStrings[6]));
        assertEquals("1 weekly", CreateTask.frequencyTest(testStrings[7]));
        assertEquals("2 yearly", CreateTask.frequencyTest(testStrings[8]));
        assertEquals("3 monthly", CreateTask.frequencyTest(testStrings[9]));
    }
}
