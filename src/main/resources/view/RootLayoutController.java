package main.resources.view;

import javafx.collections.ObservableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
    
    private ArrayList<String> history;
    private int pointer;
    
    private ArrayList<String> commands; 

    private final String ROOT_LAYOUT_LOCATION = "/view/RootLayout.fxml";
    private final String WELCOME_INPUT = "Enter your task here";
    private final String EDIT_COMMAND = "edit";
    private final String ONE_SPACING = " ";
    private final String EMPTY_STRING = "";

    // formats the date for the date label, eg. 1 April
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");

    // formats the time for the time label, eg 2:00PM to 4:00PM
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");

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

        initVariablesForHistory();
        initCommands();
        userInput.setText(WELCOME_INPUT);
    }
    
    private void initCommands() {
        commands = new ArrayList<String>(); 
        commands.add("add");
        commands.add("complete");
        commands.add("delete");
        commands.add("display");
        commands.add("edit");
        commands.add("incomplete");
        commands.add("search");
        commands.add("undo");
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // ================================================================
    // Public methods
    // ================================================================
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            listenForEdit(event);
        } else if (event.getCode() == KeyCode.ENTER) {
            controller.executeCommand(userInput.getText());
            updateHistory();
            userInput.setText("");          
        } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
            String pastCommand = getPastCommandFromHistory(event.getCode());
            userInput.setText(pastCommand);
        } else if (event.getCode() == KeyCode.TAB) {
            String autoCompletedCommand = getAutoCompletedCommand(userInput.getText());
            userInput.setText(autoCompletedCommand + " ");
            userInput.positionCaret(autoCompletedCommand.length() + 1);
        }
    }

    private String getAutoCompletedCommand(String text) {
        ArrayList<String> splitText = new ArrayList<String>(Arrays.asList(text.split(" ")));
        String lastWord = splitText.get(splitText.size() - 1);

        for (String command : commands) {
            if (lastWord.length() <= command.length() && command.substring(0, lastWord.length()).equals(lastWord)) {
                splitText.set(splitText.size() - 1, command);
                return StringUtils.join(splitText, " ");
            }
        }
        return text;
    }

    
    // ================================================================
    // Methods to handle history of user entered commands
    // ================================================================    

    private void initVariablesForHistory() {
        history = new ArrayList<String>();
        history.add("");
        history.add("");
        pointer = history.size() - 1;
    }

    private String getPastCommandFromHistory(KeyCode code) {
        String command = "";
        if (code == KeyCode.DOWN) {
            if (pointer < history.size() - 1) {
                pointer++;
                command = history.get(pointer);
            }
        } else if (code == KeyCode.UP) {
            if (pointer > 0) {
                pointer--;
            }
            command = history.get(pointer);
        }
        return command;
    }

    // ================================================================
    // Private methods
    // ================================================================
    private void listenForEdit(KeyEvent event) {
//        System.out.println(userInput.getText());
//        System.out.println(isValidEditFormat(userInput.getText()));
        if (isValidEditFormat(userInput.getText())) {
            int index = getEditIndex(userInput.getText());
            autoCompleteEdit(index);
        }
    }

    private boolean isValidEditFormat(String input) {
        String[] output = input.split(ONE_SPACING);

        // Check for edit keyword and length
        if (output.length == 2 && output[0].equals(EDIT_COMMAND)) {
            // Check for whether it's in the format "edit <int>"
            try {
                int index = Integer.parseInt(output[1]);
                return true;
            } catch (Exception e) {
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
        if (index < displayedTasks.size()+1) {
            Task task = displayedTasks.get(index-1);

            Task.Type taskType = task.getType();

            userInput.appendText(ONE_SPACING + task.getDescription());
            switch (taskType) {
                case FLOATING :
                    break;
                case DEADLINE :
                    userInput.appendText(ONE_SPACING);
                    userInput.appendText(task.getDate().toString());
                    break;
                case TIMED :
                    userInput.appendText(ONE_SPACING);
                    userInput.appendText(task.getDate().toString() + ONE_SPACING +
                            task.getStartTime().format(timeFormatter) + ONE_SPACING +
                            "to " +
                            task.getEndTime().format(timeFormatter));
                    break;
            }
            userInput.end();
        }
    }



    private void updateHistory() {
        pointer = history.size();
        history.add(pointer - 1, userInput.getText());
    }


}
