import java.util.ArrayList;

public class Controller {
    private static final String MESSAGE_EMPTY = "There is currently no task.\n";
    private static final String MESSAGE_ADD = "Task has been successfully added.\n";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted.\n";
    private static final String MESSAGE_EDIT = "Task has been successfully edited.\n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_EXIT = "Goodbye!";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";

    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";

    private static final String DISPLAY_LINE = "%d. %s\n";

    StorageStub storage;
    boolean timeToExit;
    ArrayList<Task> allTasks;


    public Controller(String[] args) {
        storage = new StorageStub();
        timeToExit = false;
        allTasks = storage.getTasksFromFile();
    }

    // Public methods
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
                return addTask(arguments);
            case DELETE :
                return deleteTask(arguments);
            case EDIT :
                return editTask(arguments);
            case DISPLAY :
                return displayTasks();
            case COMPLETE :
                return completeTask(arguments);
            case UNDO :
                return undo();
            case SEARCH :
                return search(arguments);
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
    private String setSaveFileDest(String input) {
        return storage.setSaveFileDest(input);
    }

    private String addTask(String input) {
        Task task = new Task(input);
        // TODO check if task is valid.
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

        return MESSAGE_EDIT;
    }

    private String displayTasks() {
        if (allTasks.isEmpty()) {
            return MESSAGE_EMPTY;
        }

        String display = "";

        int counter = 1;
        for (Task task : allTasks) {
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
        return null;
    }

    private String search(String input) {
        return null;
    }

    private String exit() {
        return MESSAGE_EXIT;
    }
}