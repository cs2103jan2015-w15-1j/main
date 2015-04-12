package tests;

import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import main.java.Controller;
import main.java.Task;

import org.junit.Test;

import java.util.ArrayList;

//@author A0122081X
public class ControllerTest extends TestCase {
    // ================================================================
    // Tests for non-recurring tasks
    // ================================================================
    @Test
    public void testAdd() {

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
    public void testClear() {

    }

    @Test
    public void testInvalid() {

    }

    @Test
    public void testExit() {

    }

    @Test
    public void testUndo() {
        Controller controller = Controller.getInstance();
        controller.executeCommand("add this by 14 apr");
        controller.executeCommand("add that by 20 mar");

        controller.executeCommand("edit 1 desc foobar");
        System.out.println(controller.getIncompleteTasksPublic());

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
