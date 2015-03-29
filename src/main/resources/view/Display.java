package main.resources.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import main.java.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Display extends AnchorPane {

    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private ListView<HBox> listView;

    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private static Logger logger;

    // ================================================================
    // Constants
    // ================================================================

    private final static String LOCATION_TASK_OVERVIEW = "/view/TaskOverview.fxml";

    private static final String LABEL_FLOATING = "Floating";
    private static final String LABEL_OVERDUE = "Overdue";
    private static final String LABEL_TODAY = "Today";
    private static final String LABEL_TOMORROW = "Tomorrow";
    private static final String LABEL_OTHERS = "Everything else";
    private static final String LABEL_SUCCESSFUL_SEARCH = "Results for \"%s\"";
    private static final String LABEL_UNSUCCESSFUL_SEARCH = "No results for \"%s\"";
    private static final String LABEL_DEFAULT_SEARCH_QUERY = "all tasks";
    private static final String LABEL_INCOMPLETE = "Incomplete";
    private static final String LABEL_COMPLETED = "Completed";


    // ================================================================
    // Constructor
    // ================================================================
    /**
     * The constructor is called before the initialize() method.
     */
    public Display() {
        logger = Logger.getLogger("Display");
        logger.setLevel(Level.INFO);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_OVERVIEW));
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

    public void updateOverviewDisplay(ObservableList<Task> tasks) {
        ArrayList<Task> listOfTasks = new ArrayList<Task>(tasks);
        logger.log(Level.INFO, "List of tasks: " + listOfTasks.toString());

        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
        LocalDate now = LocalDate.now();
        int index = 1;

        index = addOverdueTasks(displayBoxes, listOfTasks, index);
        index = addFloatingTasks(displayBoxes, listOfTasks, index);
        index = addThisWeeksTasks(displayBoxes, listOfTasks, now, index);
        index = addAllOtherTasks(displayBoxes, listOfTasks, now, index);

        listView.setItems(displayBoxes);
    }

    public void updateSearchDisplay(ObservableList<Task> searchResults,
                                    String searchQuery) {
        ArrayList<Task> listOfResults = new ArrayList<Task>(searchResults);
        logger.log(Level.INFO, "List of results: " + listOfResults.toString());

        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

        addSearchLabel(displayBoxes, searchResults, searchQuery);

        int index = 1;

        index = addIncompleteTasks(displayBoxes, listOfResults, index);
        index = addCompletedTasks(displayBoxes, listOfResults, index);

        listView.setItems(displayBoxes);
    }


    // ================================================================
    // Logic methods for updateOverviewDisplay
    // ================================================================

    private int addFloatingTasks(ObservableList<HBox> displayBoxes,
                                 ArrayList<Task> listOfTasks,
                                 int index) {
        CategoryBox floating = new CategoryBox(LABEL_FLOATING, "");
        displayBoxes.add(floating);

        boolean hasFloating = false;

        for (Task task : listOfTasks) {
            if (task.getType() == Task.Type.FLOATING) {
                hasFloating = true;
                displayBoxes.add(new TaskBox(index, task.getDescription()));
                index++;
            }
        }

        if (!hasFloating) {
            floating.dim();
        }

        return index;
    }

    private int addOverdueTasks(ObservableList<HBox> displayBoxes,
                                ArrayList<Task> listOfTasks,
                                int index) {
        // add second category
        CategoryBox overdue = new CategoryBox(LABEL_OVERDUE, "");
        displayBoxes.add(overdue);

        boolean hasOverdue = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");

        for (Task task : listOfTasks) {
            if (task.isOverdue()) {
                hasOverdue = true;
                displayBoxes.add(new TaskBox(index, task.getDescription() +
                                                    " on " +
                                                    task.getDate()
                                                        .format(formatter)));
                index++;
            }
        }

        if (!hasOverdue) {
            overdue.dim();
        }

        return index;
    }

    private int addThisWeeksTasks(ObservableList<HBox> displayBoxes,
                                  ArrayList<Task> listOfTasks,
                                  LocalDate now,
                                  int index) {
        // generate the dates of the 7 days from today
        ArrayList<LocalDate> days = generateDaysOfWeek(now);

        // formats the date for the day label, eg. Monday, Tuesday, etc
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");

        // formats the date for the date label, eg. 1 April
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");

        // formats the time for the time label, eg 2:00PM to 4:00PM
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");

        for (LocalDate day : days) {
            CategoryBox label = generateDayLabel(now, dayFormatter, dateFormatter,
                                            day);
            displayBoxes.add(label);

            boolean hasTaskOnThisDay = false;

            for (Task task : listOfTasks) {
                if (day.equals(task.getDate())) {
                    hasTaskOnThisDay = true;
                    if (task.getType() == Task.Type.TIMED) {
                        displayBoxes.add(new TaskBox(index,
                                                     task.getDescription() +
                                                             ", " +
                                                             task.getStartTime()
                                                                 .format(timeFormatter) +
                                                             " to " +
                                                             task.getEndTime()
                                                                 .format(timeFormatter)));
                    } else {
                        displayBoxes.add(new TaskBox(index,
                                                     task.getDescription()));
                    }
                    index++;
                }
            }

            if (!hasTaskOnThisDay) {
                label.dim();
            }
        }
        return index;
    }

    private CategoryBox generateDayLabel(LocalDate now,
                                    DateTimeFormatter dayFormatter,
                                    DateTimeFormatter dateFormatter,
                                    LocalDate day) {
        CategoryBox label;
        // special cases to show "Today" and "Tomorrow" instead of day
        if (day.equals(now)) {
            label = new CategoryBox(LABEL_TODAY, day.format(dateFormatter));
        } else if (day.equals(now.plusDays(1))) {
            label = new CategoryBox(LABEL_TOMORROW, day.format(dateFormatter));
        } else {
            label = new CategoryBox(day.format(dayFormatter),
                               day.format(dateFormatter));
        }
        return label;
    }

    private ArrayList<LocalDate> generateDaysOfWeek(LocalDate now) {
        ArrayList<LocalDate> days = new ArrayList<LocalDate>();
        days.add(now);
        for (int j = 1; j < 7; j++) {
            days.add(now.plusDays(j));
        }
        return days;
    }

    private int addAllOtherTasks(ObservableList<HBox> displayBoxes,
                                 ArrayList<Task> listOfTasks,
                                 LocalDate now,
                                 int i) {
        CategoryBox otherTasks = new CategoryBox(LABEL_OTHERS, "");
        displayBoxes.add(otherTasks);

        boolean hasOtherTasks = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");
        LocalDate dayOneWeekFromNow = now.plusWeeks(1);

        for (Task t : listOfTasks) {
            if (t.getDate() != null && dayOneWeekFromNow.isBefore(t.getDate())) {
                hasOtherTasks = true;
                displayBoxes.add(new TaskBox(i, t.getDescription() + " on " +
                                                t.getDate().format(formatter)));
                i++;
            }
        }

        if (!hasOtherTasks) {
            otherTasks.dim();
        }

        return i;
    }


    // ================================================================
    // Logic methods for updateSearchDisplay
    // ================================================================

    private void addSearchLabel(ObservableList<HBox> displayBoxes,
                                ObservableList<Task> searchResults,
                                String searchQuery) {
        CategoryBox searchLabel = generateSearchLabel(searchResults, searchQuery);
        displayBoxes.add(searchLabel);
    }

    private CategoryBox generateSearchLabel(ObservableList<Task> searchResults,
                                       String searchQuery) {
        CategoryBox searchLabel;
        if (searchQuery.isEmpty()) {
            searchQuery = LABEL_DEFAULT_SEARCH_QUERY;
        }

        if (searchResults.isEmpty()) {
            searchLabel = new CategoryBox(String.format(LABEL_UNSUCCESSFUL_SEARCH,
                                                   searchQuery), "");
        } else {
            searchLabel = new CategoryBox(String.format(LABEL_SUCCESSFUL_SEARCH,
                                                   searchQuery), "");
        }
        return searchLabel;
    }

    private int addIncompleteTasks(ObservableList<HBox> displayBoxes,
                                   ArrayList<Task> listOfResults,
                                   int index) {
        CategoryBox incompleteLabel = new CategoryBox(LABEL_INCOMPLETE, "");
        displayBoxes.add(incompleteLabel);

        boolean hasIncompleteTask = false;

        for (Task task : listOfResults) {
            if (!task.isCompleted()) {
                hasIncompleteTask = true;
                displayBoxes.add(new TaskBox(index, task.toString()));
                index++;
            }
        }

        if (!hasIncompleteTask) {
            incompleteLabel.dim();
        }

        return index;
    }

    private int addCompletedTasks(ObservableList<HBox> displayBoxes,
                                  ArrayList<Task> listOfResults,
                                  int index) {
        CategoryBox completedLabel = new CategoryBox(LABEL_COMPLETED, "");
        displayBoxes.add(completedLabel);

        boolean hasCompletedTask = false;

        for (Task task : listOfResults) {
            if (task.isCompleted()) {
                hasCompletedTask = true;
                displayBoxes.add(new TaskBox(index, task.toString(), true));
                index++;
            }
        }

        if (!hasCompletedTask) {
            completedLabel.dim();
        }

        return index;
    }
}
