import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class TaskTest {

	@Test
	public void testIsDeadlineValid() {
		Task testingTask = new Task("do homework by Jan");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("do homework by Dec");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("something something by Apr");
		assertTrue(testingTask.isDeadline());
	}
	
	@Test
	public void testIsDeadlineInvalid() {
		Task testingTask = new Task("do homework by March");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework by today");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework on Apr");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("by Oct do homework");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do what I want");
		assertFalse(testingTask.isDeadline());
	}
	
	@Test
	public void testIsTimedTaskHelperValid() {
		Task testingTask = new Task("random gibberish");
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("at");
		testList.add("1200-1400");
		testList.add("on");
		testList.add("Apr");
		assertTrue(testingTask.isTimedTaskHelper(testList));
		
		testList = new ArrayList<String>();
		testList.add("at");
		testList.add("10-20");
		testList.add("on");
		testList.add("Apr");
		assertTrue(testingTask.isTimedTaskHelper(testList));
		
		testList = new ArrayList<String>();
		testList.add("at");
		testList.add("102032-12312314");
		testList.add("on");
		testList.add("Apr");
		assertTrue(testingTask.isTimedTaskHelper(testList));
	}
}
