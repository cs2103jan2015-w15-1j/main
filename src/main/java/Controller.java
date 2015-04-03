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

    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> allTasks;
    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();

    private Stack<ArrayList<Task>> previousStates;
    private Stack<ObservableList<Task>> previousStatesDisplayed;

    private String searchArgument;
    private DateParser parser;

    private boolean switchDisplay = false;
    
    private UserDefinedSort uds;
    
    private Display display;

    private Stage stage;

    // ================================================================
    // Constants
    // ================================================================
    private final static String TASK_OVERVIEW_LOCATION = "/view/TaskOverview.fxml";
    private static final String MESSAGE_SAVE_FILE_READY = "Welcome to main.java.Veto. %s is ready for use.";
    private static final String MESSAGE_ADD = "Task has been added: ";
    private static final String MESSAGE_DELETE = "Task has been deleted: ";
    private static final String MESSAGE_EDIT = "Task has been successfully edited.\n";
    private static final String MESSAGE_EDIT_FAILED = "Task could not be edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed. \n";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" incompleted. \n";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_UNDO = "Last command has been undone. \n";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command. \n";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo. \n";
    
    private static final String HELP_ADD = "add <arguments>";
    private static final String HELP_EDIT = "edit <index> <desc/dead> <arguments>";
    private static final String HELP_DELETE = "delete <index>";
    private static final String HELP_COMPLETE = "complete <index>";
    private static final String HELP_INCOMPLETE = "incomplete <index>";
    private static final String HELP_UNDO = "undo";
    private static final String HELP_SET_SAVE_LOCATION = "set <directory>";
    private static final String HELP_SEARCH = "search <keyworrd/date>";
    private static final String HELP_EXIT = "exit";

    // ================================================================
    // Constructor
    // ================================================================
    // Singleton pattern for Controller
    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    /**
     * The constructor is called before the initialize() method.
     */
    private Controller() {
        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        saveFileName = storage.getSaveFileName();

        allTasks = storage.readFile();
        
        //Sorting process
        uds = new UserDefinedSort(allTasks);
        uds.addComparator(new SortType());
        uds.addComparator(new SortOverdue());
        uds.addComparator(new SortDate());
        uds.executeSort();

        // Load the incomplete tasks into displayedTasks (MAIN VIEW WHEN APP STARTS)
        for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }

        timeToExit = false;
        previousStates = new Stack<ArrayList<Task>>();
        previousStatesDisplayed = new Stack<ObservableList<Task>>();
    }


    // To load the tasks into the display on the first load
    public void onloadDisplay() {
        display.updateOverviewDisplay(displayedTasks);
        display.setFeedback("Welcome to Veto!");
    }

    // ================================================================
    // Public methods
    // ================================================================
    public String getWelcomeMessage() {
        return String.format(MESSAGE_SAVE_FILE_READY, saveFileName);
    }

    public String executeCommand(String input) {
        Command currentCommand = new Command(input);

        Command.Type commandType = currentCommand.getCommandType();
        String arguments = currentCommand.getArguments();
        String feedback = "";

        switch (commandType) {
        	case SETSAVEFILE:
	            feedback =  setSaveFileDirectory(arguments);
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
	        case INCOMPLETE:  // NOT TESTED YET
	            updateState();
                feedback = incompleteTask(arguments);
	            break;
	        case UNDO:  // BUGGED
	            feedback = undo();
	            break;
	        case SEARCH:  // DONE
	            search(arguments);
                searchArgument = arguments;
	            switchDisplay = true;
	            break;
	        case CLEAR:    // DONE
	        	updateState();
	        	clear();
	        	break;
	        case INVALID:  // DONE
	            feedback =  invalid();
	            break;
	        case EXIT:  // DONE
	            timeToExit = true;
	            feedback =  exit();
                stage.close();
	            break;
        }
        sortAllTasks();
        if (switchDisplay) {
            updateDisplaySearch();
        } else {
            updateDisplayWithDefault();
        }
        
        display.setFeedback(feedback);
        // just so I have something to return, will remove once the whole switch case is done
        // added feedback so that it still returns the previous returns values from the methods -CK
        return feedback;
    }

    public boolean isTimeToExit() {
        return timeToExit;
    }

    // ================================================================
    // Initialization methods
    // ================================================================

    private String setSaveFileDirectory(String input) {
        return storage.setSaveFileDirectory(input);
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

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // ================================================================
    // Logic methods
    // ================================================================

    private String addTask(String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String nonParsedWords = parser.getNonParsedWords();

        // Instantiate a new Task object
        Task task = new Task(input, parsedDates, parsedWords, nonParsedWords);

        allTasks.add(task);
        updateStorageWithAllTasks();

        return MESSAGE_ADD + task.toString();
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

    /**
     *
     * Current implementation of Edit:
     * 1. Allows users to change FLOATING to any type of tasks.
     * 2. Allows users to change DEADLINE to any type of tasks, except TIMED with DAY
     * 3. Allows users to change TIMED to any type of tasks, except TIMED with DAY
     *
     * @param input
     * @return
     */
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
        String editType = inputArray[1];

        StringBuilder editArgument = new StringBuilder();
        // format: edit 1 deadline /desc ..., so 2 is the arg's starting word
        for (int i = 2; i < inputArray.length; i++) {
            editArgument.append(inputArray[i] + " ");
        }

        // Filter for edit Description or Deadline
        try {
            Task task = displayedTasks.get(editIndex);
            if (editType.equals("d") || editType.equals("de")) {
                return MESSAGE_INVALID_COMMAND;
            } else if ("description".contains(editType)) {
                task.setDescription(editArgument.toString());
            } else if ("deadline".contains(editType)) {
                parser.parse(input);
                ArrayList<LocalDateTime> parsedDates = parser.getDates();
                if (!task.updateTypeDateTime(parsedDates)) {
                    return MESSAGE_EDIT_FAILED;
                }
            } else {
                return MESSAGE_INVALID_COMMAND;
            }
            updateStorageWithAllTasks();
        } catch (Exception e) {
            e.printStackTrace();
            return MESSAGE_INVALID_COMMAND;
        }

        return MESSAGE_EDIT;
    }

    private String completeTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            task.markAsComplete();

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
            ArrayList<Task> previousTasks = previousStates.pop(); // update state pushes the complete first
            ObservableList<Task> previousDisplayed = previousStatesDisplayed.pop(); // update state pushes the complete first
            
            allTasks = previousTasks;
            displayedTasks = previousDisplayed;

            updateStorageWithAllTasks();

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

    public void clear() {
        ArrayList<Task> emptyArr = new ArrayList<Task>();
        allTasks = emptyArr;
        storage.updateFiles(emptyArr);
    }



}
