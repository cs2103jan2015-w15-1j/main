package main.resources.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

//@author A0121813U
public class TitleBox extends HBox {
	
	private static final String LOCATION_TITLE_BOX_FXML = "/view/TitleBox.fxml";
	
	 @FXML
	 private Label title;
	
	public TitleBox(String desc) {
		
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TITLE_BOX_FXML));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 this.title.setText(desc);
	}

}
