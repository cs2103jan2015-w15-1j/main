package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> allTasks;

    private Stack<ArrayList<Task>> previousStates;
    private Stack<ObservableList<Task>> previousStatesDisplayed;
    
    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    //private ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
    private String arguments;
    private DateParser parser;

    private boolean switchDisplay = false;
    
    private UserDefinedSort uds;
    
    private Display display;

    // ================================================================
    // Constants
    // ================================================================
    private final static String TASK_OVERVIEW_LOCATION = "/view/TaskOverview.fxml";
    private static final String MESSAGE_SAVE_FILE_READY = "Welcome to main.java.Veto. %s is ready for use.";
    private static final String MESSAGE_EMPTY = "There is currently no task.\n";
    private static final String MESSAGE_ADD = "main.java.Task has been successfully added:\n     Description: %s\n     Deadline: %s\n     Time: %s\n";
    private static final String MESSAGE_NOT_APPL = "Not applicable";
    private static final String MESSAGE_DELETE = "main.java.Task has been successfully deleted:\n Description: %s \n";
    private static final String MESSAGE_EDIT = "main.java.Task has been successfully edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed. \n";
    private static final String MESSAGE_INCOMPLETE = "\"%s\" incompleted. \n";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_UNDO = "Last command has been undone. \n";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command. \n";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo. \n";

    // ================================================================
    // Constructor
    // ================================================================
    /**
     * The constructor is called before the initialize() method.
     */
    public Controller() {
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
        display.updateDisplay(displayedTasks);
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
                switchDisplay = false;
	            break;
	        case COMPLETE: // DONE
	            updateState();
	            feedback = completeTask(arguments);
	            break;
	        case INCOMPLETE:
	            updateState();
                feedback = incompleteTask(arguments);
	            break;
	        case UNDO:  // DONE
	            feedback = undo();
	            break;
	        case SEARCH:  // DONE
	            search(arguments);
	            this.arguments = arguments;
	            switchDisplay = true;
	            break;
	        case INVALID:
	            feedback =  invalid();
	            break;
	        case EXIT:  // WINDOWS WILL NOT CLOSE AFTER THIS COMMAND
	            timeToExit = true;
	            feedback =  exit();
	            break;
	        default:
	            break;

        }
        sortAllTasks();
        if (switchDisplay) {
            updateDisplaySearch();
        } else {
            updateDisplayWithDefault();
        }
        
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

    // ================================================================
    // Logic methods
    // ================================================================

    private String addTask(String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();

        // Instantiate a new Task object
        Task task = new Task(input, parsedDates, parsedWords);

        allTasks.add(task);
        updateStorageWithAllTasks();

        return "Task has been added: " + task.toString();
        // brought back previous return codes - CK
//        if (task.getType() == Task.Type.FLOATING) {
//            return String.format(MESSAGE_ADD, task.getDescription(),
//                    MESSAGE_NOT_APPL, MESSAGE_NOT_APPL);
//        } else if (task.getType() == Task.Type.DEADLINE) {
//            return String.format(MESSAGE_ADD, task.getDescription(),
//                    task.getDate(), MESSAGE_NOT_APPL);
//        } else {
//            String formattedTime = task.getStartTime() + " to "
//                    + task.getEndTime();
//            return String.format(MESSAGE_ADD, task.getDescription(),
//                    task.getDate(), formattedTime);
//        }
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = displayedTasks.get(removalIndex);
            displayedTasks.remove(task);
            allTasks.remove(task);
            
            updateStorageWithAllTasks();

            return null;
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    /**
     *
     * Current implementation of Edit: 1. Does not allow user to change to a
     * different deadline type. e.g. from Timed to Floating 2. Allows edit of
     * description 3. Allows change of deadline from one type to the same type.
     * 4. Allows user to type any substring of the words "description" /
     * "deadline" as a substitution for its original word.
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
                task.setTypeDateTime(parsedDates);
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
        display.updateDisplay(displayedTasks);
    }

    private void updateDisplaySearch() {
    	sortSearchedTasks();
        display.updateSearchDisplay(displayedTasks, arguments);
    }

    private void sortAllTasks() {
    	uds = new UserDefinedSort(allTasks);
    	uds.addComparator(new SortType());
        uds.addComparator(new SortOverdue());
        uds.addComparator(new SortDate());
        uds.executeSort();  
    }
    
    private void sortSearchedTasks() {
    	uds = new UserDefinedSort(new ArrayList<Task>(displayedTasks));
        uds.addComparator(new SortType());
        uds.addComparator(new SortOverdue());
        uds.addComparator(new SortDate());
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
        storage.updateFiles(emptyArr);
    }

}
