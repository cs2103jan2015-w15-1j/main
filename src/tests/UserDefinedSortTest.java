package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.SortDate;
import main.java.SortIncomplete;
import main.java.SortOverdue;
import main.java.SortTime;
import main.java.UserDefinedSort;
import main.java.Task;
import main.java.DateParser;
import main.java.SortType;

//@author A0121813U
public class UserDefinedSortTest {
	
	ArrayList<LocalDateTime> parsedDates;
	String parsedWords;
	String nonParsedWords;
	UserDefinedSort uds;
	ArrayList<Task> list;

	@Test
	// Test the functionality of the main container
	public void TestUDSContainer() {
		ArrayList<Task> sampleList = new ArrayList<Task>();
		sampleList.add(createTask("first"));
		sampleList.add(createTask("second"));
		sampleList.add(createTask("third"));
		
		UserDefinedSort testContainer = new UserDefinedSort(sampleList);
		
		assertEquals(sampleList, testContainer.getList());
		assertEquals(sampleList, testContainer.executeSort());
	}
	
	@Test
	// Test whether the list will be sorted by Type using the SortType comparator
	public void TestTypeSort() {
		 
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
	    Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal tomorrow by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment today 2pm to 3pm");
	   
	    list.add(taskTimed);
	    list.add(taskDeadlineWithTime);
	    list.add(taskDeadlineNoTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortType());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadlineWithTime);
	    assertEquals(list.get(2), taskDeadlineNoTime);
	    assertEquals(list.get(3), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskFloating);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskDeadlineWithTime);
	    assertEquals(list.get(3), taskDeadlineNoTime);
	}
	
	@Test
	// Test whether the list will be sorted by date using the SortDate comparator
	public void TestDateSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal tomorrow by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment today 2pm to 3pm");

	    list.add(taskTimed);
	    list.add(taskDeadlineNoTime);
	    list.add(taskDeadlineWithTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortDate());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadlineNoTime);
	    assertEquals(list.get(2), taskDeadlineWithTime);
	    assertEquals(list.get(3), taskFloating);
	      
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskFloating);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskDeadlineNoTime);
	    assertEquals(list.get(3), taskDeadlineWithTime);
	    
	}
	
	@Test
	// Test whether the list will be sorted by overdue using the SortOverdue comparator
	public void TestOverdueSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment 9 march 2pm to 3pm");
	    
	    list.add(taskDeadlineWithTime);
	    list.add(taskTimed);
	    list.add(taskDeadlineNoTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortOverdue());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskDeadlineWithTime);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskDeadlineNoTime);
	    assertEquals(list.get(3), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadlineWithTime);
	    assertEquals(list.get(2), taskDeadlineNoTime);
	    assertEquals(list.get(3), taskFloating);
	}
	
	@Test
	// Test whether the list will be sorted by completeness using the SortIncomplete comparator
	public void TestIncompleteSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment 9 march 2pm to 3pm");

	    // Make 2 tasks completed
	    taskTimed.markAsCompleted();
	    taskFloating.markAsCompleted();
	    
	    list.add(taskDeadlineNoTime);
	    list.add(taskTimed);
	    list.add(taskDeadlineWithTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortIncomplete());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskDeadlineNoTime);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskDeadlineWithTime);
	    assertEquals(list.get(3), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskDeadlineNoTime);
	    assertEquals(list.get(1), taskDeadlineWithTime);
	    assertEquals(list.get(2), taskTimed);
	    assertEquals(list.get(3), taskFloating);
	}
	
	@Test
	// Test whether the list will be sorted by time using the SortTime comparator
	public void TestTimeSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment 9 march 2pm to 3pm");
	    
	    list.add(taskDeadlineNoTime);
	    list.add(taskTimed);
	    list.add(taskDeadlineWithTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortTime());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskDeadlineNoTime);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskDeadlineWithTime);
	    assertEquals(list.get(3), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskDeadlineNoTime);
	    assertEquals(list.get(1), taskFloating);
	    assertEquals(list.get(2), taskDeadlineWithTime);
	    assertEquals(list.get(3), taskTimed);
	}
	
	@Test
	// Test the main container with all the existing comparator in its chain
	// Two list of tasks in different order, when going through the same chain
	// should result in same output order
	public void TestSortChain() {
		
		Task taskDeadlineNoTime = createTask("buy milk tomorrow");
	    Task taskDeadlineWithTime = createTask("buy cereal by 9am");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment 9 march 2pm to 3pm");
	    
	    taskDeadlineNoTime.markAsCompleted();
		
		// Test sequence 1
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
	    
	    list.add(taskDeadlineNoTime);
	    list.add(taskTimed);
	    list.add(taskDeadlineWithTime);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortType());
	    uds.addComparator(new SortTime());
	    uds.addComparator(new SortDate());
	    uds.addComparator(new SortOverdue());
	    uds.addComparator(new SortIncomplete());
	    
	    uds.executeSort();
	    
	    // Test sequence 2
	    ArrayList<Task> listCopy = new ArrayList<Task>();
	    UserDefinedSort udsCopy = new UserDefinedSort(listCopy);
	    
	    listCopy.add(taskFloating);
	    listCopy.add(taskDeadlineWithTime);
	    listCopy.add(taskTimed);
	    listCopy.add(taskDeadlineNoTime);

	    udsCopy.addComparator(new SortType());
	    udsCopy.addComparator(new SortTime());
	    udsCopy.addComparator(new SortDate());
	    udsCopy.addComparator(new SortOverdue());
	    udsCopy.addComparator(new SortIncomplete());
	    
	    udsCopy.executeSort();
	    
	    assertEquals(list.get(0), listCopy.get(0));
	    assertEquals(list.get(1), listCopy.get(1));
	    assertEquals(list.get(2), listCopy.get(2));
	    assertEquals(list.get(3), listCopy.get(3));
	}
	
	private Task createTask(String input) {
		DateParser dp = DateParser.getInstance();
		dp.parse(input);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNotParsedWords();
	    return new Task(input, parsedDates, parsedWords, nonParsedWords);
	}
}
