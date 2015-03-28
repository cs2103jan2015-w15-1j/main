package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import main.java.Controller;

import java.io.IOException;
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
        history.add("");
        pointer = history.size();
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
            controller.executeCommand(userInput.getText());
            System.out.println(userInput.getText());
            history.add(userInput.getText());
            pointer = history.size();
            userInput.setText("");
        } else if (event.getCode() == KeyCode.DOWN) {
            if (pointer < history.size() - 1) {
                pointer++;
                userInput.setText(history.get(pointer));
            } else {
                userInput.setText("");
            }
        } else if (event.getCode() == KeyCode.UP) {
            if (pointer > 0) {
                pointer--;
            }
            userInput.setText(history.get(pointer));
        }
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
