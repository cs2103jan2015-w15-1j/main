package main.resources.view;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import java.io.IOException;
import java.util.ArrayList;

public class RootLayoutController extends BorderPane {

    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TextField userInput;

    @FXML
    private Label feedbackLabel;
    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private Display display;
    private Controller controller;
    
    private ArrayList<String> history;
    private int pointer;
    
    private Timeline timeline;

    private final String ROOT_LAYOUT_LOCATION = "/view/RootLayout.fxml";
    private static final int FEEDBACK_FADE_IN_MILLISECONDS = 500;
    private static final int FEEDBACK_FADE_OUT_MILLISECONDS = 2000;
    private static final int FEEDBACK_DISPLAY_SECONDS = 10;


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
        history.add("");
        pointer = history.size() - 1;

    }

    /**
     * Initializes the root layout class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        userInput.setText("Enter your task here");
        timeline = new Timeline();
        addFeedback("Welcome to Veto!");
    }

    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String feedback = controller.executeCommand(userInput.getText());
//            System.out.println(feedback);
            addFeedback(feedback);
//            System.out.println(userInput.getText());
            pointer = history.size();
            history.add(pointer - 1, userInput.getText());
            userInput.setText("");            
        } else if (event.getCode() == KeyCode.DOWN) {
            if (pointer < history.size() - 1) {
                pointer++;
                userInput.setText(history.get(pointer));
            } else {
                userInput.setText("");
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

    private void addFeedback(String feedback) {
        feedbackLabel.setOpacity(0);
        feedbackLabel.setText(feedback);
        
        FadeTransition fadein = new FadeTransition(new Duration(FEEDBACK_FADE_IN_MILLISECONDS));
        fadein.setNode(feedbackLabel);
        fadein.setToValue(1.0);
        
        FadeTransition fadeout = new FadeTransition(new Duration(FEEDBACK_FADE_OUT_MILLISECONDS));
        fadeout.setNode(feedbackLabel);
        fadeout.setToValue(0);
        
        timeline.stop();

        timeline.getKeyFrames()
                .add(new KeyFrame(Duration.seconds(0),
                                  new EventHandler<ActionEvent>() {
                                      @Override
                                      public void handle(ActionEvent event) {
                                          
                                          fadein.play();
                                      }
                                  }));
        timeline.getKeyFrames()
                .add(new KeyFrame(Duration.seconds(FEEDBACK_DISPLAY_SECONDS),
                                  new EventHandler<ActionEvent>() {
                                      @Override
                                      public void handle(ActionEvent event) {
                                          fadeout.play();
                                      }
                                  }));
        

        timeline.play();
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
