package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.resources.view.RootLayoutController;
import main.resources.view.TaskOverviewController;

import java.io.IOException;


public class MainApp extends Application {
	// ================================================================
	// Fields
	// ================================================================
	private Stage primaryStage;
	private BorderPane rootLayout;
	private TaskOverviewController taskOverviewController;
	private RootLayoutController rootLayoutController;

	// ================================================================
	// Methods
	// ================================================================
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Constructor
	 */
	public MainApp() {
	}

	/**
	 * Returns the main stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Veto");

		initRootLayout();
//		showTaskOverview();

		rootLayoutController.setTaskOverviewController(taskOverviewController);
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.`
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Provide a handle in MainApp to rootLayoutController
			rootLayoutController = loader.getController();

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
			taskOverviewController = loader.getController();
			taskOverviewController.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
