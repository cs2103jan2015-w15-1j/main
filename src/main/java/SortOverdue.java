package main.java;

import java.util.Comparator;

//@author A0121813U
public class SortOverdue implements Comparator<Task> {
	
	private static final int FIRST_LOWER = -1;
	private static final int FIRST_HIGHER = 1;
	private static final int FIRST_SAME = 0;
	
	/* Order of comparison
	 * 
	 * OVERDUE PENDING
	 * PENDING OVERDUE
	 * SAME
	 */
	
	public int compare (Task task1, Task task2) {
		if (task1.isOverdue() && !task2.isOverdue()) {
			return FIRST_LOWER;
		} else if (!task1.isOverdue() && task2.isOverdue()) {
			return FIRST_HIGHER;
		} else {
			return FIRST_SAME;
		}
	}
}