package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class RootLayoutController extends BorderPane {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TextField userInput;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private TaskOverviewController taskOverviewController;

    private final String ROOT_LAYOUT_LOCATION = "/view/RootLayout.fxml";

    public RootLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_LOCATION));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            taskOverviewController.executeCommand(userInput.getText());
            System.out.println(userInput.getText());
            userInput.setText("");
        }
    }

    public void setTaskOverviewController(TaskOverviewController controller) {
        this.taskOverviewController = controller;
    }
}
