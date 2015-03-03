import java.util.ArrayList;

public class Controller {
    private static final int PARAM_POSITION_FILENAME = 0;
    
    private static final String MESSAGE_SAVE_FILE_READY = "Welcome to Veto. %s is ready for use.";
    
    private static final String MESSAGE_EMPTY = "There is currently no task.\n";
    private static final String MESSAGE_ADD = "Task has been successfully added.\n";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted.\n";
    private static final String MESSAGE_EDIT = "Task has been successfully edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";
    private static final String MESSAGE_UNDO = "Last command has been undone. \n";


    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
    private static final String MESSAGE_NO_UNDO = "Unable to undo. \n";

    private static final String DISPLAY_LINE = "%d. %s\n";
    
    private static final String ERROR_NO_FILE = "No file in argument";

    String saveFileName;
    Storage storage;
    boolean timeToExit;
    ArrayList<Task> allTasks;
    ArrayList<Task> allTasksPreviousState;


    public Controller(String[] args) {
        exitIfMissingArgs(args);
        saveFileName = getFileNameFromArgs(args);
        storage = new Storage(saveFileName);
        timeToExit = false;
        allTasks = storage.getTasksFromFile();
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
                if (Boolean.parseBoolean(setSaveFileDest(input))) {
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
                return formatTasksForDisplay(allTasks);
            case COMPLETE :
                updatePreviousState();
                return completeTask(arguments);
            case UNDO :
                return undo();
            case SEARCH :
                ArrayList<Task> searchResults = search(arguments);
                return formatTasksForDisplay(searchResults);
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
    
    private String setSaveFileDest(String input) {
        return storage.setSaveFileDest(input);
    }

    private String addTask(String input) {
        Task task = new Task(input);
        allTasks.add(task);
        storage.writeTasksToFile(allTasks);
        return MESSAGE_ADD;
    }

    private String deleteTask(String input) {
        // ArrayList is 0-indexed, but Tasks are displayed to users as 1-indexed
        try {
            int removalIndex = Integer.parseInt(input) - 1;
            allTasks.remove(removalIndex);
            storage.writeTasksToFile(allTasks);
            return MESSAGE_DELETE;
        } catch (NumberFormatException e) {
            return MESSAGE_INVALID_COMMAND;
        }
    }

    private String editTask(String input) {
        // TODO need to think this through
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
        if (allTasksPreviousState.equals(allTasks)) {
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
            if (input.equals(taskInfo)) {
                searchResults.add(task);
            }
        }
        return searchResults;
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
