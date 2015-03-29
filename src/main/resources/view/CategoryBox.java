package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CategoryBox extends HBox {

    private static final String LOCATION_CATEGORY_BOX_FXML = "/view/CategoryBox.fxml";

    @FXML
    private Label category;

    @FXML
    private Label date;

    public CategoryBox(String category, String date) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_CATEGORY_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.category.setText(category);
        if (!date.isEmpty()) {
            this.date.setText("(" + date + ")");
        } else {
            this.date.setText("");
        }
    }
    
    public void dim() {
        category.setStyle("-fx-text-fill: #E8E8E8;");
        date.setStyle("-fx-text-fill: #E8E8E8;");
    }
}
