package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.MainApp;
import main.java.Storage;
import main.java.Task;

import java.util.ArrayList;

public class TaskOverviewController {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> taskDescription;
    @FXML
    private TableColumn<Task, String> taskDeadline;
    @FXML
    private TableColumn<Task, Integer> taskIndex;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private ObservableList<Task> taskData = FXCollections.observableArrayList();
    private MainApp mainApp;


    // ================================================================
    // Methods
    // ================================================================
    /**
     * The constructor is called before the initialize() method.
     */
    public TaskOverviewController() {
        Storage storage = new Storage();
        String saveFileName = storage.getSaveFileName();

        ArrayList<Task> allTasks = storage.readTasksFromFile();

        for (Task task : allTasks) {
            taskData.add(task);
        }
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Populating the TableColumns (but are not directly shown in the UI yet)
        taskDescription.setCellValueFactory(
                cellData -> cellData.getValue().getTaskDesc());
        taskDeadline.setCellValueFactory(
                cellData -> cellData.getValue().getStringPropertyTaskDate());

        // Add the observable list data to the table
        taskTable.setItems(taskData);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
