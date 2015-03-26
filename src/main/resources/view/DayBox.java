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

        day.setText(dayData);
        if (!date.isEmpty()) {
            this.date.setText("(" + date + ")");
        } else {
            this.date.setText("");
        }
    }

}
