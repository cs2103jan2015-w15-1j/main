package tests;

import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import main.java.Controller;
import main.java.Task;

import main.resources.view.DisplayController;
import org.junit.Test;

import java.util.ArrayList;

//@author A0122081X
public class ControllerTest extends TestCase {
    // ================================================================
    // Tests for non-recurring tasks
    // ================================================================
    @Test
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
        String input = "add this today";
        controller.executeCommand(input);

        assertEquals("this", controller.getIncompleteTasksPublic());

    }

    @Test
    public void testAddDeadline() {
        Controller controller = Controller.getInstance();
        String input = "add this today";
        controller.executeCommand(input);
    }

    @Test
    public void testAddTimed() {
        Controller controller = Controller.getInstance();
        String input = "add this today";
        controller.executeCommand(input);
    }

    @Test
    public void testDelete() {

    }

    @Test
    public void testEdit() {

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
