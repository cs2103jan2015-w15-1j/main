package main.resources;

import MainApp;


/**
 * Created by mx on 15/3/15.
 */
public class TaskOverviewController {

    private MainApp mainApp;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}

