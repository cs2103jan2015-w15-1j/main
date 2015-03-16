package main.java;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.resources.view.TaskOverviewController;

import java.io.IOException;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	/**
	 * The data as an ObservableList of Tasks
	 */
	private ObservableList<Task> taskData = FXCollections.observableArrayList();

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Constructor
	 */
	public MainApp() {
		taskData.add(new Task("do this by 12 apr"));
		taskData.add(new Task("do that by 13 apr"));
		taskData.add(new Task("do foo by 15 apr"));
		taskData.add(new Task("do bar by 14 apr"));
		taskData.add(new Task("do asdfg by 10 apr"));
		taskData.add(new Task("do thizxccs by 11 apr"));
	}

	/**
	 * Returns the data as an observable list of Tasks
	 */
	public ObservableList<Task> getTaskData() {
		return taskData;
	}

	/**
	 * Returns the main stage
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("main.java.Veto");

		initRootLayout();
		showTaskOverview();
	}



	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the task overview inside the root layout.
	 */
	public void showTaskOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/view/TaskOverview.fxml"));
			AnchorPane taskOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(taskOverview);

			// Give the controller access to the main app.
			TaskOverviewController taskOverviewController = loader.getController();
			taskOverviewController.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
