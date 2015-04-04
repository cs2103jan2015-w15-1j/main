package main.resources.view;

import main.java.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TaskBox extends HBox {
    private static final String LOCATION_TASK_BOX_FXML = "/view/TaskBox.fxml";
    
    private static final String KEYWORD_COMPLETE = "complete";
    private static final String KEYWORD_INCOMPLETE = "incomplete";
    private static final String KEYWORD_DELETE = "delete";

    private static final String ICON_DELETE = "\uf014";
    
    @FXML
    private Label index;

    @FXML
    private CheckBox checkbox;

    @FXML
    private Label description;

    @FXML
    private Button delete;

    public TaskBox(int idx, String desc) {
        loadFxml();
        initListenerAndFields(idx, desc);
    }

    public TaskBox(int idx, String desc, boolean completed) {
        loadFxml();
        checkbox.setSelected(completed);
        initListenerAndFields(idx, desc);
    }
    
    private void loadFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initListenerAndFields(int idx, String desc) {
        ChangeListener<Boolean> checkboxListener = initCheckboxListener(idx);
        EventHandler<ActionEvent> deleteListener = initDeleteListener(idx);

        initFxmlFields(idx, desc, checkboxListener, deleteListener);
    }
    
    private ChangeListener<Boolean> initCheckboxListener(int idx) {
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean oldVal,
                                Boolean newVal) {
                Controller controller = Controller.getInstance();
                if (newVal) {
                    controller.executeCommand(KEYWORD_COMPLETE + " " + idx);
                } else {
                    controller.executeCommand(KEYWORD_INCOMPLETE + " " + idx);
                }
            }
        };
        return listener;
    }

    private EventHandler<ActionEvent> initDeleteListener(int idx) {
        EventHandler<ActionEvent> deleteListener = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller controller = Controller.getInstance();
                controller.executeCommand(KEYWORD_DELETE + " " + idx);
            }
        };
        return deleteListener;
    }

    private void initFxmlFields(int idx,
                                String desc,
                                ChangeListener<Boolean> checkboxListener,
                                EventHandler<ActionEvent> deleteListener) {
        index.setText(idx + "");
        checkbox.selectedProperty().addListener(checkboxListener);
        description.setText(desc);
        delete.setText(ICON_DELETE);
        delete.setOnAction(deleteListener);
    }
}
