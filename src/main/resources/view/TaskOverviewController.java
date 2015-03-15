package main.resources.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import main.java.MainApp;
import main.java.Task;

public class TaskOverviewController {
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> taskDescription;
    @FXML
    private TableColumn<Task, String> taskDeadline;

    // Reference to the main application
    private MainApp mainApp;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        taskDescription.setCellValueFactory(
                cellData -> cellData.getValue().getTaskDesc());
        taskDeadline.setCellValueFactory(
                cellData -> cellData.getValue().getTaskDesc());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        taskTable.setItems(mainApp.getTaskData());
    }


    public TaskOverviewController() {
//        textLabel.setText("hello world!");

    }



}
