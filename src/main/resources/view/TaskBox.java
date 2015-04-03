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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean oldVal,
                                Boolean newVal) {
//                description.setStyle("-fx-strikethrough: true;");
                Controller controller = Controller.getInstance();
                controller.executeCommand("complete " + idx);
            }
        };

        index.setText(idx + "");
        checkbox.selectedProperty().addListener(listener);
        description.setText(desc);
        delete.setText("X");
    }
    
    public TaskBox(int idx, String desc, boolean completed) {
        this(idx, desc);
        checkbox.setDisable(true);
    }
}
