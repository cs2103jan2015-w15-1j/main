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
    private ObservableList<String> helpList = FXCollections.observableArrayList();
    
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
    private static final String MESSAGE_WELCOME = "Welcome to Veto!  Here is an overview of the week ahead.";
    private static final String MESSAGE_ADD = "Task has been successfully added: %s";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted: %s";
    private static final String MESSAGE_EDIT = "Task has been successfully edited: %s";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_COMPLETE_FAILED = "\"%s\" already completed.";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" marked as incomplete.";
    private static final String MESSAGE_UNDO = "Previous command has been undone: \"%s\"";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo.";
    private static final String MESSAGE_ALL_CLEAR = "All contents are cleared!";
    
    private static final String EMPTY_STRING = "";
    
    private static final String HELP_ADD = "Add a task  ---------------------------------------------  add <arguments>";
    private static final String HELP_EDIT = "Edit a task  ---------------------  edit <index> <desc/dead> <arguments>";
    private static final String HELP_DELETE = "Delete a task  ----------------------------------------------  delete <index>";
    private static final String HELP_COMPLETE = "Mark a task as complete  -----------------------------  complete <index>";
    private static final String HELP_INCOMPLETE = "Mark a task as incomplete  -------------------------  incomplete <index>";
    private static final String HELP_UNDO = "Undo previous action  ----------------------------------------------  undo";
    private static final String HELP_SET_SAVE_LOCATION = "Change save directory  -----------------------------------  set <directory>";
    private static final String HELP_SEARCH = "Search for a task  -------------------------------  search <keyword/date>";
    private static final String HELP_EXIT = "Exit Veto  -------------------------------------------------------------  exit";

    
    // ================================================================
 	// Constructor
 	// ================================================================
    private Controller() {
        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        taskCreator = CreateTask.getInstance();
        allTasks = storage.readFile();
        
        sortAllTasks();
        
        instantiateHelpList();

        // Load the incomplete tasks into displayedTasks (MAIN VIEW WHEN APP STARTS)
        for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }
        
        previousStates = new History();
        
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
        	
        	case SET:
	            feedback = setSaveFileDirectory(arguments);
	            break;
	        
        	case ADD:
	            saveCurrentState(input);
	            feedback = addTask(arguments);
	            switchDisplayToSearch = false;
	            break; 
	        
        	case DELETE:
	            saveCurrentState(input);
	            feedback = deleteTask(arguments);
	            break;
	        
        	case EDIT:
	            saveCurrentState(input);
	            feedback = editTask(arguments);
	            break;
	        
        	case DISPLAY:
                displayTask(arguments);
	            break;
	        
        	case COMPLETE:
	            saveCurrentState(input);
	            feedback = completeTask(arguments);
	            break;
	        
        	case INCOMPLETE:
	            saveCurrentState(input);
                feedback = incompleteTask(arguments);
	            break;
	        
        	case UNDO:
	            feedback = undo();
	            break;
	        
        	case SEARCH:
	            search(arguments);
                searchArgument = arguments;
	            switchDisplayToSearch = true;
	            break;
	        
        	case CLEAR:
	        	saveCurrentState(input);
	        	feedback = clear();
	        	break;
	        
        	case INVALID:
	            feedback = invalid();
	            break;
	        
        	case HELP:
	        	helpUser = true;
	        	break;
	        
        	case EXIT:
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

    private String editTask(String input) {
        String[] inputArray;
        int editIndex;

        // Split the input into the index and the arguments
        try {
            inputArray = input.split(" ");
            editIndex = Integer.parseInt(inputArray[0]) - 1;
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }

        Task task = displayedTasks.get(editIndex);
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String notParsedWords = parser.getNotParsedWords();
        task.update(input, parsedDates, parsedWords, notParsedWords);

        return String.format(MESSAGE_EDIT, task);
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = displayedTasks.get(removalIndex);
            displayedTasks.remove(task);
            allTasks.remove(task);

            updateStorageWithAllTasks();

            return String.format(MESSAGE_DELETE, task);
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
    	display.updateHelpDisplay(helpList);
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
    
    private void instantiateHelpList() {
    	helpList.add(HELP_ADD);
    	helpList.add(HELP_EDIT);
    	helpList.add(HELP_DELETE);
    	helpList.add(HELP_COMPLETE);
    	helpList.add(HELP_INCOMPLETE);
    	helpList.add(HELP_UNDO);
    	helpList.add(HELP_SET_SAVE_LOCATION);
    	helpList.add(HELP_SEARCH);
    	helpList.add(HELP_EXIT);
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
