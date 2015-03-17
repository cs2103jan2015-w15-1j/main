package main.resources.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.MainApp;
import main.java.Task;

public class RootLayoutController {
    @FXML
    private TextField userInput;

    // Reference to the main application
    private MainApp mainApp;

    /**
     * Initializes the root layout class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        userInput.setText("Enter your task here");
    }

    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Task task = new Task(userInput.getText());
            System.out.println(userInput.getText());
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
