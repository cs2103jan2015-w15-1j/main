import static org.junit.Assert.*;
import org.junit.Test;


public class TaskTest {
	
	@Test
	public void testFloat() {
		Task testingTask = new Task("attend meeting later today");
		assertEquals("attend meeting later", testingTask.getDescription());
		assertEquals("2015-03-11", testingTask.getDate().toString());
		assertEquals(null, testingTask.getStartTime());
		assertFalse(testingTask.isCompleted());
		testingTask.markAsComplete();
		assertTrue(testingTask.isCompleted());
	}
	
	@Test
	public void testTimed() {
		Task testingTask = new Task("attend meeting later at 1200 - 1400 on 20 Feb");
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
		Task testingTask = new Task("finish homework by 20 Feb");
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
		Task task = new Task("finish homework by 20 Feb");
		assertEquals("finish homework", task.getDescription());
		task.setDescription("do not do homework");
		assertEquals("do not do homework", task.getDescription());
	}

	@Test
	public void testExtractDate() {
		Task task = new Task("finish homework by 20 feb");
		//task.extractDate();
	}
}
