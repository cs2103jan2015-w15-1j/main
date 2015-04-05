package main.resources.view;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import main.java.Controller;
import main.java.Task;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RootLayoutController extends BorderPane {

    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TextField userInput;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private Display display;
    private Controller controller;
    
    private ArrayList<String> history;
    private int pointer;

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
        
        history = new ArrayList<String>();
        history.add(EMPTY_STRING);
        history.add(EMPTY_STRING);
        pointer = history.size() - 1;

        userInput.setText(WELCOME_INPUT);
    }

    // ================================================================
    // Public methods
    // ================================================================
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            listenForEdit(event);
        } else if (event.getCode() == KeyCode.ENTER) {
            String feedback = controller.executeCommand(userInput.getText());
//            System.out.println(userInput.getText());
            pointer = history.size();
            history.add(pointer - 1, userInput.getText());
            userInput.setText(EMPTY_STRING);
        } else if (event.getCode() == KeyCode.DOWN) {
            if (pointer < history.size() - 1) {
                pointer++;
                userInput.setText(history.get(pointer));
            } else {
                userInput.setText(EMPTY_STRING);
            }
//            System.out.println("down " + pointer);
        } else if (event.getCode() == KeyCode.UP) {
            if (pointer > 0) {
                pointer--;
            }
//            System.out.println("up " + pointer);
            userInput.setText(history.get(pointer));
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
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


}
