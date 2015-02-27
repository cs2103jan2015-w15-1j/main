import java.util.ArrayList;

public class Controller {
    private static final String MESSAGE_EMPTY = "There is currently no task. \n";
    private static final String MESSAGE_ADD = "Task has been successfully added. \n";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted. \n";
    private static final String MESSAGE_EDIT = "Task has been successfully edited. \n";
    private static final String MESSAGE_COMPLETE = "\"%s\" completed.";
    private static final String MESSAGE_EXIT = "Goodbye!";

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
                return setSaveFileDest(arguments);
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
        }
    }

    public boolean isTimeToExit() {
        return timeToExit;
    }

    // Private methods
    private boolean setSaveFileDest(String input) {
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
        int removalIndex = Integer.parseInt(input) - 1;
        allTasks.remove(removalIndex);
        storage.writeTasksToFile(allTasks);
        return MESSAGE_DELETE;
    }

    private String editTask(String input) {

        return MESSAGE_EDIT;
    }

    private String displayTasks() {
        if (allTasks.size() == 0) {
            return MESSAGE_EMPTY;
        }

        String display = "";

        int counter = 1;
        for (Task task : allTasks) {
            display += counter + ". " + task.getInfo();
        }

        return display;
    }

    private String completeTask(String input) {
        return null;
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