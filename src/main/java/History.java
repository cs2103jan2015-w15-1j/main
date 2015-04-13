package main.java;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//@author A0121813U
/**
 * This class helps to store all of the previous states of the ArrayList and ObservableList
 * in the controller as a stack. This is to allow the command, "undo" to take place.
 * History class stores for 3 different fields:
 * ArrayList<Task> allTasks, ObservableList<Task> displayedTasks, String feedback
 * 
 * Basically it works like a normal Stack object. The only difference is that it manages 3 Stack objects 
 * altogether
 */
public class History {

	// ================================================================
	// Fields
	// ================================================================
	private static Logger logger;
	
	private Stack<ArrayList<Task>> mainStack;
	private Stack<ObservableList<Task>> displayedStack;
	
	private ArrayList<Task> allTasks;
	private ObservableList<Task> displayedTasks;

	private Stack<String> commandHistory;

	// ================================================================
	// Constructor
	// ================================================================
	public History() {
		logger = Logger.getLogger("History");
		logger.setLevel(Level.OFF);
		mainStack = new Stack<ArrayList<Task>>();
		displayedStack = new Stack<ObservableList<Task>>();
		commandHistory = new Stack<String>();
	}

	// ================================================================
	// Public methods
	// ================================================================
	
	// Push the arguments into their respective Stacks
	public void storeCurrentState(ArrayList<Task> allTasks, ObservableList<Task> displayedTasks) {
		logger.log(Level.INFO, "stack size before push: " + mainStack.size() + ", " + displayedStack.size());
		mainStack.push(cloneState(allTasks));
		displayedStack.push(cloneState(displayedTasks));
		assert !mainStack.empty();
		assert !displayedStack.empty();
	}
	
	// Pop the Stacks and store them in thier respective fields
	public void getPreviousState() {
		logger.log(Level.INFO, "stack size before pop: " + mainStack.size() + ", " + displayedStack.size());
		try {
			allTasks = mainStack.pop();
			displayedTasks = displayedStack.pop();
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
 	}
	
	// Return the allTasks field.
	public ArrayList<Task> getAllTasks() {
		assert allTasks != null;
		return allTasks;
	}
	
	// Return the number of elements in the allTasks field. Wont be called if empty
	public int getAllSize() {
		assert allTasks.size() >= 0;
		return allTasks.size();
	}
	
	// Return the displayedTasks field.
	public ObservableList<Task> getDisplayedTasks() {
		assert displayedTasks != null;
		return displayedTasks;
	}
	
	// Return the number of elements in the displayedTasks field. Wont be called if empty
	public int getDisplayedSize() {
		assert displayedTasks.size() >= 0;
		return displayedTasks.size();
	}
	
	public boolean isEmpty() {
		return mainStack.empty();
	}
	
	// Push the feedback string into its Stack
	public void storeCommand(String feedback) {
		logger.log(Level.INFO, "stack size before push: " + commandHistory.size());
		commandHistory.push(feedback);
		assert !commandHistory.empty();
	}

	// Pop the feedback string from its Stack
	public String getPreviousCommand() {
		logger.log(Level.INFO, "stack size before pop: " + commandHistory.size());
		String previousCommand = null;
		try {
			previousCommand = commandHistory.pop();
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
		return previousCommand;
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
