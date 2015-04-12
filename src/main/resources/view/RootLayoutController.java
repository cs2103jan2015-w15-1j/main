package main.resources.view;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import main.java.Command;
import main.java.Controller;
import main.java.Task;

import java.time.format.DateTimeFormatter;

//@author A0122081X
public class RootLayoutController extends BorderPane {

    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TextField userInput;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private Controller controller;
    private DisplayController displayController;

    private ArrayList<String> history;
    private int pointer;

    private ArrayList<String> commands;

    private final String ROOT_LAYOUT_LOCATION = "/view/RootLayout.fxml";
    private final String WELCOME_INPUT = "Enter your task here";
    private final String ONE_SPACING = " ";
    private final String EMPTY_STRING = "";
    private final String ALL_KEYWORD = "all";

    // formats the date for the date label, eg. 1 April
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");

    // formats the time for the time label, eg 2:00PM to 4:00PM
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");

    // ================================================================
    // Constructor
    // ================================================================
    public RootLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_LOCATION));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        displayController = DisplayController.getInstance();
        initVariablesForHistory();
        initAutoCompleteCommands();
        userInput.setText(WELCOME_INPUT);
    }

    // ================================================================
    // Public methods
    // ================================================================
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.D) {
            displayController.scrollDown();
        } else if (event.isControlDown() && event.getCode() == KeyCode.U) {
            displayController.scrollUp();
        } else if (event.getCode() == KeyCode.SPACE) {
            listenForEdit(event);
        } else if (event.getCode() == KeyCode.ENTER) {
            handleUserInput();
        } else if (event.getCode() == KeyCode.UP ||
                   event.getCode() == KeyCode.DOWN) {
            event.consume(); // nullifies the default behavior of UP and DOWN on a TextArea
            handleGetPastCommands(event);
        } else if (event.getCode() == KeyCode.TAB) {
            handleCommandAutoComplete();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            displayController.hideOverlays();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // ================================================================
    // Private Methods
    // ================================================================
    private void handleUserInput() {
        String inputString = userInput.getText();
        if (containsEditAll(inputString)) {
            ArrayList<LocalDate> exceptionDates = extractExceptionDates(inputString);
            if (exceptionDates != null && exceptionDates.size() > 0) {
                inputString = buildExceptStringToAdd(inputString, exceptionDates);
            }
        }
        controller.executeCommand(inputString);
        updateHistory();
        updateUserInput(EMPTY_STRING);
    }

    private boolean containsEditAll(String input) {
        String[] output = input.split(ONE_SPACING);

        return output.length >= 3 &&
                output[0].equalsIgnoreCase(Command.Type.EDIT.toString()) &&
                output[1].equalsIgnoreCase(ALL_KEYWORD);
    }

    private ArrayList<LocalDate> extractExceptionDates(String inputString) {
        int editIndex = getEditIndex(inputString, 2);
        Task task = Controller.getInstance().getDisplayedTasks().get(editIndex - 1);
        ArrayList<LocalDate> exceptionDates = task.getExceptionDates();

        return exceptionDates;
    }

    private String buildExceptStringToAdd(String inputString, ArrayList<LocalDate> exceptionDates) {
        StringBuilder newInputString = new StringBuilder(inputString);
        newInputString.append("except ");
        for (LocalDate date : exceptionDates) {
            newInputString.append(date.toString());
            newInputString.append(",");
        }
        String output = newInputString.toString();

        return output;
    }
    
    //@author A0121520A
    private void handleCommandAutoComplete() {
        String autoCompletedCommand = getAutoCompletedCommand(userInput.getText());
        updateUserInput(autoCompletedCommand);
    }

    private void handleGetPastCommands(KeyEvent event) {
        String pastCommand = getPastCommandFromHistory(event.getCode());
        updateUserInput(pastCommand);
    }

    private void updateUserInput(String newInput) {
        userInput.setText(newInput);
        userInput.end();
    }


    // ================================================================
    // Methods to handle command auto-complete
    // ================================================================
    private void initAutoCompleteCommands() {
        commands = Command.getAllCommandTypes();
        commands.remove(Command.Type.INVALID.toString());
    }

    private String getAutoCompletedCommand(String text) {
        ArrayList<String> splitText = generateSplitText(text);
        if (hasOnlyOneWord(splitText)) {
            String firstWord = getFirstWord(splitText);
            return findSuitableCommand(text, firstWord);
        } else {
            return text;
        }
    }

    private ArrayList<String> generateSplitText(String text) {
        ArrayList<String> splitText = new ArrayList<String>(Arrays.asList(text.split(ONE_SPACING)));
        return splitText;
    }

    private boolean hasOnlyOneWord(ArrayList<String> splitText) {
        return splitText.size() == 1;
    }

    private String getFirstWord(ArrayList<String> splitText) {
        return splitText.get(0);
    }

    private String findSuitableCommand(String text, String firstWord) {
        for (String command : commands) {
            if (isValidLengths(firstWord, command) &&
                isWordAtStartOfCommand(firstWord, command)) {
                return command.toLowerCase() + ONE_SPACING;
            }
        }
        return text;
    }

    private boolean isValidLengths(String firstWord, String command) {
        return firstWord.length() <= command.length();
    }

    private boolean isWordAtStartOfCommand(String firstWord, String command) {
        return command.substring(0, firstWord.length())
                      .equalsIgnoreCase(firstWord);
    }


    // ================================================================
    // Methods to handle history of user entered commands
    // ================================================================
    private void initVariablesForHistory() {
        history = new ArrayList<String>();
        history.add(EMPTY_STRING);
        history.add(EMPTY_STRING);
        pointer = history.size() - 1;
    }

    private void updateHistory() {
        pointer = history.size();
        history.add(pointer - 1, userInput.getText());
    }

    private String getPastCommandFromHistory(KeyCode code) {
        if (code == KeyCode.DOWN) {
            return getNextCommand();
        } else if (code == KeyCode.UP) {
            return getPreviousCommand();
        } else {
            return EMPTY_STRING;
        }
    }

    private String getPreviousCommand() {
        if (pointer > 0) {
            pointer--;
        }
        return history.get(pointer);
    }

    private String getNextCommand() {
        if (pointer < history.size() - 1) {
            pointer++;
        }
        return history.get(pointer);
    }

    //@author A0122081X
    // ================================================================
    // Methods to handle edit auto-complete
    // ================================================================
    private enum EditType {
        INDIVIDUAL, ALL;
    }

    private void listenForEdit(KeyEvent event) {
        // editIndexPosition is the index of the integer after the edit keyword in the
        // array input array
        int editIndexPosition = 0;
        String inputString = userInput.getText();
        EditType type = null;

        if (isEditIndividualFormat(inputString)) {
            editIndexPosition = 1;
            type = EditType.INDIVIDUAL;
        } else if(isEditAllFormat(inputString)) {
            editIndexPosition = 2;
            type = EditType.ALL;
        }

        // Auto-complete only if there is a valid editIndexPosition
        if (editIndexPosition > 0) {
            int index = getEditIndex(inputString, editIndexPosition);
            autoCompleteEdit(index, type);
        }
    }

    private boolean isEditIndividualFormat(String input) {
        String[] output = input.split(ONE_SPACING);

        if (output.length == 2 &&
                output[0].equalsIgnoreCase(Command.Type.EDIT.toString())) {
            // Check for whether it's in the format "edit <int>"
            try {
                Integer.parseInt(output[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    private boolean isEditAllFormat(String input) {
        String[] output = input.split(ONE_SPACING);

        if (output.length == 3 &&
                output[0].equalsIgnoreCase(Command.Type.EDIT.toString()) &&
                output[1].equalsIgnoreCase(ALL_KEYWORD)) {
            try {
                Integer.parseInt(output[2]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    /*
    *
    *  If invalid, returns 0.
    *  Any valid format returns an integer > 0.
    *  If is individual, returns 1.
    *  If is all, returns 2.
    *
     */
    private int isValidEditFormat(String input) {
        String[] output = input.split(ONE_SPACING);

        // Check for edit all keyword
        if (output.length == 3 &&
            output[0].equalsIgnoreCase(Command.Type.EDIT.toString()) &&
            output[1].equalsIgnoreCase(ALL_KEYWORD)) {
            try {
                Integer.parseInt(output[2]);
                return 2;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        // Check for edit keyword and length
        if (output.length == 2 &&
            output[0].equalsIgnoreCase(Command.Type.EDIT.toString())) {
            // Check for whether it's in the format "edit <int>"
            try {
                Integer.parseInt(output[1]);
                return 1;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

    private int getEditIndex(String input, int editIndexPosition) {
        String[] output = input.split(ONE_SPACING);
        return Integer.parseInt(output[editIndexPosition]);
    }

    private void autoCompleteEdit(int index, EditType type) {
        ObservableList<Task> displayedTasks = controller.getDisplayedTasks();
        if (index < displayedTasks.size() + 1) { // check if supplied index is within displayedTasks' range
            Task task = displayedTasks.get(index - 1);
            Task.Type taskType = task.getType();

            if (type.equals(EditType.ALL)) {
                autoCompleteRecurringTask(userInput, task);
                return;
            } else if (type.equals(EditType.INDIVIDUAL)) {
                userInput.appendText(ONE_SPACING + task.getDescription());
                switch (taskType) {
                    case FLOATING:
                        break;
                    case DEADLINE:
                        autoCompleteDeadlineTask(userInput, task);
                        break;
                    case TIMED:
                        autoCompleteTimedTask(userInput, task);
                        break;
                }
                userInput.end();
            }
        }
    }

    private void autoCompleteRecurringTask(TextField userInput, Task task) {
        userInput.appendText(ONE_SPACING + task.getRawInfo());
        userInput.end();
    }

    private void autoCompleteDeadlineTask(TextField userInput, Task task) {
        userInput.appendText(ONE_SPACING);
        if (task.getStartTime() != null) {
            userInput.appendText("by " + task.getStartTime().format(timeFormatter) + ONE_SPACING);
        }
        userInput.appendText(task.getDate().format(dateFormatter));
    }

    private void autoCompleteTimedTask(TextField userInput, Task task) {
        userInput.appendText(ONE_SPACING);
        userInput.appendText(task.getStartTime()
                .format(timeFormatter) +
                ONE_SPACING +
                "to " +
                task.getEndTime()
                        .format(timeFormatter) +
                ONE_SPACING +
                task.getDate().format(dateFormatter));
    }
}
