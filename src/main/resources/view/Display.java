package main.resources.view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.java.Task;

//@author A0121520A
public class Display extends VBox {

    // ================================================================
    // FXML Fields
    // ================================================================
    @FXML
    private ListView<HBox> listView;

    @FXML
    private Label feedbackLabel;

    @FXML
    private VBox noTaskOverlay;

    @FXML
    private Label noTaskOverlayIcon;

    @FXML
    private Label noTaskOverlayGreeting;

    @FXML
    private Label noTaskOverlayMessage;

    @FXML
    private VBox helpOverlay;

    @FXML
    private Label helpOverlayIcon;

    @FXML
    private Label helpOverlayTitle;

    @FXML
    private ListView<HelpBox> helpOverlayContents;


    // ================================================================
    // Non-FXML Fields
    // ================================================================
    private static Logger logger;
    private Timeline feedbackTimeline;
    private Timeline overlayTimeline;
    private ArrayList<String> allExampleCommands;
    private ObservableList<HelpBox> helpList;

    private static Display display;
    private int currentScrollIndex;
    private int numExcessTasks;
    private boolean isCurrentDisplayOverview;

    // ================================================================
    // Constants
    // ================================================================
    private final static String LOCATION_TASK_OVERVIEW_FXML = "/view/TaskOverview.fxml";
    private final static String EMPTY_STRING = "";

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
    private static final String LABEL_EXCESS_TASKS = "... and %d more tasks";

    private static final String HELP_OVERLAY_TITLE = "Need help?";
    private static final String HELP_OVERLAY_ICON = "\uf05a";
    private static final String HELP_ADD_DESC = "Add a task";
    private static final String HELP_ADD_COMMAND = "add <description> <time> <day>";
    private static final String HELP_EDIT_DESC = "Edit a task";
    private static final String HELP_EDIT_COMMAND = "edit <index> <description> <time> <day>";
    private static final String HELP_DELETE_DESC = "Delete a task";
    private static final String HELP_DELETE_COMMAND = "delete <index>";
    private static final String HELP_COMPLETE_DESC = "Mark a task as completed";
    private static final String HELP_COMPLETE_COMMAND = "complete <index>";
    private static final String HELP_INCOMPLETE_DESC = "Mark a task as incomplete";
    private static final String HELP_INCOMPLETE_COMMAND = "incomplete <index>";
    private static final String HELP_UNDO_DESC = "Undo previous action";
    private static final String HELP_UNDO_COMMAND = "undo";
    private static final String HELP_SET_SAVE_LOCATION_DESC = "Set a file as save file";
    private static final String HELP_SET_SAVE_LOCATION_COMMAND = "set <directory>";
    private static final String HELP_MOVE_SAVE_LOCATION_DESC = "Change save directory";
    private static final String HELP_MOVE_SAVE_LOCATION_COMMAND = "move <directory>";
    private static final String HELP_SEARCH_DESC = "Search for a task";
    private static final String HELP_SEARCH_COMMAND = "search <keyword/day>";
    private static final String HELP_DISPLAY_INCOMPLETE_DESC = "Display overview";
    private static final String HELP_DISPLAY_INCOMPLETE_COMMAND = "display";
    private static final String HELP_DISPLAY_COMPLETE_DESC = "Display completed tasks";
    private static final String HELP_DISPLAY_COMPLETE_COMMAND = "display completed";
    private static final String HELP_EXIT_DESC = "Exit Veto";
    private static final String HELP_EXIT_COMMAND = "exit";
    
    private static final int OVERLAY_FADE_IN_MILLISECONDS = 200;
    
    private static final String NO_TASK_OVERLAY_GREETING = "Hello!";
    private static final String NO_TASK_OVERLAY_ICON = "\uf14a";
    private static final String NO_TASK_OVERLAY_MESSAGE = "Looks like you've got no pending tasks. Type \"help\" for a list of commands or try entering the following:\n\n";
    private static final int NUM_EXAMPLE_COMMANDS = 3;
    
    private static final int FEEDBACK_FADE_IN_MILLISECONDS = 500;
    private static final int FEEDBACK_FADE_OUT_MILLISECONDS = 1000;
    private static final int FEEDBACK_DISPLAY_SECONDS = 8;

    private static final int DISPLAY_MAX_SIZE = 14;
    private static final int SCROLL_INCREMENT = 5;
    private static final int MAX_NUM_OF_TASKS = 100;

    //@author A0122081X
    // ================================================================
    // Constructor
    // ================================================================
    private Display() {
        logger = Logger.getLogger("Display");
        logger.setLevel(Level.OFF);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOCATION_TASK_OVERVIEW_FXML));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initTimelines();
        initExampleCommands();
        initHelpList();
    }
    
    //@author A0121520A 
    public static Display getInstance() {
        if (display == null) {
            display = new Display();
        }
        return display;
    }


    // ================================================================
    // Public methods
    // ================================================================
    public void hideOverlays() {
        noTaskOverlay.toBack();
        helpOverlay.toBack();
        noTaskOverlay.setOpacity(0);
        helpOverlay.setOpacity(0);
    }
    
    public void updateOverviewDisplay(ObservableList<Task> tasks) {
        hideOverlays();

        if (tasks.isEmpty()) {
            showNoTaskOverlay();
        }
        
        ArrayList<Task> listOfTasks = new ArrayList<Task>(tasks);
        listOfTasks = trimListOfTasks(listOfTasks);
        logger.log(Level.INFO, "List of tasks: " + listOfTasks.toString());
        
        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();
        LocalDate now = LocalDate.now();
        int index = 1;

        index = addOverdueTasks(displayBoxes, listOfTasks, index);
        index = addFloatingTasks(displayBoxes, listOfTasks, index);
        index = addThisWeeksTasks(displayBoxes, listOfTasks, now, index);
        index = addAllOtherTasks(displayBoxes, listOfTasks, now, index);

        addNumExcessTasksLabel(displayBoxes);
        if (isCurrentDisplayOverview) {
            highlightChanges(displayBoxes);
        }
        listView.setItems(displayBoxes);
        isCurrentDisplayOverview = true;
    }

    public void updateSearchDisplay(ObservableList<Task> searchResults,
                                    String searchQuery) {
        
        hideOverlays();
        ArrayList<Task> listOfResults = new ArrayList<Task>(searchResults);
        listOfResults = trimListOfTasks(listOfResults);
        logger.log(Level.INFO, "List of results: " + listOfResults.toString());

        ObservableList<HBox> displayBoxes = FXCollections.observableArrayList();

        addSearchLabel(displayBoxes, searchResults, searchQuery);

        int index = 1;

        index = addIncompleteTasks(displayBoxes, listOfResults, index);
        index = addCompletedTasks(displayBoxes, listOfResults, index);

        addNumExcessTasksLabel(displayBoxes);
        listView.setItems(displayBoxes);
        isCurrentDisplayOverview = false;
    }

    public void setFeedback(String feedback) {
        FadeTransition fadeIn = initFadeIn(feedbackLabel,
                                           FEEDBACK_FADE_IN_MILLISECONDS);
        FadeTransition fadeOut = initFadeOut(feedbackLabel,
                                             FEEDBACK_FADE_OUT_MILLISECONDS);

        feedbackTimeline.stop();
        feedbackTimeline = generateFeedbackTimeline(feedback, fadeIn, fadeOut);
        feedbackTimeline.play();
    }

    //@author A0121813U
    public void showHelpDisplay() {
        hideOverlays();
        FadeTransition fadeIn = initFadeIn(helpOverlay,
                                           OVERLAY_FADE_IN_MILLISECONDS);

        overlayTimeline = generateHelpOverlayTimeline(fadeIn);
        overlayTimeline.play();
    }
    
    //@author A0122081X
    public void resetScrollIndex() {
        currentScrollIndex = 0;
    }

    public void scrollDown() {
//        System.out.println(currentScrollIndex);
        if (currentScrollIndex == 0 && listView.getItems().size() < DISPLAY_MAX_SIZE) {
            currentScrollIndex = 0;
        } else if (currentScrollIndex < listView.getItems().size() - DISPLAY_MAX_SIZE) {
            currentScrollIndex += SCROLL_INCREMENT;
            listView.scrollTo(currentScrollIndex);
        }
    }

    public void scrollUp() {
//        System.out.println(currentScrollIndex);
        if (currentScrollIndex > 0) {
            currentScrollIndex -= SCROLL_INCREMENT;
            listView.scrollTo(currentScrollIndex);
        } else if (currentScrollIndex < 0 ) {
            currentScrollIndex = 0;
        }
    }

    //@author A0121520A
    // ================================================================
    // Private overlay method
    // ================================================================
    private void showNoTaskOverlay() {
        setFeedback(EMPTY_STRING);
        Collections.shuffle(allExampleCommands);
        String exampleCommands = generateParagraph(allExampleCommands,
                                                   NUM_EXAMPLE_COMMANDS);

        FadeTransition fadeIn = initFadeIn(noTaskOverlay,
                                           OVERLAY_FADE_IN_MILLISECONDS);

        overlayTimeline = generateNoTaskOverlayTimeline(exampleCommands, fadeIn);
        overlayTimeline.play();
    }

    //@author A0121520A
    // ================================================================
    // Initialization methods
    // ================================================================
    private void initTimelines() {
        feedbackTimeline = new Timeline();
        overlayTimeline = new Timeline();
    }

    //@author A0121813U 
    private void initExampleCommands() {
        allExampleCommands = new ArrayList<String>();
        allExampleCommands.add("add meet Isabel from 5pm to 6pm today");
        allExampleCommands.add("add do tutorial 10 tomorrow");
        allExampleCommands.add("add finish assignment by 2359 tomorrow");
        allExampleCommands.add("add find easter eggs by 10 apr");
        allExampleCommands.add("add complete proposal by friday");
        allExampleCommands.add("add exercise every tuesday");
        allExampleCommands.add("add lunch with boss tomorrow");
        allExampleCommands.add("add remember to buy milk");
        allExampleCommands.add("add watch movie with friends today");
        allExampleCommands.add("add remember wedding anniversary on 12 October");
        allExampleCommands.add("add buy the latest Harry Potter book");
        allExampleCommands.add("add sneak into Apple WWDC");
        allExampleCommands.add("add remember to complete SOC project");
        allExampleCommands.add("add find partner for Orbital");
        allExampleCommands.add("add make funny YouTube video next week");
        allExampleCommands.add("add study for final exams");
        allExampleCommands.add("add plan for overseas trip next week");
        allExampleCommands.add("add meeting with boss 23 July");
        allExampleCommands.add("add return money owed to John");
        allExampleCommands.add("add run for presidential campaign");
        allExampleCommands.add("add do some community work next week");
    }
    
    private void initHelpList() {
        helpList = FXCollections.observableArrayList();
        helpList.add(new HelpBox(HELP_ADD_DESC, HELP_ADD_COMMAND));
        helpList.add(new HelpBox(HELP_EDIT_DESC, HELP_EDIT_COMMAND));
        helpList.add(new HelpBox(HELP_DELETE_DESC, HELP_DELETE_COMMAND));
        helpList.add(new HelpBox(HELP_COMPLETE_DESC, HELP_COMPLETE_COMMAND));
        helpList.add(new HelpBox(HELP_INCOMPLETE_DESC, HELP_INCOMPLETE_COMMAND));
        helpList.add(new HelpBox(HELP_UNDO_DESC, HELP_UNDO_COMMAND));
        helpList.add(new HelpBox(HELP_SET_SAVE_LOCATION_DESC,
                                 HELP_SET_SAVE_LOCATION_COMMAND));
        helpList.add(new HelpBox(HELP_MOVE_SAVE_LOCATION_DESC,
                                 HELP_MOVE_SAVE_LOCATION_COMMAND));
        helpList.add(new HelpBox(HELP_SEARCH_DESC, HELP_SEARCH_COMMAND));
        helpList.add(new HelpBox(HELP_DISPLAY_INCOMPLETE_DESC, HELP_DISPLAY_INCOMPLETE_COMMAND));
        helpList.add(new HelpBox(HELP_DISPLAY_COMPLETE_DESC, HELP_DISPLAY_COMPLETE_COMMAND));
        helpList.add(new HelpBox(HELP_EXIT_DESC, HELP_EXIT_COMMAND));
    }
    
    //@author A0121520A
    private void initNoTaskOverlay(String exampleCommands) {
        noTaskOverlay.setOpacity(0);
        noTaskOverlay.toFront();
        noTaskOverlayIcon.setText(NO_TASK_OVERLAY_ICON);
        noTaskOverlayGreeting.setText(NO_TASK_OVERLAY_GREETING);
        noTaskOverlayMessage.setText(NO_TASK_OVERLAY_MESSAGE + exampleCommands);
    }

    private void initFeedbackLabel(String feedback) {
        feedbackLabel.setOpacity(0);
        feedbackLabel.setText(feedback);
    }

    private void initHelpOverlay() {
        helpOverlay.toFront();
        helpOverlayIcon.setText(HELP_OVERLAY_ICON);
        helpOverlayTitle.setText(HELP_OVERLAY_TITLE);
        helpOverlayContents.setItems(helpList);
    }

    private FadeTransition initFadeIn(Node node, int duration) {
        FadeTransition fadeIn = new FadeTransition(new Duration(duration));
        fadeIn.setNode(node);
        fadeIn.setToValue(1);
        return fadeIn;
    }

    private FadeTransition initFadeOut(Node node, int duration) {
        FadeTransition fadeOut = new FadeTransition(new Duration(duration));
        fadeOut.setNode(node);
        fadeOut.setToValue(0);
        return fadeOut;
    }

    // ================================================================
    // Timeline generators
    // ================================================================
    private Timeline generateFeedbackTimeline(String feedback,
                                              FadeTransition fadeIn,
                                              FadeTransition fadeOut) {
        return new Timeline(new KeyFrame(Duration.seconds(0),
                                         new EventHandler<ActionEvent>() {
                                             @Override
                                             public void handle(ActionEvent event) {
                                                 initFeedbackLabel(feedback);
                                                 fadeIn.play();
                                             }
                                         }),
                            new KeyFrame(Duration.seconds(FEEDBACK_DISPLAY_SECONDS),
                                         new EventHandler<ActionEvent>() {
                                             @Override
                                             public void handle(ActionEvent event) {
                                                 fadeOut.play();
                                             }
                                         }));
    }

    private Timeline generateNoTaskOverlayTimeline(String exampleCommands,
                                                   FadeTransition fadeIn) {
        return new Timeline(new KeyFrame(new Duration(1),
                                         new EventHandler<ActionEvent>() {
                                             @Override
                                             public void handle(ActionEvent event) {
                                                 initNoTaskOverlay(exampleCommands);
                                                 fadeIn.play();
                                             }
                                         }));
    }

    private Timeline generateHelpOverlayTimeline(FadeTransition fadeIn) {
        return new Timeline(new KeyFrame(new Duration(1),
                                         new EventHandler<ActionEvent>() {
                                             @Override
                                             public void handle(ActionEvent event) {
                                                 initHelpOverlay();
                                                 fadeIn.play();
                                             }
                                         }));
    }


    // ================================================================
    // Logic methods for updateOverviewDisplay
    // ================================================================
    private int addFloatingTasks(ObservableList<HBox> displayBoxes,
                                 ArrayList<Task> listOfTasks,
                                 int index) {
        CategoryBox floating = new CategoryBox(LABEL_FLOATING);
        displayBoxes.add(floating);

        boolean hasFloating = false;

        for (Task task : listOfTasks) {
            if (task.getType() == Task.Type.FLOATING) {
                hasFloating = true;
                addTask(displayBoxes, index, task, true);
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
        CategoryBox overdue = new CategoryBox(LABEL_OVERDUE);
        displayBoxes.add(overdue);

        boolean hasOverdue = false;

        for (Task task : listOfTasks) {
            if (task.isOverdue()) {
                hasOverdue = true;
                addTask(displayBoxes, index, task, true);
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

        for (LocalDate day : days) {
            CategoryBox label = generateDayLabel(now, day);
            displayBoxes.add(label);

            boolean hasTaskOnThisDay = false;

            for (Task task : listOfTasks) {
                if (day.equals(task.getDate())) {
                    hasTaskOnThisDay = true;
                    addTask(displayBoxes, index, task, false);
                    index++;
                }
            }

            if (!hasTaskOnThisDay) {
                label.dim();
            }
        }
        return index;
    }

    private int addAllOtherTasks(ObservableList<HBox> displayBoxes,
                                 ArrayList<Task> listOfTasks,
                                 LocalDate now,
                                 int index) {
        CategoryBox otherTasks = new CategoryBox(LABEL_OTHERS);
        displayBoxes.add(otherTasks);

        boolean hasOtherTasks = false;
        LocalDate dayOneWeekFromNow = now.plusWeeks(1);

        for (Task task : listOfTasks) {
            if (task.getDate() != null &&
                (dayOneWeekFromNow.equals(task.getDate()) || dayOneWeekFromNow.isBefore(task.getDate()))) {
                hasOtherTasks = true;
                addTask(displayBoxes, index, task, true);
                index++;
            }
        }

        if (!hasOtherTasks) {
            otherTasks.dim();
        }

        return index;
    }

    private CategoryBox generateDayLabel(LocalDate now, LocalDate day) {

        // formats the date for the day label, eg. Monday, Tuesday, etc
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");

        // formats the date for the date label, eg. 1 April
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM");

        // formats the date for the date label of special cases, eg. Wednesday, 1 April
        DateTimeFormatter dateFormatterForSpecialCase = DateTimeFormatter.ofPattern("EEEE, d MMMM");

        CategoryBox label;

        // special cases to show "Today" and "Tomorrow" instead of day
        if (day.equals(now)) {
            label = new CategoryBox(LABEL_TODAY,
                                    day.format(dateFormatterForSpecialCase));
        } else if (day.equals(now.plusDays(1))) {
            label = new CategoryBox(LABEL_TOMORROW,
                                    day.format(dateFormatterForSpecialCase));
        } else {
            label = new CategoryBox(day.format(dayFormatter),
                                    day.format(dateFormatter));
        }
        return label;
    }

    private ArrayList<LocalDate> generateDaysOfWeek(LocalDate now) {
        ArrayList<LocalDate> days = new ArrayList<LocalDate>();
        for (int i = 0; i < 7; i++) {
            days.add(now.plusDays(i));
        }
        return days;
    }

    private void highlightChanges(ObservableList<HBox> displayBoxes) {
        ObservableList<HBox> oldDisplayBoxes = listView.getItems();
        if (oldDisplayBoxes.isEmpty()) {
            return;
        }
        for (HBox newBox : displayBoxes) {
            if (newBox instanceof TaskBox) {
                String description = ((TaskBox) newBox).getDescription();
                if (!hasMatchingBox(oldDisplayBoxes, description)) {
                    ((TaskBox) newBox).highlight();
                }
            }
        }
    }

    private boolean hasMatchingBox(ObservableList<HBox> oldDisplayBoxes,
                                   String description) {
        for (HBox oldBox : oldDisplayBoxes) {
            if (oldBox instanceof TaskBox) {
                TaskBox tBox = (TaskBox) oldBox;
                if (description.equals(tBox.getDescription())) {
                    oldDisplayBoxes.remove(oldBox);
                    return true;
                }
            }
        }
        return false;
    }
    

    // ================================================================
    // Logic methods for updateSearchDisplay
    // ================================================================
    private int addIncompleteTasks(ObservableList<HBox> displayBoxes,
                                   ArrayList<Task> listOfResults,
                                   int index) {
        CategoryBox incompleteLabel = new CategoryBox(LABEL_INCOMPLETE);
        displayBoxes.add(incompleteLabel);

        boolean hasIncompleteTask = false;

        for (Task task : listOfResults) {
            if (!task.isCompleted()) {
                hasIncompleteTask = true;
                addTask(displayBoxes, index, task, true);
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
        CategoryBox completedLabel = new CategoryBox(LABEL_COMPLETED);
        displayBoxes.add(completedLabel);

        boolean hasCompletedTask = false;

        for (Task task : listOfResults) {
            if (task.isCompleted()) {
                hasCompletedTask = true;
                addTask(displayBoxes, index, task, true);
                index++;
            }
        }

        if (!hasCompletedTask) {
            completedLabel.dim();
        }

        return index;
    }

    private void addSearchLabel(ObservableList<HBox> displayBoxes,
                                ObservableList<Task> searchResults,
                                String searchQuery) {
        CategoryBox searchLabel = generateSearchLabel(searchResults,
                                                      searchQuery);
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
                                                        searchQuery));
        } else {
            searchLabel = new CategoryBox(String.format(LABEL_SUCCESSFUL_SEARCH,
                                                        searchQuery));
        }

        return searchLabel;
    }

    // ================================================================
    // Utility methods
    // ================================================================
    private void addTask(ObservableList<HBox> displayBoxes,
                         int index,
                         Task task,
                         boolean includeDate) {
        if (task.isCompleted()) {
            displayBoxes.add(new TaskBox(index,
                                         task.getDescription(),
                                         task.getFormattedTimeAndDate(includeDate),
                                         task.isRecurring(),
                                         true));
        } else {
            displayBoxes.add(new TaskBox(index,
                                         task.getDescription(),
                                         task.getFormattedTimeAndDate(includeDate),
                                         task.isRecurring()));
        }
    }
    
    private void addNumExcessTasksLabel(ObservableList<HBox> displayBoxes) {
        if (numExcessTasks > 0) {
            displayBoxes.add(new CategoryBox(String.format(LABEL_EXCESS_TASKS, numExcessTasks)));
        }
    }

    private ArrayList<Task> trimListOfTasks(ArrayList<Task> listOfTasks) {
        numExcessTasks = listOfTasks.size() - MAX_NUM_OF_TASKS;
        if (numExcessTasks > 0) {
            return new ArrayList<Task> (listOfTasks.subList(0, MAX_NUM_OF_TASKS));
        }
        return listOfTasks;
    }
    
    //@author A0121813U
    private String generateParagraph(ArrayList<String> list, int size) {
        return StringUtils.join(list.toArray(), "\n", 0, size);
    }
}
