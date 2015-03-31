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

	@Test
	public void TestTypeSort() {
		
		ArrayList<LocalDateTime> parsedDates;
		String parsedWords;
		String nonParsedWords;
		ArrayList<Task> list = new ArrayList<Task>();
		UserDefinedSort uds;
		
		String timed = "do assignment today 2pm to 3pm";
		String deadline = "buy milk tomorrow";
		String floating = "do homework";
		
		DateParser dp = DateParser.getInstance();
		
		dp.parse(timed);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	    
	    Task taskTimed = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	     
	    dp.parse(deadline);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskDeadline = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    dp.parse(floating);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskFloating = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds = new UserDefinedSort(list);
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
		ArrayList<LocalDateTime> parsedDates;
		String parsedWords;
		String nonParsedWords;
		ArrayList<Task> list = new ArrayList<Task>();
		UserDefinedSort uds;
		
		String timed = "do assignment tomorrow";
		String deadline = "buy milk today";
		//String floating = "do homework";
		
		DateParser dp = DateParser.getInstance();
		
		dp.parse(timed);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	    
	    Task taskTimed = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	     
	    dp.parse(deadline);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskDeadline = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
//	    dp.parse(floating);
//	    parsedDates = dp.getDates();
//	    parsedWords = dp.getParsedWords();
//	    nonParsedWords = dp.getNonParsedWords();
//	     
//	    Task taskFloating = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    //list.add(taskFloating);
	    
	    uds = new UserDefinedSort(list);
	    uds.addComparator(new SortDate());
	    
	    // Test before sort
	    assertEquals(list.get(0), taskTimed);
	    assertEquals(list.get(1), taskDeadline);
	   // assertEquals(list.get(2), taskFloating);
	    
	    System.out.println(uds.getList());

	    
	    uds.executeSort();
	    
	    System.out.println(uds.getList());
	    
	    // Test after sort
	    assertEquals(list.get(0), taskDeadline);
	    assertEquals(list.get(1), taskTimed);
	    //assertEquals(list.get(2), taskTimed);
	}
	
	@Test
	public void TestOverdueSort() {
		ArrayList<LocalDateTime> parsedDates;
		String parsedWords;
		String nonParsedWords;
		ArrayList<Task> list = new ArrayList<Task>();
		UserDefinedSort uds;
		
		String timed = "do assignment today 2pm to 3pm";
		String deadline = "buy milk yesterday";
		String floating = "do homework";
		
		DateParser dp = DateParser.getInstance();
		
		dp.parse(timed);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	    
	    Task taskTimed = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	     
	    dp.parse(deadline);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskDeadline = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    dp.parse(floating);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskFloating = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds = new UserDefinedSort(list);
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
		ArrayList<LocalDateTime> parsedDates;
		String parsedWords;
		String nonParsedWords;
		ArrayList<Task> list = new ArrayList<Task>();
		UserDefinedSort uds;
		
		String timed = "do assignment today 2pm to 3pm";
		String deadline = "buy milk yesterday";
		String floating = "do homework";
		
		DateParser dp = DateParser.getInstance();
		
		dp.parse(timed);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	    
	    Task taskTimed = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	     
	    dp.parse(deadline);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskDeadline = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    dp.parse(floating);
	    parsedDates = dp.getDates();
	    parsedWords = dp.getParsedWords();
	    nonParsedWords = dp.getNonParsedWords();
	     
	    Task taskFloating = new Task(timed, parsedDates, parsedWords, nonParsedWords);
	    
	    // Make 1 task completed
	    taskTimed.markAsComplete();
	    
	    list.add(taskTimed);
	    list.add(taskDeadline);
	    list.add(taskFloating);
	    
	    uds = new UserDefinedSort(list);
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
