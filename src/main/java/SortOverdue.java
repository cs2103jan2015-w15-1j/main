package main.java;

import java.util.Comparator;

public class SortOverdue implements Comparator<Task> {
	
	/*
	 * OVERDUE PENDING
	 * PENDING OVERDUE
	 * SAME
	 */
	
	public int compare (Task task1, Task task2) {
		if (task1.isOverdue() && !task2.isOverdue()) {
			return -1;
		} else if (!task1.isOverdue() && task2.isOverdue()) {
			return 1;
		} else {
			return 0;
		}
	}
}