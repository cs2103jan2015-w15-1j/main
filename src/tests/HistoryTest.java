package tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import main.java.DateParser;
import main.java.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.History;

//@author A0121813U
public class HistoryTest {

	private ArrayList<LocalDateTime> parsedDates;
	private String parsedWords;
	private String nonParsedWords;

	@Test
	// Checks if the newly created object have nothing inside
	public void testEmptyHistory() {
		History history = new History();
		assertTrue(history.isEmpty());
	}
	
	@Test
	// Checks that the ArrayList and Observablist passed into History wont manipulate other data
	public void testHistoryWithTasks() {
		ArrayList<Task> listA = new ArrayList<Task>();
		ObservableList<Task> listO = FXCollections.observableArrayList();
		
		Task task1 = createTask("First");
		Task task2 = createTask("Second");
		Task task3 = createTask("Third");
		Task task4 = createTask("Fourth");
		
		listA.add(task1);
		listA.add(task2);
		listA.add(task3);
		listA.add(task4);
		
		listO.add(task1);
		listO.add(task2);
		listO.add(task3);
		listO.add(task4);
		
		History history = new History();
		
		history.storeCurrentState(listA, listO);
		
		assertFalse(history.isEmpty());
		
		// Put some changes in listA and listO
		listA.remove(1); // --> (1,3,4)
		listO.remove(3); // --> (1,2,3)
		
		history.storeCurrentState(listA, listO);
		
		// Put some changes in listA and listO
		listA.remove(0); // --> (3,4)
		listO.remove(1); // --> (1,3)
		
		history.storeCurrentState(listA, listO);
		
		history.getPreviousState(); // First "undo"
		
		assertEquals(2, history.getAllSize());
		assertEquals(2, history.getDisplayedSize());
		assertEquals(task3.toString(), history.getAllTasks().get(0).toString());
		assertEquals(task4.toString(), history.getAllTasks().get(1).toString());
		assertEquals(task1.toString(), history.getDisplayedTasks().get(0).toString());
		assertEquals(task3.toString(), history.getDisplayedTasks().get(1).toString());
		
		history.getPreviousState(); // Second "undo"
		
		assertEquals(3, history.getAllSize());
		assertEquals(3, history.getDisplayedSize());
		assertEquals(task1.toString(), history.getAllTasks().get(0).toString());
		assertEquals(task3.toString(), history.getAllTasks().get(1).toString());
		assertEquals(task4.toString(), history.getAllTasks().get(2).toString());
		assertEquals(task1.toString(), history.getDisplayedTasks().get(0).toString());
		assertEquals(task2.toString(), history.getDisplayedTasks().get(1).toString());
		assertEquals(task3.toString(), history.getDisplayedTasks().get(2).toString());
		
		history.getPreviousState(); // Third "undo"
		
		assertEquals(4, history.getAllSize());
		assertEquals(4, history.getDisplayedSize());
		assertEquals(task1.toString(), history.getAllTasks().get(0).toString());
		assertEquals(task2.toString(), history.getAllTasks().get(1).toString());
		assertEquals(task3.toString(), history.getAllTasks().get(2).toString());
		assertEquals(task4.toString(), history.getAllTasks().get(3).toString());
		assertEquals(task1.toString(), history.getDisplayedTasks().get(0).toString());
		assertEquals(task2.toString(), history.getDisplayedTasks().get(1).toString());
		assertEquals(task3.toString(), history.getDisplayedTasks().get(2).toString());
		assertEquals(task4.toString(), history.getDisplayedTasks().get(3).toString());
	}
	
	@Test
	// Checks whether the Feedback String is stored properly
	public void testFeedbackHistory() {
		History history = new History();
		String string1 = "Hello";
		String string2 = "Good morning";
		String string3 = "Wazzzzzzupppp";
		String string4 = "How are you?";
		
		history.storeCommand(string3);
		history.storeCommand(string1);
		history.storeCommand(string4);
		history.storeCommand(string2);
		
		assertEquals("Good morning", history.getPreviousCommand());
		assertEquals("How are you?", history.getPreviousCommand());
		assertEquals("Hello", history.getPreviousCommand());
		assertEquals("Wazzzzzzupppp", history.getPreviousCommand());	
	}
	
	// Helper function to create an instance of a Task object quickly
	private Task createTask(String input) {
		DateParser dp = DateParser.getInstance();
		dp.parse(input);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNotParsedWords();
	    return new Task(input, parsedDates, parsedWords, nonParsedWords);
	}

}
