package main.java;

public class Command {
    public static enum Type {
        ADD, DELETE, EDIT, DISPLAY, COMPLETE, INCOMPLETE, UNDO, SEARCH, EXIT, SETSAVEFILE,
        INVALID, CLEAR, HELP
    };

    private static final int PARAM_POSITION_COMMAND = 0;
    private static final int PARAM_START_POSITION_ARGUMENT = 1;

    private Type commandType;
    private String userCommand;
    private String arguments;

    public Command(String input) {
        String[] parameters = splitUserInput(input);

        userCommand = getUserCommand(parameters);
        arguments = getUserArguments(parameters);

        commandType = determineCommandType(userCommand);
    }

    // Public getters
    public Type getCommandType() {
        return commandType;
    }

    public String getArguments() {
        return arguments;
    }


    // Private methods
    private Type determineCommandType(String userCommand2) {
        switch (userCommand.toLowerCase()) {
            case "add" :
                return Type.ADD;
            case "delete" :
                return Type.DELETE;
            case "edit" :
                return Type.EDIT;
            case "display" :
                return Type.DISPLAY;
            case "complete" :
                return Type.COMPLETE;
            case "incomplete" :
                return Type.INCOMPLETE;
            case "undo" :
                return Type.UNDO;
            case "search" :
                return Type.SEARCH;
            case "help" :
            	return Type.HELP;
            case "exit" :
                return Type.EXIT;
            case "set" :
                return Type.SETSAVEFILE;
            case "clear" :
            	return  Type.CLEAR;
            default :
                return Type.INVALID;
        }
    }

    private String getUserCommand(String[] parameters) {
        return parameters[PARAM_POSITION_COMMAND];
    }

    private String getUserArguments(String[] parameters) {
        StringBuilder builder = new StringBuilder();
        for (int i = PARAM_START_POSITION_ARGUMENT; i < parameters.length; i++) {
            builder.append(parameters[i]);
            builder.append(" ");
        }
        return builder.toString().trim();
    }


    private String[] splitUserInput(String input) {
        return input.trim().split("\\s+");
    }
}
