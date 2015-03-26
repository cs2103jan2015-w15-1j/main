package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DayBox extends HBox {
//    @FXML
//    private ListView<String> tasks;

    @FXML
    private Label day;
    
    @FXML
    private Label date;

//    private ObservableList<String> taskData = FXCollections.observableArrayList();
    private String dayData;

    public DayBox(String foo, String date) {
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

//        taskData.add("task1");
//        taskData.add("task2");
//        taskData.add("task3");
//        taskData.add("task4");
//        taskData.add("task5");
//        taskData.add("task6");
//        taskData.add("task7");
//        taskData.add("task1");
//        taskData.add("task2");
//        taskData.add("task3");
//        taskData.add("task4");
//        taskData.add("task5");
//        taskData.add("task6");
//        taskData.add("task7");

        day.setText(dayData);
        this.date.setText("(" + date + ")");
//        tasks.setItems(taskData);
    }

}
