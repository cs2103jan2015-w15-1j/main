package main.java;

import java.util.Comparator;

public class SortIncomplete implements Comparator<Task> {
	
	private static final int FIRST_LOWER = -1;
	private static final int FIRST_HIGHER = 1;
	private static final int FIRST_SAME = 0;
	
	/* Order of comparison
	 * 
	 * COMPLETE INCOMPLETE
	 * INCOMPLETE COMPLETE
	 * SAME
	 */
	
	public int compare(Task task1, Task task2) {
		if (task1.isCompleted() && !task2.isCompleted()) {
			return FIRST_HIGHER;
		} else if (!task1.isCompleted() && task2.isCompleted()) {
			return FIRST_LOWER;
		} else {
			return FIRST_SAME;
		}
	}
}