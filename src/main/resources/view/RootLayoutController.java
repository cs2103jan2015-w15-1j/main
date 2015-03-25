package main.resources.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.MainApp;
import main.java.Task;

public class RootLayoutController {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TextField userInput;

    @FXML
    private Label foo;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private MainApp mainApp;
    private TaskOverviewController taskOverviewController;

    /**
     * Initializes the root layout class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        userInput.setText("Enter your task here");
        foo.setText("hola amigos");
    }

    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            taskOverviewController.executeCommand(userInput.getText());
            System.out.println(userInput.getText());
            userInput.setText("");
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

    public void setTaskOverviewController(TaskOverviewController controller) {
        this.taskOverviewController = controller;
    }
}
