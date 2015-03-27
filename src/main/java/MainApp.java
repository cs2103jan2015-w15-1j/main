package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.resources.view.RootLayoutController;
import main.resources.view.Display;

public class MainApp extends Application {
	// ================================================================
	// Fields
	// ================================================================
	private Stage primaryStage;
	private Display display;
	private RootLayoutController rootLayoutController;
	private Controller controller;

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
		initDisplay();
		initController();

		// Provide a TOC handle inside of RLC so that user input can be passed to TOC from RLC
		rootLayoutController.setController(controller);

		// Provide a display handle in controller so that controller can pass message to display
		controller.setDisplay(display);

		// Legacy code
		rootLayoutController.setDisplay(display);
	}

	public void initRootLayout() {
		rootLayoutController = new RootLayoutController();
		primaryStage.setScene(new Scene(rootLayoutController));
		primaryStage.show();
	}

	public void initDisplay() {
		display = new Display();
		rootLayoutController.setCenter(display);
	}

	public void initController() {
		controller = new Controller();
		controller.setDisplay(display);
		controller.onloadDisplay();
	}
}
