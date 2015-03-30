package main.java;

import java.util.Comparator;

public class SortType implements Comparator<Task> {
	
	/* 
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
			return -1;
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.FLOATING) {
			return 1;
		} else if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.TIMED) {
			return -1;
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.FLOATING) {
			return 1;
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.TIMED) {
			return -1;
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.DEADLINE) {
			return 1;
		} else {
			return 0;
		}
	}
}