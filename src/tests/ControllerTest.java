package tests;

import junit.framework.TestCase;
import main.java.Controller;
import main.java.DateParser;
import main.java.Task;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

//@author A0122081X
public class ControllerTest extends TestCase {

    // ================================================================
    // Fields
    // ================================================================
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";

    // ================================================================
    // Utility methods
    // ================================================================
    //@author A0121813U
    // Created for the purpose for this test. Simplify the constructor of Task. NOT A TEST
    public Task createNewTask(String input) {
        DateParser parser = DateParser.getInstance();
        parser.parse(input);
        ArrayList<LocalDateTime> parsedDates = parser.getDates();
        String parsedWords = parser.getParsedWords();
        String nonParsedWords = parser.getNotParsedWords();
        return new Task(input, parsedDates, parsedWords, nonParsedWords);
    }

    //@author A0122081X
    private ArrayList<String> getTaskDesc(ArrayList<Task> input) {
        ArrayList<String> output = new ArrayList<String>();
        for (Task task : input) {
            output.add(task.getDescription());
        }

        return output;
    }

    // ================================================================
    // Tests for non-recurring tasks
    // ================================================================
    @Test
    //@author A0122081X
    public void testClear() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();

        controller.executeCommand("clear");

        assertEquals(testList, controller.getAllTasks());
    }

    @Test
    public void testAddFloat() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("this");
        testList.add(testingTask);

        controller.executeCommand("add this");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testAddDeadline() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("this 1 apr");
        testList.add(testingTask);

        controller.executeCommand("add this 1 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testAddTimed() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("this 1 apr 4pm to 6pm");
        testList.add(testingTask);

        controller.executeCommand("add this 1 apr 4pm to 6pm");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testDelete() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("this");
        testList.add(testingTask);

        controller.executeCommand("add this");

        testList.remove(0);
        controller.executeCommand("delete 1");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testEditDescription() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("that");
        testList.add(testingTask);

        controller.executeCommand("add this");
        controller.executeCommand("edit 1 that");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    public void testEditDeadline() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        Task testingTask = createNewTask("this on 2 apr");
        testList.add(testingTask);

        controller.executeCommand("add this 1 apr");
        controller.executeCommand("edit 1 this 2 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testComplete() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();

        controller.executeCommand("add this");
        controller.executeCommand("complete 1");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testIncomplete() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();

        controller.executeCommand("add this");
        controller.executeCommand("complete 1");
        controller.executeCommand("display completed");
        controller.executeCommand("incomplete 1");

        assertEquals(testList.toString(), controller.getCompleteTasksPublic().toString());
    }

    @Test
    public void testSearch() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();
        testList.add(createNewTask("this on 1 apr"));
        testList.add(createNewTask("this on 2 apr"));

        controller.executeCommand("add this on 1 apr");
        controller.executeCommand("add this on 2 apr");
        controller.executeCommand("add that");
        controller.executeCommand("add foo on 3 apr by 1pm");
        controller.executeCommand("add bar on 3 apr from 2pm to 4pm");

        // Search by description
        controller.executeCommand("search this");
        assertEquals(testList.toString(), controller.getDisplayedTasks().toString());

        // Search by date
        testList.clear();
        testList.add(createNewTask("foo on 3 apr by 1pm"));
        testList.add(createNewTask("bar on 3 apr by 2pm to 4pm"));
        controller.executeCommand("search 3 apr");
        assertEquals(testList.toString(), controller.getDisplayedTasks().toString());
    }

    @Test
    public void testInvalidCommands() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        assertEquals(MESSAGE_INVALID_COMMAND, controller.executeCommand("asjdhaskjdkj"));
        assertEquals(MESSAGE_INVALID_COMMAND, controller.executeCommand("addd"));
        assertEquals(MESSAGE_INVALID_COMMAND, controller.executeCommand("deelete"));
        assertEquals(MESSAGE_INVALID_COMMAND, controller.executeCommand("editt"));
    }

    @Test
    public void testUndo() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<Task>();

        // Undo add
        controller.executeCommand("add this");
        controller.executeCommand("undo");
        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());

        // Undo edit
        controller.executeCommand("add that 1 apr by 4pm");
        controller.executeCommand("edit 1 that 1 apr by 6pm");
        controller.executeCommand("undo");
        Task task = createNewTask("that 1 apr by 4pm");
        testList.add(task);
        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());

        // Undo delete
        controller.executeCommand("delete 1");
        controller.executeCommand("undo");
        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }


    // ================================================================
    // Tests for recurring tasks
    // ================================================================

    // Need to cover
    // add, edit, delete

    @Test
    public void testAddRecurring() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();
        testList.add(createNewTask("this on 1 apr"));
        testList.add(createNewTask("this on 2 apr"));
        testList.add(createNewTask("this on 3 apr"));
        testList.add(createNewTask("this on 4 apr"));
        testList.add(createNewTask("this on 5 apr"));

        controller.executeCommand("add this everyday from 1 apr to 5 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testAddRecurringWithExceptions() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();
        testList.add(createNewTask("this on 1 apr"));
        testList.add(createNewTask("this on 2 apr"));
        testList.add(createNewTask("this on 3 apr"));
        testList.add(createNewTask("this on 5 apr"));

        controller.executeCommand("add this everyday from 1 apr to 5 apr except 4 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
     public void testEditOneRecurring() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();
        testList.add(createNewTask("this on 1 apr"));
        testList.add(createNewTask("this on 2 apr"));
        testList.add(createNewTask("this on 3 apr"));
        testList.add(createNewTask("this on 4 apr"));
        testList.add(createNewTask("that on 6 apr"));

        controller.executeCommand("add this everyday from 1 apr to 5 apr");
        controller.executeCommand("edit 5 that on 6 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testEditAllRecurring() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();
        testList.add(createNewTask("that on 1 apr"));
        testList.add(createNewTask("that on 2 apr"));
        testList.add(createNewTask("that on 3 apr"));
        testList.add(createNewTask("that on 4 apr"));
        testList.add(createNewTask("that on 5 apr"));
        testList.add(createNewTask("that on 6 apr"));
        testList.add(createNewTask("that on 7 apr"));
        testList.add(createNewTask("that on 8 apr"));
        testList.add(createNewTask("that on 9 apr"));
        testList.add(createNewTask("that on 10 apr"));

        controller.executeCommand("add this everyday from 1 apr to 5 apr");
        controller.executeCommand("edit all 5 that everyday from 1 apr to 10 apr");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testDeleteOneRecurring() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();
        testList.add(createNewTask("this on 1 apr"));
        testList.add(createNewTask("this on 2 apr"));
        testList.add(createNewTask("this on 3 apr"));
        testList.add(createNewTask("this on 4 apr"));

        controller.executeCommand("add this everyday from 1 apr to 5 apr");
        controller.executeCommand("delete 5");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }

    @Test
    public void testDeleteAllRecurring() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");

        ArrayList<Task> testList = new ArrayList<>();

        controller.executeCommand("add this everyday from 1 apr to 5 apr");
        controller.executeCommand("delete all 3");

        assertEquals(testList.toString(), controller.getIncompleteTasksPublic().toString());
    }
}
