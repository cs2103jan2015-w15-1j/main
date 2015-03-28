package main.java;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserDefinedSort {

	private ArrayList<Comparator<Task>> chain;
	private ArrayList<Task> list;
	
	public UserDefinedSort(ArrayList<Task> list) {
		this.list = list;
	}
	
	public ArrayList<Task> getList() {
		return list;
	}
	
	public void addComparator(Comparator<Task> comparator) {
		chain.add(comparator);
	}
	
	public void executeSort() {
		for (Comparator<Task> comparator: chain) {
			Collections.sort(list, comparator);
		}
	}
}

class SortType implements Comparator<Task> {
	
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

class SortOverdue implements Comparator<Task> {
	
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

class SortDate implements Comparator<Task> {
	
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
		} else if (date1 != null && date2 != null) {
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

class SortIncomplete implements Comparator<Task> {
	
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
