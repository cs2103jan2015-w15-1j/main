package main.java;

import java.util.ArrayList;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class History {

	// ================================================================
	// Fields
	// ================================================================
	private Stack<ArrayList<Task>> mainStack;
	private Stack<ObservableList<Task>> displayedStack;
	
	private ArrayList<Task> allTasks;
	private ObservableList<Task> displayedTasks;

	private Stack<String> feedbackHistory;

	// ================================================================
	// Constructor
	// ================================================================
	public History() {
		mainStack = new Stack<ArrayList<Task>>();
		displayedStack = new Stack<ObservableList<Task>>();
		feedbackHistory = new Stack<String>();
	}

	// ================================================================
	// Public methods
	// ================================================================
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

	public void addFeedback(String feedback) {
		feedbackHistory.push(feedback);
	}

	public String getPreviousFeedback() {
		return feedbackHistory.pop();
	}

	// ================================================================
	// Private methods
	// ================================================================
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
