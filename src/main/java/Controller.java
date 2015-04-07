package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import main.java.Command;
import main.java.DateParser;
import main.java.Storage;
import main.java.Task;
import main.resources.view.Display;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
	
    // ================================================================
    // Fields
    // ================================================================
    // Singleton
    private static Controller controller;

    private Storage storage;

    private ArrayList<Task> allTasks;
    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    
    private History previousStates;

    private String searchArgument;
    private DateParser parser;
    private CreateTask taskCreator;

    private boolean switchDisplayToSearch = false;
    
    private UserDefinedSort userDefinedSort;
    
    private Display display;

    private Stage stage;

    // ================================================================
    // Constants
    // ================================================================
    private static final String MESSAGE_WELCOME = "Welcome to Veto! Here is an overview of the week ahead.";
    private static final String MESSAGE_ADD = "Task has been successfully added: %s";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted: %s";
    private static final String MESSAGE_DELETE_ALL = "All recurring task has been successfully deleted: %s";
    private static final String MESSAGE_EDIT = "Task has been successfully edited: %s";
    private static final String MESSAGE_EDIT_ALL = "All recurring task has been successfully edited: %s";
    private static final String MESSAGE_EDIT_INDEX_ERROR = "The task you specified could not be found.";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_COMPLETE_FAILED = "\"%s\" already completed.";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" marked as incomplete.";
    private static final String MESSAGE_UNDO = "Previous command has been undone: \"%s\"";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo.";
    private static final String MESSAGE_ALL_CLEAR = "All tasks have been deleted!";
    
    private static final String EMPTY_STRING = "";
    
    // ================================================================
 	// Constructor
 	// ================================================================
    private Controller() {
        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        taskCreator = CreateTask.getInstance();
        allTasks = storage.readFile();
        previousStates = new History();
        
        sortAllTasks();

        // Load the incomplete tasks into displayedTasks (MAIN VIEW WHEN APP STARTS)
        for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }
        
        // THIS FIXES THE SLOW ADDITION OF FIRST TASK
        parser.parse("foo today");
    }

	// Singleton pattern for Controller
	public static Controller getInstance() {
	    if (controller == null) {
	        controller = new Controller();
	    }
	    return controller;
	}

	// To load the tasks into the display on the first load
    public void onloadDisplay() {
        display.setFeedback(getWelcomeMessage());
        display.updateOverviewDisplay(displayedTasks);
    }

    // ================================================================
    // Public methods
    // ================================================================
    public String getWelcomeMessage() {
        return MESSAGE_WELCOME;
    }

    public String executeCommand(String input) {
        Command currentCommand = new Command(input);

        Command.Type commandType = currentCommand.getCommandType();
        String arguments = currentCommand.getArguments();
        String feedback = EMPTY_STRING;
        boolean helpUser = false;

        switch (commandType) {
        	
        	case SET :
	            feedback = setSaveFileDirectory(arguments);
	            break;
	        
        	case ADD :
	            saveCurrentState(input);
	            feedback = addTask(arguments);
	            switchDisplayToSearch = false;
	            break; 
	        
        	case DELETE :
	            saveCurrentState(input);
	            feedback = deleteTask(arguments);
	            break;
	        
        	case EDIT :
	            saveCurrentState(input);
	            feedback = editTask(arguments);
	            break;
	        
        	case DISPLAY :
                displayTask(arguments);
	            break;
	        
        	case COMPLETE :
	            saveCurrentState(input);
	            feedback = completeTask(arguments);
	            break;
	        
        	case INCOMPLETE :
	            saveCurrentState(input);
                feedback = incompleteTask(arguments);
	            break;
	        
        	case UNDO :
	            feedback = undo();
	            break;
	        
        	case SEARCH :
	            search(arguments);
                searchArgument = arguments;
	            switchDisplayToSearch = true;
	            break;
	        
        	case CLEAR :
	        	saveCurrentState(input);
	        	feedback = clear();
	        	break;
	        
        	case INVALID :
	            feedback = invalid();
	            break;
	        
        	case HELP :
	        	helpUser = true;
	        	break;
	        
        	case EXIT :
	        	exit();
                stage.hide();
	            break;
        }
        showAppropriateDisplay(helpUser);
        display.setFeedback(feedback);

        return feedback;
    }

    // ================================================================
    // Initialization methods
    // ================================================================

    public void setDisplay(Display display) {
	    this.display = display;
	}
    
    public void setStage(Stage stage) {
	    this.stage = stage;
	}

	// ================================================================
	// Getters
	// ================================================================
	
	public ObservableList<Task> getDisplayedTasks() {
	    return displayedTasks;
	}

    private ArrayList<Task> getIncompleteTasks(ArrayList<Task> allTasks) {
        List<Task> incompleteTasks = allTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
        return (ArrayList<Task>) incompleteTasks;
    }

    private List<Task> getCompletedTasks(ArrayList<Task> allTasks) {
        List<Task> completedTasks = allTasks.stream()
                .filter(task -> task.isCompleted())
                .collect(Collectors.toList());
        return completedTasks;
    } 

    // ================================================================
    // Logic methods
    // ================================================================

    private String addTask(String input) {
        if (input.isEmpty()) {
            return MESSAGE_INVALID_COMMAND;
        }
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String notParsedWords = parser.getNotParsedWords();
        ArrayList<Task> newTask = new ArrayList<Task>();

        // Instantiate a new Task object
        newTask = taskCreator.create(input, parsedDates, parsedWords, notParsedWords);

        Task task = newTask.get(0);
        allTasks.addAll(newTask);
        updateStorageWithAllTasks();
        
        return String.format(MESSAGE_ADD, task);      
    }

    /**
     *
     * Deletes the task with the selected index. Replaces it with a new task by calling addTask
     * with the extracted arguments.
     *
     */
    private String editTask(String input) {
        String[] inputArray;
        int editIndex;

        // Check if it's an edit all
        if (input.toLowerCase().contains("all")) {
            input = input.toLowerCase().replace("all", "").trim();
            return editAllTasks(input);
        }

        try {
            // Get the index from input
            inputArray = input.split(" ");
            editIndex = Integer.parseInt(inputArray[0]) - 1;

            // Delete the task
            Task task = displayedTasks.remove(editIndex);
            allTasks.remove(task);

            // Creates an input to addTask
            String[] addArgumentArray =  new String[inputArray.length - 1];
            System.arraycopy(inputArray, 1, addArgumentArray, 0, inputArray.length - 1);
            String addArgument = String.join(" ", addArgumentArray);
            System.out.println(addArgument);

            addTask(addArgument);
            return String.format(MESSAGE_EDIT, task);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return MESSAGE_EDIT_INDEX_ERROR;
        }
    }

    private void updateIndividualTask(Task task, String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String notParsedWords = parser.getNotParsedWords();

        task.update(input, parsedDates, parsedWords, notParsedWords);
    }

    // Use this overloaded version or editAllTasks to prevent parsing n times,
    // where n is the number of recurring tasks to edit
    private void updateIndividualTask(Task task, String input, ArrayList<LocalDateTime> parsedDates,
                                      String parsedWords, String notParsedWords) {
        task.update(input, parsedDates, parsedWords, notParsedWords);
    }

    private String editAllTasks(String input) {
        String[] inputArray;
        int editIndex;
        String recurringId;

        try {
            inputArray = input.split(" ");
            editIndex = Integer.parseInt(inputArray[0]) - 1;
            Task task = displayedTasks.get(editIndex);

            recurringId = task.getID();

            if (recurringId.equals("NA")) {
                return MESSAGE_INVALID_COMMAND;
            } else {
                parser.parse(input);
                ArrayList<LocalDateTime> parsedDates = parser.getDates();
                String parsedWords = parser.getParsedWords();
                String notParsedWords = parser.getNotParsedWords();

                for (Task editedTask : allTasks) {
                    updateIndividualTask(editedTask, input, parsedDates, parsedWords, notParsedWords);
                }
                updateStorageWithAllTasks();
            }
            return String.format(MESSAGE_EDIT_ALL, task);
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        ArrayList<Task> tasksToRemove = new ArrayList<Task>();
        Boolean isToDeleteAll = false;
        String recurringID;
        
        if (input.toLowerCase().contains("all")) {
            isToDeleteAll = true;
            input = input.toLowerCase().replace("all", "").trim();
        }
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task removeTask = displayedTasks.get(removalIndex);
            if (isToDeleteAll) {
                recurringID = removeTask.getID();
                if (recurringID.equals("NA")) {
                    return MESSAGE_INVALID_COMMAND;
                }
                for (Task task : allTasks) {
                    if (task.getID().equals(recurringID)) {
                        tasksToRemove.add(task);
                    }
                }
            } else {
                tasksToRemove.add(removeTask);
            }
            
            displayedTasks.removeAll(tasksToRemove);
            allTasks.removeAll(tasksToRemove);
            updateStorageWithAllTasks();
            
            if (isToDeleteAll) {
                return String.format(MESSAGE_DELETE_ALL, removeTask.getDescription());
            }
            return String.format(MESSAGE_DELETE, removeTask);
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String completeTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            
            if (task.isCompleted()) {
                return String.format(MESSAGE_COMPLETE_FAILED, task.getDescription());
            }
            
            task.markAsCompleted();

            updateStorageWithAllTasks();

            return String.format(MESSAGE_COMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String incompleteTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            task.markAsIncomplete();

            updateStorageWithAllTasks();

            return String.format(MESSAGE_INCOMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String undo() {
        if (previousStates.isEmpty()) {
            return MESSAGE_NO_UNDO;
        } else {
        	previousStates.extractLatestStatus();
            allTasks = previousStates.getAllTasks();
            displayedTasks = previousStates.getDisplayedTasks();
            
            updateStorageWithAllTasks();
            
            if (switchDisplayToSearch) {
            	search(searchArgument);
            }
            return String.format(MESSAGE_UNDO, previousStates.getPreviousFeedback());
        }
    }

    private void search(String input) {
        displayedTasks.clear();
        parser.parse(input);
        ArrayList<LocalDateTime> searchDate = parser.getDates();

        for (Task task : allTasks) {
            String taskInfo = task.getDescription().toLowerCase();
            if (taskInfo.contains(input.toLowerCase())) {
                displayedTasks.add(task);
            } else if (searchDate.size() > 0
                    && searchDate.get(0).toLocalDate().equals(task.getDate())) {
                displayedTasks.add(task);
            }
        }
    }

    private void displayTask(String input) {
        displayedTasks.clear();

        if (input.equals("completed")) {
            switchDisplayToSearch = true;
            searchArgument = input;
            updateDisplayWithCompleted();
        } else {
            switchDisplayToSearch = false;
            searchArgument = null;
        }
    }

    private String invalid() {
        return MESSAGE_INVALID_COMMAND;
    }
    
    private String setSaveFileDirectory(String input) {
        return storage.setSaveFileDirectory(input);
    }
    
    private String clear() {
        allTasks = new ArrayList<Task>();
        displayedTasks = FXCollections.observableArrayList();;
        storage.updateFiles(allTasks);
        return MESSAGE_ALL_CLEAR;
    }

    private void exit() {
        updateStorageWithAllTasks();
    }

    // ================================================================
    // Utility methods
    // ================================================================
    
    private void showAppropriateDisplay(boolean helpUser) {
    	if (helpUser) {
        	updateHelpDisplay();
        } else if (switchDisplayToSearch) {
            updateDisplaySearch();
        } else {
        	sortAllTasks();
            updateDisplayWithDefault();
        }
    }
    
    private void updateDisplayWithDefault() {
        displayedTasks.setAll(getIncompleteTasks(allTasks));
        display.updateOverviewDisplay(displayedTasks);
    }

    private void updateDisplayWithCompleted() {
        displayedTasks.setAll(getCompletedTasks(allTasks));
        display.updateOverviewDisplay(displayedTasks);
    }

    private void updateDisplaySearch() {
    	sortSearchedTasks();
        display.updateSearchDisplay(displayedTasks, searchArgument);
    }
    
    private void updateHelpDisplay() {
    	display.showHelpDisplay();
;    }

    private void sortAllTasks() {
    	userDefinedSort = new UserDefinedSort(allTasks);
    	userDefinedSort.addComparator(new SortType());
    	userDefinedSort.addComparator(new SortDate());
        userDefinedSort.addComparator(new SortOverdue());   
        allTasks = userDefinedSort.executeSort();
    }
    
    private void sortSearchedTasks() {
    	userDefinedSort = new UserDefinedSort(new ArrayList<Task>(displayedTasks));
        userDefinedSort.addComparator(new SortType());
        userDefinedSort.addComparator(new SortDate());
        userDefinedSort.addComparator(new SortOverdue());
        userDefinedSort.addComparator(new SortIncomplete());
        userDefinedSort.executeSort();
        displayedTasks = FXCollections.observableArrayList(userDefinedSort.getList());
    }

    private void updateStorageWithAllTasks() {
        storage.updateFiles(allTasks);
    }

    private void saveCurrentState(String input) {
    	previousStates.storeCurrentStatus(allTasks, displayedTasks);
        previousStates.addFeedback(input);
    }

    // ================================================================
    // Testing methods
    // ================================================================

    public List<Task> getIncompleteTasksPublic() {
        return getIncompleteTasks(allTasks);
    }

    public List<Task> getCompleteTasksPublic() {
        return getCompletedTasks(allTasks);
    }
}
