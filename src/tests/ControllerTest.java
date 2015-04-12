package tests;

import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import main.java.Controller;
import main.java.DateParser;
import main.java.Task;

import main.resources.view.DisplayController;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

//@author A0122081X
public class ControllerTest extends TestCase {
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

    // ================================================================
    // Tests for non-recurring tasks
    // ================================================================
    @Test
    //@author A0122081X
    public void testClear() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("clear");
        ArrayList<Task> emptyArrayList = new ArrayList<>();

        assertEquals(controller.getAllTasks(), emptyArrayList);
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

    }

    @Test
    public void testIncomplete() {

    }

    @Test
    public void testSearch() {

    }


    @Test
    public void testInvalid() {

    }

    @Test
    public void testExit() {

    }

    @Test
    public void testUndo() {


    }

    @Test
    public void testCompleteOld() {
        String[] args = {"holaamigos.txt"};
        Controller controller = Controller.getInstance();

        controller.executeCommand("add this by 4 feb");
        //assertEquals("hello", getTaskDesc(controller.getIncompleteTasksPublic()));
    }

    private ArrayList<String> getTaskDesc(ArrayList<Task> input) {
        ArrayList<String> output = new ArrayList<String>();
        for (Task task : input) {
            output.add(task.getDescription());
        }

        return output;

    }

    // ================================================================
    // Tests for recurring tasks
    // ================================================================

    // Need to cover
    // add, edit, delete

}
