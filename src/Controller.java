public class Controller {
    Storage storage;
    boolean timeToExit;

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
    private String setSaveFileDest(String input) {
        return null;
    }

    private String addTask(String input) {
        return null;
    }

    private String deleteTask(String input) {
        return null;
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