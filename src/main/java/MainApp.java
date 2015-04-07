package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    private static final int STAGE_MINIMUM_HEIGHT = 650;
    private static final int STAGE_MINIMUM_WIDTH = 650;

	// ================================================================
	// Methods
	// ================================================================
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image("/images/icon.png")); 
		this.primaryStage.setTitle("Veto");  // --> This represent the name on the Windows Title Bar
		this.primaryStage.setMinWidth(STAGE_MINIMUM_WIDTH);
		this.primaryStage.setMinHeight(STAGE_MINIMUM_HEIGHT);
		
		initRootLayout();
		initDisplay();
		initController();

		// Provide a TOC handle inside of RLC so that user input can be passed to TOC from RLC
		rootLayoutController.setController(controller);

		// Provide a display handle in controller so that controller can pass message to display
		controller.setDisplay(display);
		controller.setStage(this.primaryStage);
	}

	public void initRootLayout() {
		rootLayoutController = new RootLayoutController();
		primaryStage.setScene(new Scene(rootLayoutController));
		primaryStage.show();
	}

	public void initDisplay() {
		display = Display.getInstance();
		rootLayoutController.setCenter(display);
	}

	public void initController() {
		controller = Controller.getInstance();
		controller.setDisplay(display);
		controller.onloadDisplay();
	}
}
