import java.util.ArrayList;

public class Controller {
    private static final String MESSAGE_ADD = "Task has been successfully added. \n";
    private static final String MESSAGE_DELETE = "Task has been successfully deleted. \n";

    StorageStub storage;
    boolean timeToExit;
    ArrayList<Task> allTasks;


    public Controller(String[] args) {
        storage = new StorageStub();
        timeToExit = false;
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
                break;
            case SEARCH :
                break;
        }

        return null;
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
        
        return MESSAGE_DELETE;
    }

    private String editTask(String input) {
        return null;
    }

    private String displayTasks() {
        return null;
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

}