import static org.junit.Assert.*;

import org.junit.Test;


public class TaskTest {

	@Test
	public void testIsDeadline() {
		Task testingTask = new Task("do homework by Jan");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("do homework by Dec");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("do homework by March");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework by today");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework on Apr");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("by Oct do homework");
		assertFalse(testingTask.isDeadline());
	}
}
