package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HelpBox extends HBox {

    private static final String LOCATION_HELP_BOX_FXML = "/view/HelpBox.fxml";
    
    @FXML
    private Label description;
    
    @FXML
    private Label command;

    public HelpBox(String description, String command) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_HELP_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.description.setText(description);
        this.command.setText(command);
        
    }

}
