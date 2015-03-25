package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.Task;

public class DayBox extends VBox {
    @FXML private ListView<String> tasks;
    @FXML private Label hola;

    private ObservableList<String> taskData = FXCollections.observableArrayList();
    private String dayData;

    public DayBox(String foo) {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DayBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
        dayData = foo;

        taskData.add("hello");
        taskData.add("foo");
        taskData.add("bar");
        taskData.add("bye");
        taskData.add("baz");


    }

    @FXML
    private void initialize() {
        hola.setText("MONDAY");
        tasks.setItems(taskData);
    }
}
