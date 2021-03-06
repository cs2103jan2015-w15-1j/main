package main.java;

import java.time.LocalDate;
import java.util.Comparator;

//@author A0121813U
/**
 * SortDate is a comparator, when used on a list object, will sort the list by date
 * in chronological order. Note that Task with no dates will be placed infront
 * of the list.
 */
public class SortDate implements Comparator<Task> {
	
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
	 * SAME DAY
	 */
	
	public int compare(Task task1, Task task2) {
		
		LocalDate date1 = task1.getDate();
		LocalDate date2 = task2.getDate();
		
		if (date1 == null && date2 != null) {
			return FIRST_LOWER;
		} else if (date1 != null && date2 == null) {
			return FIRST_HIGHER;
		} else if (date1 == null && date2 == null) {
			return FIRST_SAME;
		} else if (date1.isBefore(date2)) {
			return FIRST_LOWER;
		} else if (date1.isAfter(date2)) {
			return FIRST_HIGHER;
		} else {
			return FIRST_SAME;
		}
	}
}