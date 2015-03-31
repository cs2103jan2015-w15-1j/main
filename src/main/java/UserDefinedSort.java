package main.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserDefinedSort {

	private ArrayList<Comparator<Task>> chain;
	private ArrayList<Task> list;
	
	public UserDefinedSort(ArrayList<Task> list) {
		this.list = list;
		chain = new ArrayList<Comparator<Task>>();
	}
	
	public ArrayList<Task> getList() {
		return list;
	}
	
	public void addComparator(Comparator<Task> comparator) {
		chain.add(comparator);
	}
	
	public ArrayList<Task> executeSort() {
		for (Comparator<Task> comparator: chain) {
			Collections.sort(list, comparator);
		}

		return list;
	}
}