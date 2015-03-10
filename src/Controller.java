import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Controller {
    private static final int PARAM_POSITION_FILENAME = 0;

    private static final String MESSAGE_SAVE_FILE_READY = "Welcome to Veto. %s is ready for use.";

    private static final String MESSAGE_EMPTY = "There is currently no task.\n";
    private static final String MESSAGE_ADD = "Task has been successfully added:\n Description: %s, Deadline: %s";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted:\n Description: %s";
    private static final String MESSAGE_EDIT = "Task has been successfully edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed. \n";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";
    private static final String MESSAGE_UNDO = "Last command has been undone. \n";


    private static final String MESSAGE_INVALID_COMMAND = "Invalid command. \n";
    private static final String MESSAGE_NO_UNDO = "Already at oldest change, unable to undo. \n";

    private static final String DISPLAY_LINE = "%d. %s\n";
    private static final String DISPLAY_LINE_DEADLINE = "%s/%s \n";
    private static final String DISPLAY_NO_DEADLINE = "No deadline \n";

    private static final String ERROR_NO_FILE = "No file in argument \n";

    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> incompleteTasks;
    private ArrayList<Task> completeTasks;

    private Stack<ArrayList<Task>> previousStates;

    public Controller(String[] args) {
        exitIfMissingArgs(args);
        saveFileName = getFileNameFromArgs(args);
        storage = new Storage();
        timeToExit = false;
        ArrayList<Task> allTasks = storage.readTasksFromFile();
        incompleteTasks = new ArrayList<Task>(getIncompleteTasks(allTasks));
        completeTasks = new ArrayList<Task>(getCompleteTasks(allTasks));
        previousStates = new Stack<ArrayList<Task>>();
    }

    // Public methods
    public String getWelcomeMessage() {
        return String.format(MESSAGE_SAVE_FILE_READY, saveFileName);
    }

    public String executeCommand(String input) {
        Command currentCommand = new Command(input);

        Command.Type commandType = currentCommand.getCommandType();
        String arguments = currentCommand.getArguments();

        switch (commandType) {
            case SETSAVEFILE :
                if (setSaveFileDest(arguments)) {
                    return MESSAGE_SAVE_DEST;
                }
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

    // Private methods
    private String getFileNameFromArgs(String[] args) {
        return args[PARAM_POSITION_FILENAME];
    }

    private Boolean setSaveFileDest(String input) {
        return storage.setSaveFileDirectory(input);
    }

    private List<Task> getIncompleteTasks(ArrayList<Task> allTasks) {
        List<Task> incompleteTasks = allTasks.stream()
                                             .filter(task -> !task.getTaskStatus())
                                             .collect(Collectors.toList());
        return incompleteTasks;
    }

    private List<Task> getCompleteTasks(ArrayList<Task> allTasks) {
        List<Task> completeTasks = allTasks.stream()
                                           .filter(task -> task.getTaskStatus())
                                           .collect(Collectors.toList());
        return completeTasks;
    }

    private String addTask(String input) {
        Task task = new Task(input);
        incompleteTasks.add(task);
        updateStorageWithAllTasks();

        String description = task.getInfo();
        if (task.getMonth() != null) { // task has a deadline
            String deadline = String.format(DISPLAY_LINE_DEADLINE, task.getDay(), task.getMonth());
            return String.format(MESSAGE_ADD, description, deadline);
        } else {
            return String.format(MESSAGE_ADD, description, DISPLAY_NO_DEADLINE);
        }
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            Task task = incompleteTasks.remove(removalIndex);
            updateStorageWithAllTasks();

            return String.format(MESSAGE_DELETE, task.getInfo());
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
        // TODO need to think this through

        // Split the input into the index and the arguments
        String[] inputArray = input.split(" ");
        int editIndex = Integer.parseInt(inputArray[0]) - 1;
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
                String description = task.getInfo();
                String date = editArgument.toString();
                String newInput = description.trim() + " " + date.trim();

                System.out.println(newInput);

                Task newTask = new Task(newInput);
                incompleteTasks.set(editIndex, newTask);
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

    private String formatTasksForDisplay(ArrayList<Task> input) {
        // TODO: this method will be depreciated after Task.toString() is completed
        if (input.isEmpty()) {
            return MESSAGE_EMPTY;
        }

        String display = "";

        int counter = 1;
        for (Task task : input) {
            display += String.format(DISPLAY_LINE, counter, task.getInfo());
            if (task.getMonth() == null) {
                display += DISPLAY_NO_DEADLINE;
            } else {
                display += String.format(DISPLAY_LINE_DEADLINE, task.getDay(), task.getMonth());
            }
            counter++;
        }
        return display;
    }

    private String completeTask(String input) {
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            Task task = incompleteTasks.get(index);
            task.markAsComplete();
            updateStorageWithAllTasks();
            return String.format(MESSAGE_COMPLETE, task.getInfo());
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
            completeTasks = previousCompleteTasks;

            updateStorageWithAllTasks();

            return MESSAGE_UNDO;
        }
    }

    private ArrayList<Task> concatenateTasks(ArrayList<Task> first, ArrayList<Task> second) {
        ArrayList<Task> output = new ArrayList<Task>();
        output.addAll(first);
        output.addAll(second);
        return output;
    }

    private void updateStorageWithAllTasks() {
        ArrayList<Task> allTasks = concatenateTasks(incompleteTasks, completeTasks);
        storage.writeTasksToFile(allTasks);
    }

    private void updateState() {
        previousStates.push(new ArrayList<Task>(incompleteTasks));
        previousStates.push(new ArrayList<Task>(completeTasks));
    }


    private ArrayList<Task> search(String input) {
        // TODO check Task.getInfo() implementation
        ArrayList<Task> searchResults = new ArrayList<Task>();

        ArrayList<Task> allTasks = concatenateTasks(incompleteTasks, completeTasks);
        for (Task task : allTasks) {
            String taskInfo = task.getInfo();
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
        return MESSAGE_EXIT;
    }

    private void exitIfMissingArgs(String[] args) {
        if (args.length == 0) {
            System.err.println(ERROR_NO_FILE);
            System.exit(0);
        }
    }
}
