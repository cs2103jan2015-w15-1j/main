package main.java;

import java.time.LocalTime;
import java.util.Comparator;

//@author A0121813U
/**
 * SortTime is a comparator, when used on a list object, will sort the
 * list by time (regardless of the date) in chronological order. Note that those
 * tasks with no time will be placed in front of the list.
 */
public class SortTime implements Comparator<Task> {
	
	private static final int FIRST_LOWER = -1;
	private static final int FIRST_HIGHER = 1;
	private static final int FIRST_SAME = 0;
	
	/* Order of comparison
	 * 
	 * NULL EXIST
	 * EXIST NULL
	 * NULL NULL
	 * EARLY LATER
	 * LATER EARLY
	 * SAME TIME
	 */
	
	public int compare(Task task1, Task task2) {
		
		LocalTime time1 = task1.getStartTime();
		LocalTime time2 = task2.getStartTime();
		
		if (time1 == null && time2 != null) {
			return FIRST_LOWER;
		} else if (time1 != null && time2 == null) {
			return FIRST_HIGHER;
		} else if (time1 == null && time2 == null) {
			return FIRST_SAME;
		} else if (time1.isBefore(time2)) {
			return FIRST_LOWER;
		} else if (time1.isAfter(time2)) {
			return FIRST_HIGHER;
		} else {
			return FIRST_SAME;
		}
	}
}