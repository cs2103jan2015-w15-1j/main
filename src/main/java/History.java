package main.java;

import java.util.ArrayList;
import java.util.Stack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//@author A0121813U
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
	
	// Push the arguments into their respective Stacks
	public void storeCurrentStatus(ArrayList<Task> allTasks, ObservableList<Task> displayedTasks) {
		mainStack.push(cloneState(allTasks));
		displayedStack.push(cloneState(displayedTasks));
	}
	
	// Pop the Stacks and store them in thier respective fields
	public void extractLatestStatus() {
		allTasks = mainStack.pop();
		displayedTasks = displayedStack.pop();
	}
	
	public ArrayList<Task> getAllTasks() {
		return allTasks;
	}
	
	public int getAllSize() {
		return allTasks.size();
	}
	
	public ObservableList<Task> getDisplayedTasks() {
		return displayedTasks;
	}
	
	public int getDisplayedSize() {
		return displayedTasks.size();
	}
	
	public boolean isEmpty() {
		return mainStack.empty();
	}
	
	// Push the feedback string into its Stack
	public void addFeedback(String feedback) {
		feedbackHistory.push(feedback);
	}

	// Pop the feedback string from its Stack
	public String getPreviousFeedback() {
		return feedbackHistory.pop();
	}

	// ================================================================
	// Private methods --> The two methods below are to help developers to 
	//                      create a deep copy of its arguments
	// ================================================================
	private ArrayList<Task> cloneState(ArrayList<Task> input) {
        ArrayList<Task> output = new ArrayList<Task>();
        try {
            for (Task task : input) {
                output.add(task.clone());
            }
        } catch (CloneNotSupportedException e) {
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
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(output);
    }
 }
