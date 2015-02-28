import static org.junit.Assert.*;
import org.junit.Test;


public class TaskTest {

	@Test
	public void testIsDeadlineValid() {
		Task testingTask = new Task("do homework by 23 Jan");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("do homework by 31 Dec");
		assertTrue(testingTask.isDeadline());
		
		testingTask = new Task("something something by 50 Apr");
		assertTrue(testingTask.isDeadline());
	}
	
	@Test
	public void testIsDeadlineInvalid() {
		Task testingTask = new Task("do homework by 30 March");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework by today");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do homework on 20 Apr");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("by 10 Oct do homework");
		assertFalse(testingTask.isDeadline());
		
		testingTask = new Task("do what I want");
		assertFalse(testingTask.isDeadline());
	}
	
	@Test
	public void testIsTimedValid() {
		Task testingTask = new Task("attend meeting at 1200-1400 on 20 Apr");
		assertTrue(testingTask.isTimed());
		
		testingTask = new Task("gibberish at 12-14 on 100 Dec");
		assertTrue(testingTask.isTimed());
		
		testingTask = new Task("random at 5643-654321 on -3 May");
		assertTrue(testingTask.isTimed());
	}
	
	@Test
	public void testIsTimedInvalid() {
		Task testingTask = new Task("attend meeting on 1200-1400 on 20 Apr");
		assertFalse(testingTask.isTimed());
		
		testingTask = new Task("attend meeting at 1200 by 5 April");
		assertFalse(testingTask.isTimed());
		
		testingTask = new Task("gibberish at 12529 on 10th Nov");
		assertFalse(testingTask.isTimed());
		
		testingTask = new Task("random stuffs on 12pm-2pm on 2 Feb");
		assertFalse(testingTask.isTimed());
		
		testingTask = new Task("attend meeting on 1200-1400 later");
		assertFalse(testingTask.isTimed());
	}
}
