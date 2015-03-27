package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import main.java.Command;
import main.java.DateParser;
import main.java.Storage;
import main.java.Task;
import main.resources.view.DayBox;
import main.resources.view.Display;
import main.resources.view.TaskBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    private ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
    private ObservableList<Task> searchedList;
    private String arguments;
    private DateParser parser;

    private Display display;

    // They exist so that I can compile my program lol pls remove
//    private ArrayList<Task> incompleteTasks;
//    private ArrayList<Task> completedTasks;

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
        String saveFileName = storage.getSaveFileName();

        allTasks = storage.readFile();
        allTasks = sortToDisplay(allTasks);

        // Load the incomplete tasks into displayedTasks
        for (Task task : getIncompleteTasks(allTasks)) {
            displayedTasks.add(task);
        }

        timeToExit = false;
        previousStates = new Stack<ArrayList<Task>>();
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
        boolean switchDisplay = false;      

        switch (commandType) {
            case SETSAVEFILE :
                return setSaveFileDirectory(arguments);
            case ADD :  // DONE
                updateState();
                addTask(arguments);
                break;
            case DELETE :  // DONE
                updateState();
                deleteTask(arguments);
                break;
            case EDIT :
                updateState();
                editTask(arguments);
                break;
            case DISPLAY :
                return null;
            case COMPLETE :  // DONE
                updateState();
                completeTask(arguments);
                break;
            case INCOMPLETE :
                updateState();
                break;
//                return incompleteTask(arguments);
            case UNDO :
                return undo();
            case SEARCH :
            	searchedList = search(arguments);
            	this.arguments = arguments;
            	switchDisplay = true;
                break;
            case INVALID :
                return invalid();
            case EXIT :
                timeToExit = true;
                return exit();
            default :
                return null;
        }
        // I think need to sort all tasks so that the index is correct (my logic could be wrong)
        sortAllTasks();
        if (switchDisplay) {
        	updateDisplaySearch();
        } else {
        updateDisplayWithDefault();
        }
        return "hello"; // just so I have something to return, will remove once the whole switch case is done
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

    private List<Task> getIncompleteTasks(ArrayList<Task> allTasks) {
        List<Task> incompleteTasks = allTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
        return incompleteTasks;
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

    // TODO NEED TO REPLACE WITH ADAM'S UPDATED METHOD
    private ArrayList<Task> sortToDisplay(ArrayList<Task> list) {
        ArrayList<Task> overdueTasks = new ArrayList<Task>();
        ArrayList<Task> floatingTasks = new ArrayList<Task>();
        ArrayList<Task> notOverdueTasks = new ArrayList<Task>();
        ArrayList<Task> finalList = new ArrayList<Task>();

        // Separate the floating, overdue and pending
        for (Task task: list) {
            if (task.getType() == Task.Type.FLOATING) {
                floatingTasks.add(task);
            } else if (task.isOverdue()) {
                overdueTasks.add(task);
            } else {
                notOverdueTasks.add(task);
            }
        }

        // Sort according to what we discussed
        overdueTasks = sortByDateAndType(overdueTasks);
        notOverdueTasks = sortByDateAndType(notOverdueTasks);

        finalList.addAll(floatingTasks);
        finalList.addAll(overdueTasks);
        finalList.addAll(notOverdueTasks);

        return finalList;
    }

    private ArrayList<Task> sortByDateAndType(ArrayList<Task> list) {
        ArrayList<Task> output = new ArrayList<Task>();
        boolean isSorted;

        for (Task task: list) {
            isSorted=false;
            if (output.size() == 0) {
                output.add(task);
            } else {
                for (Task something: output) {
                    if (task.getDate().isBefore(something.getDate())) {
                        output.add(output.indexOf(something), task);
                        isSorted = true;
                        break;
                    } else if (task.getDate().isEqual(something.getDate())) {
                        if (something.getType() == Task.Type.TIMED && task.getType() == Task.Type.DEADLINE) {
                            output.add(output.indexOf(something), task);
                            isSorted = true;
                            break;
                        }
                    }
                }
                if (!isSorted) {
                    output.add(task);
                }
            }
        }
        return output;
    }

    private void addTask(String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();

        // Instantiate a new Task object
        Task task = new Task(input, parsedDates, parsedWords);

        allTasks.add(task);
        updateStorageWithAllTasks();
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = displayedTasks.get(removalIndex);
            allTasks.remove(task);
            updateStorageWithAllTasks();

            return null;
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    /**
     *
     * Current implementation of Edit:
     * 1. Does not allow user to change to a different deadline type. e.g. from Timed to Floating
     * 2. Allows edit of description
     * 3. Allows change of deadline from one type to the same type.
     * 4. Allows user to type any substring of the words "description" / "deadline" as a substitution
     * for its original word.
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
            // TODO something should be broken here
            Task task = allTasks.get(editIndex);
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
            // TODO something should be broken here
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = displayedTasks.get(index);
            task.markAsComplete();

            updateStorageWithAllTasks();

            return String.format(MESSAGE_COMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }


//    private String incompleteTask(String input) {
//        try {
//            int index = Integer.parseInt(input.trim()) - 1;
//            Task task = completedTasks.get(index);
//            task.markAsIncomplete();
//
//            // Move the completed task from completeTasks to incompleteTasks
//            incompleteTasks.add(completedTasks.remove(index));
//            updateStorageWithAllTasks();
//
//            return String.format(MESSAGE_INCOMPLETE, task.getDescription());
//        } catch (NumberFormatException | IndexOutOfBoundsException e) {
//            return MESSAGE_INVALID_COMMAND;
//        }
//    }

    // TODO IS BROKEN
    private String undo() {
        if (previousStates.empty()) {
            return MESSAGE_NO_UNDO;
        } else {
//            ArrayList<Task> previousCompleteTasks = previousStates.pop(); // update state pushes the complete first
//            ArrayList<Task> previousIncompleteTasks = previousStates.pop();
//
//            incompleteTasks = previousIncompleteTasks;
//            completedTasks = previousCompleteTasks;
//
//            updateStorageWithAllTasks();
            return MESSAGE_UNDO;
        }
    }

    private ObservableList<Task> search(String input) {
        // TODO check main.java.Task.getInfo() implementation
        ArrayList<Task> searchResults = new ArrayList<Task>();

        parser.parse(input);
        ArrayList<LocalDateTime> searchDate = parser.getDates();

        for (Task task : allTasks) {
            String taskInfo = task.getDescription().toLowerCase();
            if (taskInfo.contains(input.toLowerCase())) {
                searchResults.add(task);
            } else if (searchDate.size()>0 && searchDate.get(0).toLocalDate().equals(task.getDate())) {
                searchResults.add(task);
            }
        }

        ObservableList<Task> results = FXCollections.observableArrayList();
        results.addAll(searchResults);
        return results;
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
        List<Task> incomplete = getIncompleteTasks(allTasks);
        displayedTasks.setAll(incomplete);
        display.updateDisplay(displayedTasks);
    }
    
    private void updateDisplaySearch() {
    	display.updateSearchDisplay(searchedList, arguments);
    }

    private void sortAllTasks() {
        allTasks = sortToDisplay(allTasks);
    }

    private void loadDisplayedTasks(ArrayList<Task> input) {
        displayedTasks.setAll(input);
    }

    private String formatTasksForDisplay(ArrayList<Task> input) {
        if (input.isEmpty()) {
            return MESSAGE_EMPTY;
        }

        String display = "";
        for (Task task : input) {
            display += task;
        }
        return display;
    }

    private void updateStorageWithAllTasks() {
        storage.updateFiles(allTasks);
    }

    private void updateState() {
        // TODO Need to do the cloning and shit
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
