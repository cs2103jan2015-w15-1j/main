package main.java;

import java.util.Comparator;

//@author A0121813U
/**
 * SortType is a comparator, when used on a list object, will sort the
 * list placing those floating tasks at the front while the deadline/timed
 * tasks will be placed at the back. Note that there is no ranking between
 * deadline task and timed task.
 */
public class SortType implements Comparator<Task> {
	
	private static final int FIRST_LOWER = -1;
	private static final int FIRST_HIGHER = 1;
	private static final int FIRST_SAME = 0;
	
	/* Order of comparison
	 * 
	 * NEW! --> Now we don't need to rank between DEADLINE and TIMED
	 * 
	 * FLOATING DEADLINE
	 * DEADLINE FLOATING
	 * FLOATING TIMED
	 * TIMED FLOATING
	 * DEADLINE TIMED (Removed)
	 * TIMED DEADLINE (Removed)
	 * SAME TYPES OR DEADLINE AND TIMED (Updated)
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
		} else {
			return FIRST_SAME;
		}
	}
}