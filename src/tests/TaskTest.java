package tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.DateParser;
import main.java.Task;

import org.junit.Test;

public class TaskTest {

    @Test
    public void testFloat() {
        /**
         * A gotcha for this test: the testingTask is created with
         * "attend meeting later today" When testing, make sure to change
         * "2015-xx-xx" to whatever date it is today, or it will obviously fail,
         * even though the test is running fine.
         */
        String text = "attend meeting later today";
        Task testingTask = createNewTask(text);
        assertEquals("attend meeting later", testingTask.getDescription());
        assertEquals("2015-04-04", testingTask.getDate().toString());
        assertEquals(null, testingTask.getStartTime());
        assertFalse(testingTask.isCompleted());
        testingTask.markAsComplete();
        assertTrue(testingTask.isCompleted());
    }

    @Test
    public void testTimed() {
        String text = "attend meeting later at 1200 - 1400 on 20 Feb";
        Task testingTask = createNewTask(text);
        assertEquals("attend meeting later", testingTask.getDescription());
        assertEquals(20, testingTask.getDate().getDayOfMonth());
        assertEquals(2, testingTask.getDate().getMonthValue());
        assertEquals("12:00", testingTask.getStartTime().toString());
        assertEquals("14:00", testingTask.getEndTime().toString());
        assertFalse(testingTask.isCompleted());
        testingTask.markAsComplete();
        assertTrue(testingTask.isCompleted());
    }

    @Test
    public void testDeadline() {
        String text = "finish homework by 20 Feb";
        Task testingTask = createNewTask(text);
        assertEquals("finish homework", testingTask.getDescription());
        assertEquals(20, testingTask.getDate().getDayOfMonth());
        assertEquals(2, testingTask.getDate().getMonthValue());
        assertEquals(null, testingTask.getStartTime());
        assertFalse(testingTask.isCompleted());
        testingTask.markAsComplete();
        assertTrue(testingTask.isCompleted());
    }

    @Test
    public void testSetDescription() {
        String text = "finish homework by 20 Feb";
        Task task = createNewTask(text);
        assertEquals("finish homework", task.getDescription());
        task.setDescription("do not do homework");
        assertEquals("do not do homework", task.getDescription());
    }

    @Test
    public void testExtractDate() {
        String text = "finish homework by 20 feb";
        Task task = createNewTask(text);
        // task.extractDate();
    }

    @Test
    public void testTaskClone() throws Exception {
        String text = "do this by 14 apr";
        Task original = createNewTask(text);
        Task clone = original.clone();

        // Check that the clone has the same properties as the original
        assertEquals(original.getDate(), clone.getDate());

        // Check that changes to original does not affect clone
        original.setDescription("hola amigos");
        assertNotEquals(original.getDescription(), clone.getDescription());
        assertEquals(original.getDescription(), original.getDescription());
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
