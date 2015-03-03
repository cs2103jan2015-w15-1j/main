import static org.junit.Assert.*;
import org.junit.Test;


public class TaskTest {
	
	@Test
	public void testFloat() {
		Task testingTask = new Task("attend meeting later today");
		assertEquals("attend meeting later today", testingTask.getInfo());
		assertEquals(null, testingTask.getDay());
		assertEquals(null, testingTask.getMonth());
		assertEquals(null, testingTask.getTime());
		assertFalse(testingTask.getTaskStatus());
		testingTask.markAsComplete();
		assertTrue(testingTask.getTaskStatus());
	}
	
	@Test
	public void testTimed() {
		Task testingTask = new Task("attend meeting later at 1200-1400 on 20 Feb");
		assertEquals("attend meeting later", testingTask.getInfo());
		assertEquals("20", testingTask.getDay());
		assertEquals("Feb", testingTask.getMonth());
		assertEquals("1200-1400", testingTask.getTime());
		assertFalse(testingTask.getTaskStatus());
		testingTask.markAsComplete();
		assertTrue(testingTask.getTaskStatus());
	}
	
	@Test
	public void testDeadline() {
		Task testingTask = new Task("finish homework by 20 Feb");
		assertEquals("finish homework", testingTask.getInfo());
		assertEquals("20", testingTask.getDay());
		assertEquals("Feb", testingTask.getMonth());
		assertEquals(null, testingTask.getTime());
		assertFalse(testingTask.getTaskStatus());
		testingTask.markAsComplete();
		assertTrue(testingTask.getTaskStatus());
	}
}
