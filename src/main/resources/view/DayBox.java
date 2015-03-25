package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class DayBox extends VBox {
    @FXML
    private ListView<String> tasks;

    @FXML
    private Label day;

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

        taskData.add("task1");
        taskData.add("task2");
        taskData.add("task3");
        taskData.add("task4");
        taskData.add("task5");
        taskData.add("task6");
        taskData.add("task7");
        taskData.add("task1");
        taskData.add("task2");
        taskData.add("task3");
        taskData.add("task4");
        taskData.add("task5");
        taskData.add("task6");
        taskData.add("task7");

        day.setText(dayData);
        tasks.setItems(taskData);
    }

}
