package main.resources.view;

import main.java.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    public TaskBox(int index, String description) {
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
                System.out.println(index + " " + newVal);
            }
        };

        this.index.setText(index + "");
        checkbox.selectedProperty().addListener(listener);
        this.description.setText(description);

    }
}
