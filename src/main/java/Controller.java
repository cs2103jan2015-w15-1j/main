package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import main.resources.view.DisplayController;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import main.resources.view.DisplayController;

//@author A0122081X
public class Controller {
	
    // ================================================================
    // Fields
    // ================================================================
    // Singleton
    private static Controller controller;

    private Logger logger;

    private Storage storage;

    private ArrayList<Task> allTasks;
    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    
    private History previousStates;

    private String searchArgument;
    private DateParser parser;
    private CreateTask taskCreator;

    private boolean switchDisplayToSearch = false;
    
    private UserDefinedSort userDefinedSort;

    private Stage stage;

    // For testing purposes ONLY, un-comment the following line. Comment it for deployment.
//    private DisplayControllerStub displayController = DisplayControllerStub.getInstance();

    // For deployment purposes, un-comment the following line. Comment it for JUnit testing.
    private DisplayController displayController = DisplayController.getInstance();

    // ================================================================
    // Constants
    // ================================================================
    private static final String MESSAGE_WELCOME = "Welcome to Veto! Here is an overview of the week ahead.";
    private static final String MESSAGE_ADD = "Task has been successfully added: %s";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted: %s";
    private static final String MESSAGE_DELETE_ALL = "All recurring task has been successfully deleted: %s";
    private static final String MESSAGE_EDIT = "Task has been successfully edited: %s";
    private static final String MESSAGE_EDIT_ALL = "All recurring task has been successfully edited: %s";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_COMPLETE_FAILED = "\"%s\" already completed.";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" marked as incomplete.";
    private static final String MESSAGE_UNDO = "Previous command has been undone: \"%s\"";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo.";
    private static final String MESSAGE_ALL_CLEAR = "All tasks have been deleted!";
    private static final String MESSAGE_TASK_INDEX_ERROR = "The task you specified could not be found.";
    private static final String MESSAGE_SAVE_SET = "File save destination has been confirmed.";
    private static final String MESSAGE_SAVE_SET_FAIL = "File save destination failed.";
    private static final String MESSAGE_SAVE_MOVE = "Save file has been moved.";
    private static final String MESSAGE_SAVE_MOVE_FAIL = "Moving save file failed.";
    private static final String MESSAGE_NON_CHRONO_DATES = "Task was not created as %s";
   
    private static final String STRING_EMPTY = "";
    private static final String STRING_SPACE = " ";
    private static final String STRING_ALL = "all";
    private static final String STRING_COMPLETED = "completed";

    
    // ================================================================
 	// Constructor
 	// ================================================================
    private Controller() {
        logger = Logger.getLogger("Display");
        logger.setLevel(Level.OFF);

        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        taskCreator = CreateTask.getInstance();
        allTasks = storage.readFile();
        previousStates = new History();       
        sortAllTasks();
        loadIncompleteTasks();
        warmUpParser();
    }

	// Singleton pattern for Controller
	public static Controller getInstance() {
	    if (controller == null) {
	        controller = new Controller();
	        controller.initFirstDisplay();
	    }
	    return controller;
	}

	// To load the tasks into the display on the first load
    private void initFirstDisplay() {
        displayController.setFeedback(getWelcomeMessage());
        displayController.updateOverviewDisplay(displayedTasks);
    }

    // ================================================================
    // Public methods
    // ================================================================
    public String getWelcomeMessage() {
        return MESSAGE_WELCOME;
    }

    //@author A0121813U
    // Based on what the user has type, this method will call the respective methods
    public String executeCommand(String input) {
        Command currentCommand = new Command(input);
        Command.Type commandType = currentCommand.getCommandType();
        String arguments = currentCommand.getArguments();
        String feedback = STRING_EMPTY;
        boolean helpUser = false;

        logger.log(Level.INFO, "User's input: " + input);
        logger.log(Level.INFO, "Type of command: " + commandType.toString());
        logger.log(Level.INFO, "Arguments: " + arguments);

        switch (commandType) {
        	
        	case SET :
	            feedback = setSaveFileDirectory(arguments);
	            break;
	            
            case MOVE :
                feedback = moveSaveFileDirectory(arguments);
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
        displayController.setFeedback(feedback);
        return feedback;
    }

    // ================================================================
    // Initialization methods
    // ================================================================

    //@author A0122081X
    public void setStage(Stage stage) {
	    this.stage = stage;
	}
    
    // Fixes the delay when adding first task upon start up
	private void warmUpParser() {
		parser.parse("foo today");
	}

	// Load the incomplete tasks into displayedTasks
	private void loadIncompleteTasks() {
		for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }
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

    //@author A0122393L
    private String addTask(String input) {
        if (input.isEmpty()) {
            return MESSAGE_INVALID_COMMAND;
        }
        try {
            parser.parse(input);
        } catch (DateTimeException e) {
            return String.format(MESSAGE_NON_CHRONO_DATES, e.getMessage());
        }
        Task task;
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String notParsedWords = parser.getNotParsedWords();
        ArrayList<Task> newTask = new ArrayList<Task>();

        // Instantiate a new Task object
        try {
            newTask = taskCreator.create(input,
                                         parsedDates,
                                         parsedWords,
                                         notParsedWords);
            task = newTask.get(0);
        } catch (IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }

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
    //@author A0122081X
    private String editTask(String input) {
        String[] inputArray;
        int editIndex;
        boolean editAll = false;
        Task editTask;

        // Check if it's an edit all
        if (input.toLowerCase().contains(STRING_ALL)) {
            input = input.replace(STRING_ALL, STRING_EMPTY).trim();
            editAll = true;
            logger.log(Level.INFO, "Contains 'all' in edit");
        }

        try {
            inputArray = input.split(STRING_SPACE);
            // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
            editIndex = Integer.parseInt(inputArray[0]) - 1;
            editTask = displayedTasks.get(editIndex);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return MESSAGE_TASK_INDEX_ERROR;
        }

        // Creates an input to addTask
        String[] addArgumentArray =  new String[inputArray.length - 1];
        System.arraycopy(inputArray, 1, addArgumentArray, 0, inputArray.length - 1);
        String addArgument = String.join(" ", addArgumentArray);

        if (addArgument.isEmpty()) {
            return MESSAGE_INVALID_COMMAND;
        }

        if (editAll && editTask.getId() != null) {
            deleteAllTasks(editTask);
            addTask(addArgument);
            return String.format(MESSAGE_EDIT_ALL, editTask);
        } else {
            deleteIndividualTask(editTask);
            addTask(addArgument);
        }
        
        checkPreviousDisplay();

        return String.format(MESSAGE_EDIT, editTask);
    }

    //@author A0122393L
    private String deleteTask(String input) {
        boolean deleteAll = false;
        Task removeTask;

        if (input.toLowerCase().contains(STRING_ALL)) {
            // Remove the "all" keyword so the try-catch can parse it properly
            input = input.toLowerCase().replace(STRING_ALL, STRING_EMPTY).trim();
            deleteAll = true;
        }

        try {
            // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
            int removalIndex = Integer.parseInt(input) - 1;
            removeTask = displayedTasks.get(removalIndex);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return MESSAGE_TASK_INDEX_ERROR;
        }

        if (deleteAll && removeTask.getId() != null) {
            deleteAllTasks(removeTask);
            return String.format(MESSAGE_DELETE_ALL, removeTask.getDescription());
        } else {
            deleteIndividualTask(removeTask);
            return String.format(MESSAGE_DELETE, removeTask);
        }
    }

    private void deleteIndividualTask(Task taskToDelete) {
        if (taskToDelete.isRecurring()) {
            for (Task task : allTasks) {
                if (task.getId() != null && task.getId().equals(taskToDelete.getId())) {
                    task.addException(taskToDelete.getDate());
                }
            }
        }
        displayedTasks.remove(taskToDelete);
        allTasks.remove(taskToDelete);
        updateStorageWithAllTasks();
        logger.log(Level.INFO, "displayedTasks after individual deletion: " + displayedTasks);
    }

    private void deleteAllTasks(Task taskToDelete) {
        String recurringId = taskToDelete.getId();
        ArrayList<Task> tasksToDelete = new ArrayList<Task>();

        for (Task task : allTasks) {
            if (task.getId() != null && task.getId().equals(recurringId)) {
                tasksToDelete.add(task);
            }
        }
        displayedTasks.removeAll(tasksToDelete);
        allTasks.removeAll(tasksToDelete);
        updateStorageWithAllTasks();
        logger.log(Level.INFO, "displayedTasks after all deletion: " + displayedTasks);
    }

    //@author A0122081X
    private String completeTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            
            if (task.isCompleted()) {
                return String.format(MESSAGE_COMPLETE_FAILED, task.getDescription());
            }
            
            task.markAsCompleted();
            logger.log(Level.INFO, "the completed task: " + task.toString());

            updateStorageWithAllTasks();

            logger.log(Level.INFO, "completed tasks after complete: " + getCompletedTasks(allTasks));
            return String.format(MESSAGE_COMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_TASK_INDEX_ERROR;
        }
    }

    private String incompleteTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            task.markAsIncomplete();
            logger.log(Level.INFO, "the incompleted task: " + task.toString());

            updateStorageWithAllTasks();

            logger.log(Level.INFO, "incomplete tasks after incomplete: " + getIncompleteTasks(allTasks));
            return String.format(MESSAGE_INCOMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_TASK_INDEX_ERROR;
        }
    }

    //@author A0121813U
    // The previous state of the ArrayList and the ObservableList are restored
    private String undo() {
    	assert previousStates != null;
        if (previousStates.isEmpty()) {
            return MESSAGE_NO_UNDO;
        }
        previousStates.getPreviousState();
        restorePreviousState(); 
        updateStorageWithAllTasks(); 
        checkPreviousDisplay();
        return String.format(MESSAGE_UNDO, previousStates.getPreviousCommand());
    }
    
    // Execute search if the previous display is on search display
	private void checkPreviousDisplay() {
		if (switchDisplayToSearch) {
			search(searchArgument);
		}
	}

	// Assign the allTasks and displayedTasks field to its previous state
	private void restorePreviousState() {
		allTasks = previousStates.getAllTasks();
		displayedTasks = previousStates.getDisplayedTasks();
	}

    //@author A0122393L
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

    //@author A0122081X
    private void displayTask(String input) {
        displayedTasks.clear();

        if (input.equals(STRING_COMPLETED)) {
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
    
    //@author A0122393L
    private String moveSaveFileDirectory(String input) {
        if (storage.moveSaveFileDirectory(input)) {
            return MESSAGE_SAVE_MOVE;
        } else {
            return MESSAGE_SAVE_MOVE_FAIL;
        }
    }
    
    private String setSaveFileDirectory(String input) {
        if (storage.setSaveFileDirectory(input)) {
            allTasks = storage.readFile();
            return MESSAGE_SAVE_SET;
        } else {
            return MESSAGE_SAVE_SET_FAIL;
        }
    }
    
    //@author A0122081X
    private String clear() {
        allTasks = new ArrayList<Task>();
        displayedTasks = FXCollections.observableArrayList();;
        storage.updateFiles(allTasks);
        displayController.resetScrollIndex();
        return MESSAGE_ALL_CLEAR;
    }

    private void exit() {
        updateStorageWithAllTasks();
    }

    // ================================================================
    // Utility methods
    // ================================================================
    
    //@author A0121813U
    // Based on what input the user has typed, 
    // this method will determine the appropriate screen to display
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
    
    //@author A0122081X
    private void updateDisplayWithDefault() {
        displayedTasks.setAll(getIncompleteTasks(allTasks));
        displayController.updateOverviewDisplay(displayedTasks);
        logger.log(Level.INFO, "Displayed tasks: " + displayedTasks);
    }

    private void updateDisplayWithCompleted() {
        displayedTasks.setAll(getCompletedTasks(allTasks));
        displayController.updateOverviewDisplay(displayedTasks);
        logger.log(Level.INFO, "Displayed tasks: " + displayedTasks);
    }
    
    //@author A0121813U
    // Call the display object to show the "search" display
    private void updateDisplaySearch() {
    	assert displayedTasks != null;
    	sortSearchedTasks();
        displayController.updateSearchDisplay(displayedTasks, searchArgument);
    }
    
    // Call the display object to show the "help" display
    private void updateHelpDisplay() {
    	displayController.showHelpDisplay();
    }

    // Sorts the allTasks field based on developer's preference
    private void sortAllTasks() {
    	assert allTasks != null;
    	userDefinedSort = new UserDefinedSort(allTasks);
    	userDefinedSort.addComparator(new SortType());
    	userDefinedSort.addComparator(new SortTime());
    	userDefinedSort.addComparator(new SortDate());
        userDefinedSort.addComparator(new SortOverdue());   
        allTasks = userDefinedSort.executeSort();
    }
    
    // Sorts the displayedTasks when the "search" command is entered
    private void sortSearchedTasks() {
    	assert displayedTasks != null;
    	userDefinedSort = new UserDefinedSort(new ArrayList<Task>(displayedTasks));
        userDefinedSort.addComparator(new SortType());
        userDefinedSort.addComparator(new SortTime());
        userDefinedSort.addComparator(new SortDate());
        userDefinedSort.addComparator(new SortOverdue());
        userDefinedSort.addComparator(new SortIncomplete());
        userDefinedSort.executeSort();
        displayedTasks = FXCollections.observableArrayList(userDefinedSort.getList());
    }

    // Save the current state of allTasks and displayedTasks field before execution of command
    private void saveCurrentState(String input) {
    	assert allTasks != null;
        assert displayedTasks != null;
        previousStates.storeCurrentState(allTasks, displayedTasks);
        previousStates.storeCommand(input);
    }

    //@author A0122393L
    private void updateStorageWithAllTasks() {
        storage.updateFiles(allTasks);
    }

    // ================================================================
    // Testing methods
    // ================================================================

    //@author A0122081X
    public List<Task> getIncompleteTasksPublic() {
        return getIncompleteTasks(allTasks);
    }

    public List<Task> getCompleteTasksPublic() {
        return getCompletedTasks(allTasks);
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }
}
