package main.java;

import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;


public class DisplayControllerStub extends VBox {
    public static DisplayControllerStub displayControllerStub;

    private DisplayControllerStub() {

    }

    public static DisplayControllerStub getInstance() {
        if (displayControllerStub == null) {
            displayControllerStub = new DisplayControllerStub();
        }
        return displayControllerStub;
    }

    // ================================================================
    // Public methods
    // ================================================================
    public void hideOverlays() {
    }

    public void updateOverviewDisplay(ObservableList<Task> tasks) {

    }

    public void updateSearchDisplay(ObservableList<Task> searchResults,
                                    String searchQuery) {
    }

    public void setFeedback(String feedback) {

    }

    public void showHelpDisplay() {

    }

    public void resetScrollIndex() {

    }

    public void scrollDown() {

    }

    public void scrollUp() {

    }
}
