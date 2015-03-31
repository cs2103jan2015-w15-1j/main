package main.java;

import java.time.LocalDate;
import java.util.Comparator;

public class SortDate implements Comparator<Task> {
	
	/*
	 * NULL EXIST
	 * EXIST NULL
	 * NULL NULL
	 * EXIST EXIST
	 * EARLY LATER
	 * LATER EARLY
	 * SAME DAY
	 */
	
	public int compare(Task task1, Task task2) {
		
		LocalDate date1 = task1.getDate();
		LocalDate date2 = task2.getDate();
		
		if (date1 == null && date2 != null) {
			return -1;
		} else if (date1 != null && date2 == null) {
			return 1;
		} else if (date1 == null && date2 == null) {
			return 0;
		} else if (date1.isBefore(date2)) {
			return -1;
		} else if (date1.isAfter(date2)) {
			return 1;
		} else {
			return 0;
		}
	}
}