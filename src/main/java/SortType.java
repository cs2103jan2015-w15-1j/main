package main.java;

import java.util.Comparator;

public class SortType implements Comparator<Task> {
	
	private static final int FIRST_LOWER = -1;
	private static final int FIRST_HIGHER = 1;
	private static final int FIRST_SAME = 0;
	
	/* Order of comparison
	 * 
	 * FLOATING DEADLINE
	 * DEADLINE FLOATING
	 * FLOATING TIMED
	 * TIMED FLOATING
	 * DEADLINE TIMED
	 * TIMED DEADLINE
	 * SAME TYPES
	 */ 
	
	public int compare(Task task1, Task task2) {
		if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.DEADLINE) {
			return FIRST_LOWER;
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.FLOATING) {
			return FIRST_HIGHER;
		} else if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.TIMED) {
			return FIRST_LOWER;
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.FLOATING) {
			return FIRST_HIGHER;
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.TIMED) {
			return FIRST_LOWER;
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.DEADLINE) {
			return FIRST_HIGHER;
		} else {
			return FIRST_SAME;
		}
	}
}