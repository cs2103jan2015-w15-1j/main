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
import java.util.Stack;
import java.util.stream.Collectors;

public class Controller {
	
    // ================================================================
    // Fields
    // ================================================================
    private static Controller controller;

    private Storage storage;

    private ArrayList<Task> allTasks;
    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();

    private Stack<ArrayList<Task>> previousStates;
    private Stack<ObservableList<Task>> previousStatesDisplayed;

    private String searchArgument;
    private DateParser parser;
    private CreateTask taskCreator;

    private boolean switchDisplay = false;
    
    private UserDefinedSort uds;
    
    private Display display;

    private Stage stage;

    // ================================================================
    // Constants
    // ================================================================
    private static final String MESSAGE_WELCOME = "Welcome to Veto!  Here is an overview of the week ahead.";
    private static final String MESSAGE_ADD = "Task has been successfully added: ";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted: ";
    private static final String MESSAGE_EDIT = "Task has been successfully edited: ";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_COMPLETE_FAILED = "\"%s\" already completed.";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" marked as incomplete.";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_UNDO = "Last command has been undone.";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo.";
    private static final String MESSAGE_ALL_CLEAR = "All contents are cleared!";
    
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
    /**
     * The constructor is called before the initialize() method.
     */
    private Controller() {
        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        taskCreator = CreateTask.getInstance();
        allTasks = storage.readFile();
        
        sortAllTasks();

        // Load the incomplete tasks into displayedTasks (MAIN VIEW WHEN APP STARTS)
        for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }

        previousStates = new Stack<ArrayList<Task>>();
        previousStatesDisplayed = new Stack<ObservableList<Task>>();
        
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
        display.updateOverviewDisplay(displayedTasks);
        display.setFeedback(getWelcomeMessage());
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
        String feedback = "";
        
        boolean helpUser = false;

        switch (commandType) {
        	case SET:  // DONE
	            feedback = setSaveFileDirectory(arguments);
	            break;
	        case ADD: // DONE
	            updateState();
	            feedback = addTask(arguments);
	            switchDisplay = false;
	            break; 
	        case DELETE: // DONE
	            updateState();
	            feedback = deleteTask(arguments);
	            break;
	        case EDIT:
	            updateState();
	            feedback = editTask(arguments);
	            break;
	        case DISPLAY:  // DONE
                displayTask(arguments);
	            break;
	        case COMPLETE: // DONE
	            updateState();
	            feedback = completeTask(arguments);
	            break;
	        case INCOMPLETE:  // DONE
	            updateState();
                feedback = incompleteTask(arguments);
	            break;
	        case UNDO:  // DONE
	            feedback = undo();
	            break;
	        case SEARCH:  // DONE
	            search(arguments);
                searchArgument = arguments;
	            switchDisplay = true;
	            break;
	        case CLEAR:    // DONE
	        	updateState();
	        	feedback = clear();
	        	break;
	        case INVALID:  // DONE
	            feedback =  invalid();
	            break;
	        case HELP:  // DONE
	        	helpUser = true;
	        	break;
	        case EXIT:  // DONE
	            feedback =  exit();
                stage.hide();
	            break;
	        default:
	            break;
        }
   
        if (helpUser) {
        	updateHelpDisplay();
        } else if (switchDisplay) {
            updateDisplaySearch();
        } else {
        	sortAllTasks();
            updateDisplayWithDefault();
        }
        
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
	    sortAllTasks();
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
        
        return MESSAGE_ADD + task.toString();      
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

        return MESSAGE_EDIT;
    }


    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = displayedTasks.get(removalIndex);
            displayedTasks.remove(task);
            allTasks.remove(task);

            updateStorageWithAllTasks();

            return MESSAGE_DELETE + task.toString();
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
        if (previousStates.empty()) {
            return MESSAGE_NO_UNDO;
        } else {
            ArrayList<Task> previousTasks = previousStates.pop();
            ObservableList<Task> previousDisplayed = previousStatesDisplayed.pop();
            
            allTasks = previousTasks;
            displayedTasks = previousDisplayed;
            
            updateStorageWithAllTasks();
            
            if (switchDisplay) {
            	search(searchArgument);
            }
            return MESSAGE_UNDO;
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
            switchDisplay = true;
            searchArgument = input;
            updateDisplayWithCompleted();
        } else {
            switchDisplay = false;
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
        ArrayList<Task> emptyArr = new ArrayList<Task>();
        allTasks = emptyArr;
        displayedTasks = FXCollections.observableArrayList();;
        storage.updateFiles(emptyArr);
        return MESSAGE_ALL_CLEAR;
    }

    private String exit() {
        updateStorageWithAllTasks();
        return MESSAGE_EXIT;
    }

    // ================================================================
    // Utility methods
    // ================================================================
    
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
    	ObservableList<String> list = FXCollections.observableArrayList();
    	list.add(HELP_ADD);
    	list.add(HELP_EDIT);
    	list.add(HELP_DELETE);
    	list.add(HELP_COMPLETE);
    	list.add(HELP_INCOMPLETE);
    	list.add(HELP_UNDO);
    	list.add(HELP_SET_SAVE_LOCATION);
    	list.add(HELP_SEARCH);
    	list.add(HELP_EXIT);
    	display.updateHelpDisplay(list);
;    }

    private void sortAllTasks() {
    	uds = new UserDefinedSort(allTasks);
    	uds.addComparator(new SortType());
    	uds.addComparator(new SortDate());
        uds.addComparator(new SortOverdue());   
        allTasks = uds.executeSort();
    }
    
    private void sortSearchedTasks() {
    	uds = new UserDefinedSort(new ArrayList<Task>(displayedTasks));
        uds.addComparator(new SortType());
        uds.addComparator(new SortDate());
        uds.addComparator(new SortOverdue());
        uds.addComparator(new SortIncomplete());
        uds.executeSort();
        displayedTasks = FXCollections.observableArrayList(uds.getList());
    }

    private void updateStorageWithAllTasks() {
        storage.updateFiles(allTasks);
    }

    private void updateState() {
        previousStates.push(cloneState(allTasks));
        previousStatesDisplayed.push(cloneState(displayedTasks));
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
