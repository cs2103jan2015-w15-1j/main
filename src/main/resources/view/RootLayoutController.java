package main.resources.view;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import main.java.Command;
import main.java.Controller;
import main.java.Task;

import java.time.format.DateTimeFormatter;

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
    private Display display;

    private ArrayList<String> history;
    private int pointer;

    private ArrayList<String> commands;

    private final String ROOT_LAYOUT_LOCATION = "/view/RootLayout.fxml";
    private final String WELCOME_INPUT = "Enter your task here";
    private final String ONE_SPACING = " ";
    private final String EMPTY_STRING = "";

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

        display = Display.getInstance();
        initVariablesForHistory();
        initAutoCompleteCommands();
        userInput.setText(WELCOME_INPUT);
    }

    // ================================================================
    // Experimental
    // ================================================================
    final BooleanProperty ctrlPressed = new SimpleBooleanProperty(false);
    final BooleanProperty upPresed = new SimpleBooleanProperty(false);
    final BooleanProperty downPressed = new SimpleBooleanProperty(false);
    final BooleanBinding ctrlAndUpPressed = ctrlPressed.and(upPresed);
    final BooleanBinding ctrlAndDownPressed = ctrlPressed.and(downPressed);


    // ================================================================
    // Public methods
    // ================================================================
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.D) {
            display.scrollDown();
        } else if (event.isControlDown() && event.getCode() == KeyCode.U) {
            display.scrollUp();
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
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }


    // ================================================================
    // Private Methods
    // ================================================================
    private void handleUserInput() {
        controller.executeCommand(userInput.getText());
        updateHistory();
        updateUserInput(EMPTY_STRING);
    }

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
    // Methods to handle command autocomplete
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


    // ================================================================
    // Methods to handle edit autocomplete
    // ================================================================    
    private void listenForEdit(KeyEvent event) {
        if (isValidEditFormat(userInput.getText())) {
            int index = getEditIndex(userInput.getText());
            autoCompleteEdit(index);
        }
    }

    private boolean isValidEditFormat(String input) {
        String[] output = input.split(ONE_SPACING);

        // Check for edit keyword and length
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

    private int getEditIndex(String input) {
        String[] output = input.split(ONE_SPACING);
        return Integer.parseInt(output[1]);
    }

    private void autoCompleteEdit(int index) {
        ObservableList<Task> displayedTasks = controller.getDisplayedTasks();
        if (index < displayedTasks.size() + 1) {
            Task task = displayedTasks.get(index - 1);
            Task.Type taskType = task.getType();

            if (task.getId() != null) {
                userInput.appendText(ONE_SPACING + task.getRawInfo());
                userInput.end();
                return;
            }

            userInput.appendText(ONE_SPACING + task.getDescription());

            switch (taskType) {
                case FLOATING :
                    break;
                case DEADLINE :
                    userInput.appendText(ONE_SPACING);
                    if (task.getStartTime() != null) {
                        userInput.appendText("by " + task.getStartTime().format(timeFormatter) + ONE_SPACING);
                    }
                    userInput.appendText(task.getDate().format(dateFormatter));
                    break;
                case TIMED :
                    userInput.appendText(ONE_SPACING);
                    userInput.appendText(task.getDate().format(dateFormatter) +
                                         ONE_SPACING +
                                         task.getStartTime()
                                             .format(timeFormatter) +
                                         ONE_SPACING +
                                         "to " +
                                         task.getEndTime()
                                             .format(timeFormatter));
                    break;
            }
            userInput.end();
        }
    }

}
