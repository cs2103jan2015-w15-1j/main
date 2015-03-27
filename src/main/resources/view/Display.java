package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import main.java.Command;
import main.java.DateParser;
import main.java.Storage;
import main.java.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Display extends AnchorPane {
    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private ListView<HBox> listView;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private String saveFileName;
    private Storage storage;
    private boolean timeToExit;

    private ArrayList<Task> incompleteTasks;
    private ArrayList<Task> completedTasks;

    private Stack<ArrayList<Task>> previousStates;

    private ObservableList<Task> displayedTasks = FXCollections.observableArrayList();
    private ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
    private DateParser parser;

    // ================================================================
    // Constants
    // ================================================================
    private final static String TASK_OVERVIEW_LOCATION = "/view/TaskOverview.fxml";
    private static final String MESSAGE_EMPTY = "There is currently no task.\n";


    // ================================================================
    // Constructor
    // ================================================================
    /**
     * The constructor is called before the initialize() method.
     */
    public Display() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(TASK_OVERVIEW_LOCATION));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================================
    // Public methods
    // ================================================================

    // ================================================================
    // Logic methods
    // ================================================================

    private ArrayList<Task> sortToDisplay(ArrayList<Task> list) {
    	ArrayList<Task> overdueTasks = new ArrayList<Task>();
    	ArrayList<Task> floatingTasks = new ArrayList<Task>();
    	ArrayList<Task> notOverdueTasks = new ArrayList<Task>();
    	ArrayList<Task> finalList = new ArrayList<Task>();

    	// Separate the floating, overdue and pending
    	for (Task task: list) {
    		if (task.getType() == Task.Type.FLOATING) {
    			floatingTasks.add(task);
    		} else if (task.isOverdue()) {
    			overdueTasks.add(task);
    		} else {
    			notOverdueTasks.add(task);
    		}
    	}

    	// Sort according to what we discussed
    	overdueTasks = sortByDateAndType(overdueTasks);
    	notOverdueTasks = sortByDateAndType(notOverdueTasks);

    	finalList.addAll(floatingTasks);
    	finalList.addAll(overdueTasks);
    	finalList.addAll(notOverdueTasks);

        return finalList;
    }

    private ArrayList<Task> sortByDateAndType(ArrayList<Task> list) {
    	ArrayList<Task> output = new ArrayList<Task>();

    	for (Task task: list) {
    		if (output.size() == 0) {
    			output.add(task);
    		} else {
    			for (Task something: output) {
    				if (task.getDate().isBefore(something.getDate())) {
    					output.add(output.indexOf(something), task);
    					break;
    				} else if (task.getDate().isEqual(something.getDate())) {
    					if (something.getType() == Task.Type.TIMED && task.getType() == Task.Type.DEADLINE) {
    						output.add(output.indexOf(something), task);
    						break;
    					}
    				}
    			}
    			output.add(task);
    		}
    	}
    	return output;
    }

    // ================================================================
    // Utility methods
    // ================================================================

    public void updateDisplay(ObservableList<Task> tasks) {
        // TODO THIS METHOD NEEDS REFACTORING
        // TODO THIS METHOD NEEDS REFACTORING
        // TODO THIS METHOD NEEDS REFACTORING
        ArrayList<Task> listOfTasks = sortToDisplay(new ArrayList<Task>(tasks));

        // re-initialise displayBoxes
        displayBoxes = FXCollections.observableArrayList();
        LocalDate now = LocalDate.now();
        int i = 1;

        // add first category
        DayBox floating = new DayBox("Floating", "");
        displayBoxes.add(floating);
        boolean hasFloating = false;
        for (Task t : listOfTasks) {
            if (t.getType() == Task.Type.FLOATING) {
                hasFloating = true;
                displayBoxes.add(new TaskBox(i, t.getDescription()));
                i++;
            }
        }
        if (!hasFloating) {
            floating.dim();
        }

        // add second category
        DayBox overdue = new DayBox("Overdue", "");
        displayBoxes.add(overdue);
        boolean hasOverdue = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");
        for (Task t : listOfTasks) {
            if (t.isOverdue()) {
                hasOverdue = true;
                displayBoxes.add(new TaskBox(i, t.getDescription() + " on " +
                                                t.getDate().format(formatter)));
                i++;
            }
        }
        if (!hasOverdue) {
            overdue.dim();
        }

        // generate the dates of the 7 days from today
        ArrayList<LocalDate> days = new ArrayList<LocalDate>();
        days.add(now);
        for (int j = 1; j < 7; j++) {
            days.add(now.plusDays(j));
        }

        // formats the date for the day label, eg. Monday, Tuesday, etc
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");

        // formats the date for the date label, eg. 1 April
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");


        for (LocalDate day : days) {
            DayBox label;
            if (day.equals(now)) {
                // special cases to show "Today" and "Tomorrow" instead
                label = new DayBox("Today", day.format(dateFormatter));
            } else if (day.equals(now.plusDays(1))) {
                label = new DayBox("Tomorrow", day.format(dateFormatter));
            } else {
                label = new DayBox(day.format(dayFormatter),
                                            day.format(dateFormatter));
            }
            displayBoxes.add(label);
            boolean hasTaskOnThisDay = false;
            for (Task t : listOfTasks) {
                if (t.getDate() != null && t.getDate().isEqual(day)) {
                    hasTaskOnThisDay = true;
                    displayBoxes.add(new TaskBox(i, t.getDescription()));
                    i++;
                }
            }
            if (!hasTaskOnThisDay) {
                label.dim();
            }
        }
        listView.setItems(displayBoxes);
    }

    public void loadDisplayedTasks(ArrayList<Task> input) {
        displayedTasks.setAll(input);
    }

    private ArrayList<Task> concatenateTasks(ArrayList<Task> first, ArrayList<Task> second) {
        ArrayList<Task> output = new ArrayList<Task>();
        output.addAll(first);
        output.addAll(second);
        return output;
    }
}
