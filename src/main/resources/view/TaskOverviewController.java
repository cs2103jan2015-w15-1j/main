package main.resources.view;

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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class TaskOverviewController extends AnchorPane {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private ListView<HBox> listView;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> incompleteTasks;
    private ArrayList<Task> completedTasks;

    private Stack<ArrayList<Task>> previousStates;

    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    private ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
    private DateParser parser;

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
    public TaskOverviewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(TASK_OVERVIEW_LOCATION));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parser = DateParser.getInstance();
        storage = Storage.getInstance();
        String saveFileName = storage.getSaveFileName();

        ArrayList<Task> allTasks = storage.readFile();
        for (Task task : allTasks) {
            displayedTasks.add(task);
        }

        // Causes GUI to display the tasks
        updateDisplay(displayedTasks);

        ListChangeListener<Task> listener = new ListChangeListener<Task>() {
            public void onChanged(ListChangeListener.Change<? extends Task> c) {
                updateDisplay(displayedTasks);
            }
        };
        displayedTasks.addListener(listener);

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
//        createContent();
    }

    // ================================================================
    // Public methods
    // ================================================================
    public void createContent() {
        displayBoxes.add(new DayBox("Monday", "30 March"));
        displayBoxes.add(new TaskBox(1, "Finish CS2103T v0.2"));
        displayBoxes.add(new TaskBox(2, "Do CS2103T tutorial"));
        displayBoxes.add(new DayBox("Tuesday", "31 March"));
        displayBoxes.add(new TaskBox(3, "Prepare for April Fools"));
        displayBoxes.add(new DayBox("Wednesday", "1 April"));
        displayBoxes.add(new TaskBox(4, "April Foolssss"));
        displayBoxes.add(new TaskBox(5, "April Foolssss"));
        displayBoxes.add(new TaskBox(6, "April Foolssss"));
        displayBoxes.add(new TaskBox(7, "April Foolssss"));
        displayBoxes.add(new TaskBox(8, "April Foolssss"));
        displayBoxes.add(new TaskBox(9, "April Foolssss"));
        displayBoxes.add(new TaskBox(10, "April Foolssss"));
        displayBoxes.add(new TaskBox(11, "April Foolssss"));
        displayBoxes.add(new TaskBox(12, "April Foolssss"));
        listView.setItems(displayBoxes);
    }

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
                updateDisplay(displayedTasks);
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

    private void sortToDisplay(ArrayList<Task> list) {
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

    	list = finalList;

    }

    private ArrayList<Task> sortByDateAndType(ArrayList<Task> list) {
    	ArrayList<Task> output = new ArrayList<Task>();

    	for (Task task: list) {
    		if (output.size() == 0) {
    			output.add(task);
    		} else {
    			for (Task something: output) {
    				if (task.getDate().isBefore(something.getDate())) {
    					output.add(output.indexOf(something), task);
    					break;
    				} else if (task.getDate().isEqual(something.getDate())) {
    					if (something.getType() == Task.Type.TIMED && task.getType() == Task.Type.DEADLINE) {
    						output.add(output.indexOf(something), task);
    						break;
    					}
    				}
    			}
    			output.add(task);
    		}
    	}
    	return output;
    }

    private String addTask(String input) {
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        Task task = new Task(input, parsedDates, parsedWords);

        // Testing purpose. Remove this line in the future.
        displayedTasks.add(task);
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
//            Task task = incompleteTasks.remove(removalIndex);
//            updateStorageWithAllTasks();
            Task foo = displayedTasks.remove(removalIndex);
            System.out.println(foo);

            return null;
//            return String.format(MESSAGE_DELETE, task.getDescription());
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

        parser.parse(input);
        ArrayList<LocalDateTime> searchDate = parser.getDates();
        ArrayList<Task> allTasks = concatenateTasks(incompleteTasks, completedTasks);

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
        updateDisplay(results);
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

    private void updateDisplay(ObservableList<Task> tasks) {
        // TODO THIS METHOD NEEDS REFACTORING
        // TODO THIS METHOD NEEDS REFACTORING
        // TODO THIS METHOD NEEDS REFACTORING
        ArrayList<Task> listOfTasks = new ArrayList<Task>(tasks);
        sortToDisplay(listOfTasks);

        // re-initialise displayBoxes
        displayBoxes = FXCollections.observableArrayList();
        LocalDate now = LocalDate.now();
        int i = 1;

        // add first category
        DayBox floating = new DayBox("Floating", "");
        displayBoxes.add(floating);
        boolean hasFloating = false;
        for (Task t : listOfTasks) {
            if (t.getType() == Task.Type.FLOATING) {
                hasFloating = true;
                displayBoxes.add(new TaskBox(i, t.getDescription()));
                i++;
            }
        }
        if (!hasFloating) {
            floating.dim();
        }

        // add second category
        DayBox overdue = new DayBox("Overdue", "");
        displayBoxes.add(overdue);
        boolean hasOverdue = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");
        for (Task t : listOfTasks) {
            if (t.isOverdue()) {
                hasOverdue = true;
                displayBoxes.add(new TaskBox(i, t.getDescription() + " on " +
                                                t.getDate().format(formatter)));
                i++;
            }
        }
        if (!hasOverdue) {
            overdue.dim();
        }

        // generate the dates of the 7 days from today
        ArrayList<LocalDate> days = new ArrayList<LocalDate>();
        days.add(now);
        for (int j = 1; j < 7; j++) {
            days.add(now.plusDays(j));
        }

        // formats the date for the day label, eg. Monday, Tuesday, etc
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");

        // formats the date for the date label, eg. 1 April
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");


        for (LocalDate day : days) {
            DayBox label;
            if (day.equals(now)) {
                // special cases to show "Today" and "Tomorrow" instead
                label = new DayBox("Today", day.format(dateFormatter));
            } else if (day.equals(now.plusDays(1))) {
                label = new DayBox("Tomorrow", day.format(dateFormatter));
            } else {
                label = new DayBox(day.format(dayFormatter),
                                            day.format(dateFormatter));
            }
            displayBoxes.add(label);
            boolean hasTaskOnThisDay = false;
            for (Task t : listOfTasks) {
                if (t.getDate() != null && t.getDate().isEqual(day)) {
                    hasTaskOnThisDay = true;
                    displayBoxes.add(new TaskBox(i, t.getDescription()));
                    i++;
                }
            }
            if (!hasTaskOnThisDay) {
                label.dim();
            }
        }
        listView.setItems(displayBoxes);
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
