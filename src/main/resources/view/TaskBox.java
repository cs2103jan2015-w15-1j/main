package main.resources.view;

import main.java.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TaskBox extends HBox {
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

        ChangeListener<Boolean> listener = initCheckboxListener(idx);

        initFxmlFields(idx, desc, listener);
    }

    public TaskBox(int idx, String desc, boolean completed) {
        loadFxml();
        checkbox.setSelected(completed);

        ChangeListener<Boolean> listener = initCheckboxListener(idx);

        initFxmlFields(idx, desc, listener);
    }

    private void loadFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ChangeListener<Boolean> initCheckboxListener(int idx) {
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean oldVal,
                                Boolean newVal) {
                Controller controller = Controller.getInstance();
                if (newVal) {
                    controller.executeCommand("complete " + idx);
                } else {
                    controller.executeCommand("incomplete " + idx);
                }
            }
        };
        return listener;
    }

    private void initFxmlFields(int idx,
                                String desc,
                                ChangeListener<Boolean> listener) {
        index.setText(idx + "");
        checkbox.selectedProperty().addListener(listener);
        description.setText(desc);
        delete.setText("X");
    }
}
