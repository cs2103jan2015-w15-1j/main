package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import main.java.SortDate;
import main.java.SortIncomplete;
import main.java.SortOverdue;
import main.java.UserDefinedSort;
import main.java.Task;
import main.java.DateParser;
import main.java.SortType;

public class UserDefinedSortTest {
	
	ArrayList<LocalDateTime> parsedDates;
	String parsedWords;
	String nonParsedWords;
	UserDefinedSort uds;
	ArrayList<Task> list;
	
	public Task createTask(String input) {
		DateParser dp = DateParser.getInstance();
		dp.parse(input);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNotParsedWords();
	    return new Task(input, parsedDates, parsedWords, nonParsedWords);
	}

	@Test
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
	public void TestTypeSort() {
		 
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
	    Task taskDeadline = createTask("buy milk tomorrow");
	    Task taskFloating = createTask("do homework");
	    Task taskTimed = createTask("do assignment today 2pm to 3pm");
	   
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortType());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskFloating);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskTimed);
	}
	
	@Test
	public void TestDateSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskTimed = createTask("do assignment tomorrow");
		Task taskDeadline = createTask("buy milk today");
		Task taskFloating = createTask("do homework");

	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortDate());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskFloating);
	      
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskFloating);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskTimed);
	}
	
	@Test
	public void TestOverdueSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskTimed = createTask("do assignment today 2pm to 3pm");
		Task taskDeadline = createTask("buy milk yesterday");
		Task taskFloating = createTask("do homework");
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortOverdue());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskDeadline);
	    assertEquals(list.get(1), taskTimed);
	    assertEquals(list.get(2), taskFloating);
	}
	
	@Test
	public void TestIncompleteSort() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskTimed = createTask("do assignment today 2pm to 3pm");
		Task taskDeadline = createTask("buy milk yesterday");
		Task taskFloating = createTask("do homework");

	    // Make 1 task completed
	    taskTimed.markAsCompleted();
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds.addComparator(new SortIncomplete());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadline);
	    assertEquals(list.get(2), taskFloating);
	    
	    uds.executeSort();
	    
	    // Test after sort
	    assertEquals(list.get(0), taskDeadline);
	    assertEquals(list.get(1), taskFloating);
	    assertEquals(list.get(2), taskTimed);
	}
}
