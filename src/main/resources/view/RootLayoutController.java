package main.resources.view;

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

    /**
     * Initializes the root layout class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        userInput.setText("Enter your task here");
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
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

    private void updateHistory() {
        pointer = history.size();
        history.add(pointer - 1, userInput.getText());
    }

}
