package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CategoryBox extends HBox {

    private static final String LOCATION_CATEGORY_BOX_FXML = "/view/CategoryBox.fxml";
    private static final String FORMAT_DATE = "(%s)";
    private static final String NO_DATE = "";

    @FXML
    private Label category;

    @FXML
    private Label date;

    private CategoryBox() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_CATEGORY_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public CategoryBox(String category, String date) {
        this();
        this.category.setText(category);
        if (!date.isEmpty()) {
            this.date.setText(String.format(FORMAT_DATE, date));
        } else {
            this.date.setText(NO_DATE);
        }
    }
    
    public CategoryBox(String category) {
        this();
        this.category.setText(category);
        this.date.setText(NO_DATE);
    }
    
    public void dim() {
        category.setStyle("-fx-text-fill: #E8E8E8;");
        date.setStyle("-fx-text-fill: #E8E8E8;");
    }
}
