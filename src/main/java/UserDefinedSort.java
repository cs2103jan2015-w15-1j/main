package main.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

//@author A0121813U
/**
 * UserDefinedSort (UDS) is used to sort a list object comparator by comparator
 * It is important to note that the different order of the comparator in the 
 * field "chain" may affect the outcome of the list. In other words, different 
 * sequence of comparator may result in different sequence in the list.
 */
public class UserDefinedSort {

	private ArrayList<Comparator<Task>> chain;
	private ArrayList<Task> list;
	private Logger logger;
	
	public UserDefinedSort(ArrayList<Task> list) {
		logger = Logger.getLogger("UDS");
        logger.setLevel(Level.OFF);
		this.list = list;
		chain = new ArrayList<Comparator<Task>>();
	}
	
	public ArrayList<Task> getList() {
		return list;
	}
	
	// Add a comparator object in the "chain" field for future use
	public void addComparator(Comparator<Task> comparator) {
		assert comparator != null;
		chain.add(comparator);
	}
	
	// Execute the sort based on the order of comparator in the chain
	public ArrayList<Task> executeSort() {
		assert list != null;
		logger.log(Level.INFO, "Before sort: " + list);
		for (Comparator<Task> comparator: chain) {
			Collections.sort(list, comparator);
		}
		logger.log(Level.INFO, "After sort: " + list);
		return list;
	}
}