package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

//@author A0121520A
public class CategoryBox extends HBox {

    private static final String STYLE_DIM = "-fx-text-fill: #E8E8E8;";
    private static final String LOCATION_CATEGORY_BOX_FXML = "/view/CategoryBox.fxml";
    private static final String FORMAT_DATE = "(%s)";
    private static final String NO_DATE = "";

    @FXML
    private Label category;

    @FXML
    private Label date;

    public CategoryBox(String category, String date) {
        loadFxml();
        this.category.setText(category);
        if (!date.isEmpty()) {
            this.date.setText(String.format(FORMAT_DATE, date));
        } else {
            this.date.setText(NO_DATE);
        }
    }
    
    public CategoryBox(String category) {
        loadFxml();
        this.category.setText(category);
        this.date.setText(NO_DATE);
    }
    
    private void loadFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_CATEGORY_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dim() {
        category.setStyle(STYLE_DIM);
        date.setStyle(STYLE_DIM);
    }
}
