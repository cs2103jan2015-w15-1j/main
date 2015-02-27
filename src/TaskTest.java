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
	
	// Note that this method wont check the the first index of the array
	// The main "isTimedTask" helper will handle that
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
	
	@Test
	public void testIsTimedTaskHelperInvalid() {
		Task testingTask = new Task("random gibberish");
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("at");
		testList.add("1200-1400");
		testList.add("on");
		testList.add("April");
		assertFalse(testingTask.isTimedTaskHelper(testList));
		
		testList = new ArrayList<String>();
		testList.add("at");
		testList.add("12pm-2pm");
		testList.add("on");
		testList.add("Apr");
		assertFalse(testingTask.isTimedTaskHelper(testList));
		
		testList = new ArrayList<String>();
		testList.add("at");
		testList.add("1200-1400");
		testList.add("by");
		testList.add("Apr");
		assertFalse(testingTask.isTimedTaskHelper(testList));
		
		testList = new ArrayList<String>();
		testList.add("at");
		testList.add("156432-12344");
		testList.add("on");
		testList.add("apr");
		assertFalse(testingTask.isTimedTaskHelper(testList));
	}
	
	@Test
	public void testIsTimedTaskValid() {
		Task testingTask = new Task("attend meeting at 1200-1400 on Apr");
		assertTrue(testingTask.isTimedTask());
		
		testingTask = new Task("gibberish at 12-14 on Dec");
		assertTrue(testingTask.isTimedTask());
		
		testingTask = new Task("random at 5643-654321 on May");
		assertTrue(testingTask.isTimedTask());
	}
	
	@Test
	public void testIsTimedTaskInvalid() {
		Task testingTask = new Task("attend meeting on 1200-1400 on Apr");
		assertFalse(testingTask.isTimedTask());
		
		testingTask = new Task("attend meeting at 1200 by April");
		assertFalse(testingTask.isTimedTask());
		
		testingTask = new Task("gibberish at 12529 on Nov");
		assertFalse(testingTask.isTimedTask());
		
		testingTask = new Task("random stuffs on 12pm-2pm on Feb");
		assertFalse(testingTask.isTimedTask());
		
		testingTask = new Task("attend meeting on 1200-1400 later");
		assertFalse(testingTask.isTimedTask());
	}
}
