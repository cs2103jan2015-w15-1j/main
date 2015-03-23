package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.Command;
import main.java.DateParser;
import main.java.MainApp;
import main.java.Storage;
import main.java.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class TaskOverviewController {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> taskDescription;
    @FXML
    private TableColumn<Task, String> taskDeadline;
    @FXML
    private TableColumn<Task, Integer> taskIndex;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private ObservableList<Task> taskData = FXCollections.observableArrayList();
    private MainApp mainApp;
    private DateParser parser;

    // ================================================================
    // Methods
    // ================================================================
    /**
     * The constructor is called before the initialize() method.
     */
    public TaskOverviewController() {
        parser = DateParser.getInstance();
        storage = new Storage();
        String saveFileName = storage.getSaveFileName();

        ArrayList<Task> allTasks = storage.readFile();

        for (Task task : allTasks) {
            taskData.add(task);
        }

        timeToExit = false;

        incompleteTasks = new ArrayList<Task>(getIncompleteTasks(allTasks));
        completedTasks = new ArrayList<Task>(getCompletedTasks(allTasks));

        previousStates = new Stack<ArrayList<Task>>();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Populating the TableColumns (but are not directly shown in the UI yet)
        taskDescription.setCellValueFactory(
                cellData -> cellData.getValue().getTaskDesc());
        taskDeadline.setCellValueFactory(
                cellData -> cellData.getValue().getStringPropertyTaskDate());

        // Add the observable list data to the table
        taskTable.setItems(taskData);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // ================================================================
    // Imported from Controller.java
    // ================================================================

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

    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> incompleteTasks;
    private ArrayList<Task> completedTasks;

    private Stack<ArrayList<Task>> previousStates;


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

        switch (commandType) {
            case SETSAVEFILE :
                return setSaveFileDirectory(arguments);
            case ADD :
                updateState();
                return addTask(arguments);
            case DELETE :
                updateState();
                return deleteTask(arguments);
            case EDIT :
                updateState();
                return editTask(arguments);
            case DISPLAY :
                return formatTasksForDisplay(incompleteTasks);
            case COMPLETE :
                updateState();
                return completeTask(arguments);
            case INCOMPLETE :
                updateState();
                return incompleteTask(arguments);
            case UNDO :
                return undo();
            case SEARCH :
                ArrayList<Task> searchResults = search(arguments);
                return formatTasksForDisplay(searchResults);
            case INVALID :
                return invalid();
            case EXIT :
                timeToExit = true;
                return exit();
            default :
                return null;
        }
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


    // ================================================================
    // Logic methods
    // ================================================================

    private String addTask(String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        Task task = new Task(input, parsedDates, parsedWords);

        // Testing purpose. Remove this line in the future.
        taskData.add(task);

        incompleteTasks.add(task);
        updateStorageWithAllTasks();
        if (task.getType() == Task.Type.FLOATING) {
            return String.format(MESSAGE_ADD, task.getDescription(), MESSAGE_NOT_APPL, MESSAGE_NOT_APPL);
        } else if (task.getType() == Task.Type.DEADLINE) {
            return String.format(MESSAGE_ADD, task.getDescription(), task.getDate(), MESSAGE_NOT_APPL);
        } else {
            String formattedTime = task.getStartTime() + " to " + task.getEndTime();
            return String.format(MESSAGE_ADD, task.getDescription(), task.getDate(), formattedTime);
        }



    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = incompleteTasks.remove(removalIndex);
            updateStorageWithAllTasks();

            return String.format(MESSAGE_DELETE, task.getDescription());
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

        try {
            Task task = incompleteTasks.get(editIndex);
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
            Task task = incompleteTasks.get(index);
            task.markAsComplete();

            // Move the completed task from incompleteTasks to completeTasks
            completedTasks.add(incompleteTasks.remove(index));
            updateStorageWithAllTasks();

            return String.format(MESSAGE_COMPLETE, task.getDescription());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String incompleteTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = completedTasks.get(index);
            task.markAsIncomplete();

            // Move the completed task from completeTasks to incompleteTasks
            incompleteTasks.add(completedTasks.remove(index));
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
            ArrayList<Task> previousCompleteTasks = previousStates.pop(); // update state pushes the complete first
            ArrayList<Task> previousIncompleteTasks = previousStates.pop();

            incompleteTasks = previousIncompleteTasks;
            completedTasks = previousCompleteTasks;

            updateStorageWithAllTasks();

            return MESSAGE_UNDO;
        }
    }

    private ArrayList<Task> search(String input) {
        // TODO check main.java.Task.getInfo() implementation
        ArrayList<Task> searchResults = new ArrayList<Task>();

        ArrayList<Task> allTasks = concatenateTasks(incompleteTasks, completedTasks);
        for (Task task : allTasks) {
            String taskInfo = task.getDescription();
            if (taskInfo.contains(input)) {
                searchResults.add(task);
            }
        }
        return searchResults;
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

    private ArrayList<Task> concatenateTasks(ArrayList<Task> first, ArrayList<Task> second) {
        ArrayList<Task> output = new ArrayList<Task>();
        output.addAll(first);
        output.addAll(second);
        return output;
    }

    private void updateStorageWithAllTasks() {
        ArrayList<Task> allTasks = concatenateTasks(incompleteTasks, completedTasks);
        storage.updateFiles(allTasks);
    }

    private void updateState() {
        previousStates.push(new ArrayList<Task>(incompleteTasks));
        previousStates.push(new ArrayList<Task>(completedTasks));
    }


    // ================================================================
    // Testing methods
    // ================================================================

    public ArrayList<Task> getIncompleteTasksPublic() {
        return incompleteTasks;
    }

    public ArrayList<Task> getCompleteTasksPublic() {
        return completedTasks;
    }

    public void clear() {
        ArrayList<Task> emptyArr = new ArrayList<Task>();
        storage.updateFiles(emptyArr);
    }
}
