package main.java;

import java.util.Comparator;

public class SortIncomplete implements Comparator<Task> {
	
	/*
	 * COMPLETE INCOMPLETE
	 * INCOMPLETE COMPLETE
	 * SAME
	 */
	
	public int compare(Task task1, Task task2) {
		if (task1.isCompleted() && !task2.isCompleted()) {
			return 1;
		} else if (!task1.isCompleted() && task2.isCompleted()) {
			return -1;
		} else {
			return 0;
		}
	}
}