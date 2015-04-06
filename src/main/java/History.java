package main.java;

import java.util.ArrayList;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class History {
	
	private Stack<ArrayList<Task>> mainStack;
	private Stack<ObservableList<Task>> displayedStack;
	
	private ArrayList<Task> allTasks;
	private ObservableList<Task> displayedTasks;
	
	public History() {
		mainStack = new Stack<ArrayList<Task>>();
		displayedStack = new Stack<ObservableList<Task>>();
	}
	
	public void storeCurrentStatus(ArrayList<Task> allTasks, ObservableList<Task> displayedTasks) {
		mainStack.push(cloneState(allTasks));
		displayedStack.push(cloneState(displayedTasks));
	}
	
	public void extractLatestStatus() {
		allTasks = mainStack.pop();
		displayedTasks = displayedStack.pop();
	}
	
	public ArrayList<Task> getAllTasks() {
		return allTasks;
	}
	
	public ObservableList<Task> getDisplayedTasks() {
		return displayedTasks;
	}
	
	public boolean isEmpty() {
		return mainStack.empty();
	}
	
	private ArrayList<Task> cloneState(ArrayList<Task> input) {
        ArrayList<Task> output = new ArrayList<Task>();
        try {
            for (Task task : input) {
                output.add(task.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    
    private ObservableList<Task> cloneState(ObservableList<Task> input) {
        ArrayList<Task> output = new ArrayList<Task>();
        try {
            for (Task task : input) {
                output.add(task.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(output);
    }
 }
