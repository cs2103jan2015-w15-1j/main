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
	    nonParsedWords = dp.getNonParsedWords();
	    return new Task(input, parsedDates, parsedWords, nonParsedWords);
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
	    taskTimed.markAsComplete();
	    
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
	
	@Test
	public void TestAllCombined() {
		
		list = new ArrayList<Task>();
		uds = new UserDefinedSort(list);
		
		Task taskTimed1 = createTask("do homework 2pm to 3pm on friday");
		Task taskDeadline1 = createTask("buy milk tomorrow");
		Task taskFloating1 = createTask("do homework");
		Task taskTimed2 = createTask("watch movie with friends today 5pm to 8pm");
		Task taskDeadline2 = createTask("buy present today");
		Task taskFloating2 = createTask("go to school");
		Task taskTimed3 = createTask("exam from 8am to 10am on Thursday");
		Task taskDeadline3 = createTask("teach tuition yesterday");
		Task taskFloating3 = createTask("remember to drink water");
		Task taskTimed4 = createTask("got meeting yesterday at 10am to 12pm");
		Task taskDeadline4 = createTask("buy cereal 28 march");
		Task taskFloating4 = createTask("float to swim");
		Task taskTimed5 = createTask("IPPT to attend 23 april 1pm to 2pm");
		Task taskDeadline5 = createTask("do something tomorrow");
		Task taskFloating5 = createTask("do shit");
		
		taskTimed2.markAsComplete();
		taskDeadline1.markAsComplete();
		taskFloating5.markAsComplete();
		taskFloating4.markAsComplete();
		taskDeadline3.markAsComplete();
		taskTimed5.markAsComplete();
		
		list.add(taskTimed1);        // do homework 2pm to 3pm 3 days later
	    list.add(taskDeadline1);     // buy milk tomorrow (COMPLETED)
	    list.add(taskFloating1);     // do homework\
	    list.add(taskTimed2);        // watch movie with friends today 5pm to 8pm (COMPLETED)
	    list.add(taskDeadline2);     // buy present today
	    list.add(taskFloating2);     // go to school\
	    list.add(taskTimed3);        // exam from 8am to 10am next week
	    list.add(taskDeadline3);     // teach tuition 2 days later (COMPLETED)
	    list.add(taskFloating3);     // remember to drink water\
	    list.add(taskTimed4);        // got meeting yesterday at 10am to 12pm
	    list.add(taskDeadline4);     // buy cereal 2 days ago
	    list.add(taskFloating4);     // float to swim (COMPLETED)
	    list.add(taskTimed5);        // IPPT to attend next week 1pm to 2pm (COMPLETED)
	    list.add(taskDeadline5);     // do something next 2 days
	    list.add(taskFloating5);     // do shit (COMPLETED)
	    
	    uds.addComparator(new SortType());
	    uds.addComparator(new SortOverdue());
	    uds.addComparator(new SortDate());
	    uds.addComparator(new SortIncomplete());
		
	    assertEquals(list.get(0), taskTimed1);
	    assertEquals(list.get(1), taskDeadline1);
	    assertEquals(list.get(2), taskFloating1);
	    assertEquals(list.get(3), taskTimed2);
	    assertEquals(list.get(4), taskDeadline2);
	    assertEquals(list.get(5), taskFloating2);
	    assertEquals(list.get(6), taskTimed3);
	    assertEquals(list.get(7), taskDeadline3);
	    assertEquals(list.get(8), taskFloating3);
	    assertEquals(list.get(9), taskTimed4);
	    assertEquals(list.get(10), taskDeadline4);
	    assertEquals(list.get(11), taskFloating4);
	    assertEquals(list.get(12), taskTimed5);
	    assertEquals(list.get(13), taskDeadline5);
	    assertEquals(list.get(14), taskFloating5);
	    
	    uds.executeSort();
	    
	    assertEquals(list.get(0), taskFloating1);
	    assertEquals(list.get(1), taskFloating2);
	    assertEquals(list.get(2), taskFloating3);
	    assertEquals(list.get(3), taskTimed2);
	    assertEquals(list.get(4), taskDeadline2);
	    assertEquals(list.get(5), taskFloating2);
	    assertEquals(list.get(6), taskTimed3);
	    assertEquals(list.get(7), taskDeadline3);
	    assertEquals(list.get(8), taskFloating3);
	    assertEquals(list.get(9), taskTimed4);
	    assertEquals(list.get(10), taskDeadline4);
	    assertEquals(list.get(11), taskFloating4);
	    assertEquals(list.get(12), taskTimed5);
	    assertEquals(list.get(13), taskDeadline5);
	    assertEquals(list.get(14), taskFloating5);
		
	}
}
