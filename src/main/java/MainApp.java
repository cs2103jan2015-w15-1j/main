package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.resources.view.RootLayoutController;

//@author A0122081X
public class MainApp extends Application {

    private static final String WINDOW_TITLE = "Veto";
    private static final String IMAGE_ICON = "/images/icon.png";
    // ================================================================
	// Fields
	// ================================================================
	private Stage primaryStage;
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
		initController();
		initRootLayout();
		initPrimaryStage(primaryStage);

		// Provide a stage handle in controller so that controller can close it when exiting
		controller.setStage(this.primaryStage);
	}

    private void initController() {
        controller = Controller.getInstance();
    }

    private void initPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image(IMAGE_ICON)); 
		this.primaryStage.setTitle(WINDOW_TITLE);
		this.primaryStage.setMinWidth(STAGE_MINIMUM_WIDTH);
		this.primaryStage.setMinHeight(STAGE_MINIMUM_HEIGHT);
		assert rootLayoutController != null;
		this.primaryStage.setScene(new Scene(rootLayoutController));
        this.primaryStage.show();
    }

	public void initRootLayout() {
		rootLayoutController = new RootLayoutController(controller);
	}
}
