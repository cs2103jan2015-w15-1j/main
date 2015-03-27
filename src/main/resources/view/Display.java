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
    private ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();


    // ================================================================
    // Constants
    // ================================================================
    private final static String TASK_OVERVIEW_LOCATION = "/view/TaskOverview.fxml";
    private static final String LABEL_FLOATING = "Floating";
    private static final String LABEL_OVERDUE = "Overdue";
    private static final String LABEL_TODAY = "Today";
    private static final String LABEL_TOMORROW = "Tomorrow";
    private static final String LABEL_OTHERS = "Everything else";


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
    public void updateDisplay(ObservableList<Task> tasks) {
        ArrayList<Task> listOfTasks = sortToDisplay(new ArrayList<Task>(tasks));

        System.out.println(listOfTasks.toString());

        // re-initialise displayBoxes
        displayBoxes = FXCollections.observableArrayList();
        LocalDate now = LocalDate.now();
        int i = 1;

        i = addFloatingTasks(listOfTasks, i);
        i = addOverdueTasks(listOfTasks, i);
        i = addThisWeeksTasks(listOfTasks, now, i);
        i = addAllOtherTasks(listOfTasks, now, i);

        listView.setItems(displayBoxes);
    }

    // ================================================================
    // Logic methods
    // ================================================================
    private int addFloatingTasks(ArrayList<Task> listOfTasks, int index) {
        DayBox floating = new DayBox(LABEL_FLOATING, "");
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

    private int addOverdueTasks(ArrayList<Task> listOfTasks, int index) {
        // add second category
        DayBox overdue = new DayBox(LABEL_OVERDUE, "");
        displayBoxes.add(overdue);

        boolean hasOverdue = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");

        for (Task task : listOfTasks) {
            if (task.isOverdue()) {
                hasOverdue = true;
                displayBoxes.add(new TaskBox(index, task.getDescription() + " on " +
                                                task.getDate().format(formatter)));
                index++;
            }
        }

        if (!hasOverdue) {
            overdue.dim();
        }

        return index;
    }

    private int addThisWeeksTasks(ArrayList<Task> listOfTasks,
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
            DayBox label = generateDayLabel(now, dayFormatter, dateFormatter,
                                            day);
            displayBoxes.add(label);

            boolean hasTaskOnThisDay = false;

            for (Task task : listOfTasks) {
                if (day.equals(task.getDate())) {
                    hasTaskOnThisDay = true;
                    if (task.getType() == Task.Type.TIMED) {
                    	displayBoxes.add(new TaskBox(index, task.getDescription() + ", " + 
                    						task.getStartTime().format(timeFormatter) + " to " +
                    						task.getEndTime().format(timeFormatter)));
                    } else {
                    displayBoxes.add(new TaskBox(index, task.getDescription()));
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

    private DayBox generateDayLabel(LocalDate now,
                                    DateTimeFormatter dayFormatter,
                                    DateTimeFormatter dateFormatter,
                                    LocalDate day) {
        DayBox label;
        // special cases to show "Today" and "Tomorrow" instead of day
        if (day.equals(now)) {
            label = new DayBox(LABEL_TODAY, day.format(dateFormatter));
        } else if (day.equals(now.plusDays(1))) {
            label = new DayBox(LABEL_TOMORROW, day.format(dateFormatter));
        } else {
            label = new DayBox(day.format(dayFormatter),
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

    private int addAllOtherTasks(ArrayList<Task> listOfTasks,
                                 LocalDate now,
                                 int i) {
        DayBox otherTasks = new DayBox(LABEL_OTHERS, "");
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

    private ArrayList<Task> sortToDisplay(ArrayList<Task> list) {
        ArrayList<Task> overdueTasks = new ArrayList<Task>();
        ArrayList<Task> floatingTasks = new ArrayList<Task>();
        ArrayList<Task> notOverdueTasks = new ArrayList<Task>();
        ArrayList<Task> finalList = new ArrayList<Task>();

        // Separate the floating, overdue and pending
        for (Task task : list) {
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

        for (Task task : list) {
            if (output.size() == 0) {
                output.add(task);
            } else {
                for (Task something : output) {
                    if (task.getDate().isBefore(something.getDate())) {
                        output.add(output.indexOf(something), task);
                        break;
                    } else if (task.getDate().isEqual(something.getDate())) {
                        if (something.getType() == Task.Type.TIMED &&
                            task.getType() == Task.Type.DEADLINE) {
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
    private ArrayList<Task> concatenateTasks(ArrayList<Task> first,
                                             ArrayList<Task> second) {
        ArrayList<Task> output = new ArrayList<Task>();
        output.addAll(first);
        output.addAll(second);
        return output;
    }
}
