package main.java;

import java.time.LocalDate;
import java.util.Comparator;

public class SortDefault implements Comparator<Task> {
	
	private static final int FIRST_SMALLER = -1;
	private static final int FIRST_BIGGER = 1;
	private static final int FIRST_SAME = 0;
	
	/** SortDefault will sort the list of tasks into the default sorting which is:
	 * 1) All Floating tasks
	 * 2) All overdue tasks
	 * 3) The remaining will follow sorting according to day
	 * 
	 */
	
	
	public int compare(Task task1, Task task2) {
		// First is to compare two Tasks of the same type
		
		// Both floating means they are of the same value
		if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.FLOATING) {
			return FIRST_SAME;
			
		// If both deadline, 1) Check if any ONE of them is overdue
		//				     2) If both overdue or both not overdue, check the dates
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.DEADLINE) {
			if (task1.isOverdue() && !task2.isOverdue()) {
				return FIRST_SMALLER;
			} else if (!task1.isOverdue() && task2.isOverdue()) {
				return FIRST_BIGGER;
			} else {
				LocalDate date1 = task1.getDate();
				LocalDate date2 = task1.getDate();
				if (date1.isBefore(date2)) {
					return FIRST_SMALLER;
				} else if (date1.isAfter(date2)) {
					return FIRST_BIGGER;
				} else {
					return FIRST_SAME;
				}
			}
			
		// If both timed, 1) Check if any ONE of them is overdue
		//				  2) If both overdue or both not overdue, check the dates
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.TIMED) {
			if (task1.isOverdue() && !task2.isOverdue()) {
				return FIRST_SMALLER;
			} else if (!task1.isOverdue() && task2.isOverdue()) {
				return FIRST_BIGGER;
			} else {
				LocalDate date1 = task1.getDate();
				LocalDate date2 = task1.getDate();
				if (date1.isBefore(date2)) {
					return FIRST_SMALLER;
				} else if (date1.isAfter(date2)) {
					return FIRST_BIGGER;
				} else {
					return FIRST_SAME;
				}
			}
			
		// Second is to compare floating and deadline, floating and timed
		} else if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.DEADLINE) {
			return FIRST_SMALLER;
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.FLOATING) {
			return FIRST_BIGGER;
		} else if (task1.getType() == Task.Type.FLOATING && task2.getType() == Task.Type.TIMED) {
			return FIRST_SMALLER;
		} else if (task1.getType() == Task.Type.TIMED && task2.getType() == Task.Type.FLOATING) {
			return FIRST_BIGGER;
			
		// Third is to compare deadline and timed
		// 1) Check if any one of them is overdue
		// 2) If both overdue or both not overdue, check the dates
		// 3) If same dates, deadline is above timed
		} else if (task1.getType() == Task.Type.DEADLINE && task2.getType() == Task.Type.TIMED) {
			if (task1.isOverdue() && !task2.isOverdue()) {
				return FIRST_SMALLER;
			} else if (!task1.isOverdue() && task2.isOverdue()) {
				return FIRST_BIGGER;
			} else {
				LocalDate date1 = task1.getDate();
				LocalDate date2 = task1.getDate();
				if (date1.isBefore(date2)) {
					return FIRST_SMALLER;
				} else if (date1.isAfter(date2)) {
					return FIRST_BIGGER;
				} else {
					return FIRST_SMALLER;
				}
			}
		} else {
			if (task1.isOverdue() && !task2.isOverdue()) {
				return FIRST_SMALLER;
			} else if (!task1.isOverdue() && task2.isOverdue()) {
				return FIRST_BIGGER;
			} else {
				LocalDate date1 = task1.getDate();
				LocalDate date2 = task1.getDate();
				if (date1.isBefore(date2)) {
					return FIRST_SMALLER;
				} else if (date1.isAfter(date2)) {
					return FIRST_BIGGER;
				} else {
					return FIRST_BIGGER;
				}
			}
		}
	}
	

}
