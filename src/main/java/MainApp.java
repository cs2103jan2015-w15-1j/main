package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.resources.view.RootLayoutController;
import main.resources.view.TaskOverviewController;

public class MainApp extends Application {
	// ================================================================
	// Fields
	// ================================================================
	private Stage primaryStage;
	private TaskOverviewController taskOverviewController;
	private RootLayoutController rootLayoutController;

	// ================================================================
	// Methods
	// ================================================================
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Veto");

		initRootLayout();
		showTaskOverview();

		// Provide TOC handle in RLC so that user input can be passed to TOC
		rootLayoutController.setTaskOverviewController(taskOverviewController);
	}

	public void initRootLayout() {
		rootLayoutController = new RootLayoutController();
		primaryStage.setScene(new Scene(rootLayoutController));
		primaryStage.show();
	}

	public void showTaskOverview() {
		taskOverviewController = new TaskOverviewController();
		rootLayoutController.setCenter(taskOverviewController);
	}
}
