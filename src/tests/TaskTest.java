package tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.DateParser;
import main.java.Task;

import org.junit.Test;

//@author A0121813U
public class TaskTest {

    @Test
    public void testFloat() {
        String text = "attend meeting later";
        Task testingTask = createNewTask(text);
        
        // Testing type and the info w/wo parsed
        assertEquals(Task.Type.FLOATING, testingTask.getType());
        assertEquals("attend meeting later", testingTask.getRawInfo());
        assertEquals("attend meeting later", testingTask.getDescription());
        
        // Testing LocalDateTime 
        assertEquals(null, testingTask.getDate());
        assertEquals(null, testingTask.getStartTime());
        assertEquals(null, testingTask.getEndTime());
        
        // Testing other attributes
        assertFalse(testingTask.isCompleted());
        assertEquals(null, testingTask.getId());
        assertFalse(testingTask.isOverdue());
    }

    @Test
    public void testTimed() {
        String text = "attend meeting later at 1200 - 1400 on 20 Feb";
        Task testingTask = createNewTask(text);
        
        // Testing type and the info w/wo parsed
        assertEquals(Task.Type.TIMED, testingTask.getType());
        assertEquals("attend meeting later at 1200 - 1400 on 20 Feb", testingTask.getRawInfo());
        assertEquals("attend meeting later", testingTask.getDescription());
        
        // Testing LocalDateTime
        assertEquals("2015-02-20", testingTask.getDate().toString());
        assertEquals(20, testingTask.getDate().getDayOfMonth());
        assertEquals(2, testingTask.getDate().getMonthValue());
        assertEquals(2015, testingTask.getDate().getYear());
        assertEquals("12:00", testingTask.getStartTime().toString());
        assertEquals("14:00", testingTask.getEndTime().toString());
        
        // Testing other attributes
        assertFalse(testingTask.isCompleted());
        assertEquals(null, testingTask.getId());
        assertTrue(testingTask.isOverdue());
    }

    @Test
    public void testDeadline() {
        String text = "finish homework by 20 Feb";
        Task testingTask = createNewTask(text);
        
        // Testing type and the info w/wo parsed
        assertEquals(Task.Type.DEADLINE, testingTask.getType());
        assertEquals("finish homework by 20 Feb", testingTask.getRawInfo());
        assertEquals("finish homework", testingTask.getDescription());
        
        // Testing LocalDateTime
        assertEquals("2015-02-20", testingTask.getDate().toString());
        assertEquals(20, testingTask.getDate().getDayOfMonth());
        assertEquals(2, testingTask.getDate().getMonthValue());
        assertEquals(2015, testingTask.getDate().getYear());
        assertEquals(null, testingTask.getStartTime());
        assertEquals(null, testingTask.getEndTime());
        
        // Testing other attributes
        assertFalse(testingTask.isCompleted());
        assertEquals(null, testingTask.getId());
        assertTrue(testingTask.isOverdue());
    }

    @Test
    public void testSetDescriptionFloating() {
        String text = "finish homework";
        Task task = createNewTask(text);
        
        // Testing before modifying description
        assertEquals("finish homework", task.getDescription());
        assertEquals(null, task.getDate());
        assertEquals(null, task.getStartTime());
        assertEquals(null, task.getEndTime());
        
        task.setDescription("do not do homework");
        
        // Testing after modifying description
        assertEquals("do not do homework", task.getDescription());
        assertEquals(null, task.getDate());
        assertEquals(null, task.getStartTime());
        assertEquals(null, task.getEndTime());
    }
    
    @Test
    public void testSetDescriptionDeadline() {
        String text = "finish homework by today";
        Task task = createNewTask(text);
        
        // Testing before modifying description
        assertEquals("finish homework", task.getDescription());
        assertEquals("2015-04-08", task.getDate().toString());
        assertEquals(null, task.getStartTime());
        assertEquals(null, task.getEndTime());
        
        task.setDescription("do not do homework");
        
        // Testing after modifying description
        assertEquals("do not do homework", task.getDescription());
        assertEquals("2015-04-08", task.getDate().toString());
        assertEquals(null, task.getStartTime());
        assertEquals(null, task.getEndTime());
    }
    
    @Test
    public void testSetDescriptionTimed() {
        String text = "attend meeting 2pm to 3pm on 25 april";
        Task task = createNewTask(text);
        
    	// Testing before modifying description
        assertEquals("attend meeting", task.getDescription());
        assertEquals("2015-04-25", task.getDate().toString());
        assertEquals("14:00", task.getStartTime().toString());
        assertEquals("15:00", task.getEndTime().toString());
        
        task.setDescription("watch movie");
        
        // Testing after modifying description
        assertEquals("watch movie", task.getDescription());
        assertEquals("2015-04-25", task.getDate().toString());
        assertEquals("14:00", task.getStartTime().toString());
        assertEquals("15:00", task.getEndTime().toString());
    }

    @Test
    public void testCompleteness() {
    	String text = "do this by 14 apr";
    	Task task = createNewTask(text);
    	
    	assertFalse(task.isCompleted());
    	task.markAsCompleted();
    	assertTrue(task.isCompleted());
    	task.markAsIncomplete();
    	assertFalse(task.isCompleted());
    }

    @Test
    public void testTaskClone() throws CloneNotSupportedException {
        String text = "do this by 14 apr 1pm to 3pm";
        Task original = createNewTask(text);
        Task clone = original.clone();

        // Check that the clone has the same properties as the original
        assertEquals(original.getDescription(), clone.getDescription());
        assertEquals(original.getDate(), clone.getDate());
        assertEquals(original.getStartTime(), clone.getStartTime());
        assertEquals(original.getEndTime(), clone.getEndTime());

        // Check that changes to clone does not affect original
        clone.setDescription("do that");
        assertNotEquals(original.getDescription(), clone.getDescription());
        assertEquals("do that", clone.getDescription());
    }
    
    // Created for the purpose for this test. Simplify the constructor of Task. NOT A TEST
    public Task createNewTask(String input) {
        DateParser parser = DateParser.getInstance();
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String nonParsedWords = parser.getNotParsedWords();
        return new Task(input, parsedDates, parsedWords, nonParsedWords);
    }
}
