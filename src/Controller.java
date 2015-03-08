import java.lang.reflect.Array;
import java.util.ArrayList;

public class Controller {
    private static final int PARAM_POSITION_FILENAME = 0;

    private static final String MESSAGE_SAVE_FILE_READY = "Welcome to Veto. %s is ready for use.";

    private static final String MESSAGE_EMPTY = "There is currently no task.\n";
    private static final String MESSAGE_ADD = "Task has been successfully added:\n Description: %s, Deadline: %s";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted:\n Description: %s";
    private static final String MESSAGE_EDIT = "Task has been successfully edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";
    private static final String MESSAGE_UNDO = "Last command has been undone. \n";


    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Unable to undo. \n";

    private static final String DISPLAY_LINE = "%d. %s\n";
    private static final String DISPLAY_LINE_DEADLINE = "Deadline: %s/%s \n";
    private static final String DISPLAY_NO_DEADLINE = "No deadline \n";

    private static final String ERROR_NO_FILE = "No file in argument";

    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;
    private ArrayList<Task> allTasks;
    private ArrayList<Task> allTasksPreviousState;


    public Controller(String[] args) {
        exitIfMissingArgs(args);
        saveFileName = getFileNameFromArgs(args);
        storage = new Storage();
        timeToExit = false;
        allTasks = storage.readTasksFromFile();
        allTasksPreviousState = allTasks;
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
                if (setSaveFileDest(input)) {
                    return MESSAGE_SAVE_DEST;
                }
            case ADD :
                updatePreviousState();
                return addTask(arguments);
            case DELETE :
                updatePreviousState();
                return deleteTask(arguments);
            case EDIT :
                updatePreviousState();
                return editTask(arguments);
            case DISPLAY :
                ArrayList<Task> uncompletedTasks = getUncompletedTasks(allTasks);
                return formatTasksForDisplay(uncompletedTasks);
            case COMPLETE :
                updatePreviousState();
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

    private String addTask(String input) {
        Task task = new Task(input);
        allTasks.add(task);
        storage.writeTasksToFile(allTasks);

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
            Task task = allTasks.remove(removalIndex);
            storage.writeTasksToFile(allTasks);
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
            Task task = allTasks.get(editIndex);
            if (editType.equals("d") || editType.equals("de")) {
                return MESSAGE_INVALID_COMMAND;
            } else if ("description".contains(editType)) {
                task.setDescription(editArgument.toString());
            } else if ("deadline".contains(editType)) {
                Date date = new Date(input);
                date.getLocalDateObj();
                task.setDeadLine(input);
            } else {
                return MESSAGE_INVALID_COMMAND;
            }
            storage.writeTasksToFile(allTasks);
        } catch (Exception e) {
            return MESSAGE_INVALID_COMMAND;
        }









        return MESSAGE_EDIT;
    }

    private String formatTasksForDisplay(ArrayList<Task> input) {
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
            Task task = allTasks.get(index);
            task.markAsComplete();
            storage.writeTasksToFile(allTasks);
            return String.format(MESSAGE_COMPLETE, task.getInfo());
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String undo() {
        // TODO check if this method actually works
        if (allTasksPreviousState == allTasks) {
            return MESSAGE_NO_UNDO;
        } else {
            allTasks = allTasksPreviousState;
            return MESSAGE_UNDO;
        }
    }

    private ArrayList<Task> search(String input) {
        // TODO check Task.getInfo() implementation
        ArrayList<Task> searchResults = new ArrayList<Task>();

        for (Task task : allTasks) {
            String taskInfo = task.getInfo();
            if (taskInfo.contains(input)) {
                searchResults.add(task);
            }
        }
        return searchResults;
    }

    private ArrayList<Task> getUncompletedTasks(ArrayList<Task> allTasks) {
        // TODO consider using Java 8's fancy new FP methods, stream()
        ArrayList<Task> completedTasks = new ArrayList<Task>();
        for (Task task : allTasks){
            if (!task.getTaskStatus()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }
    
    private String invalid() {
        return MESSAGE_INVALID_COMMAND;
    }

    private String exit() {
        return MESSAGE_EXIT;
    }

    private void updatePreviousState() {
        allTasksPreviousState = allTasks;
    }

    private void exitIfMissingArgs(String[] args) {
        if (args.length == 0) {
            System.err.println(ERROR_NO_FILE);
            System.exit(0);
        }
    }
}
