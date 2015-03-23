package tests;

import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import main.java.Controller;
import main.java.Task;

import org.junit.Test;

import java.util.ArrayList;

public class ControllerTest extends TestCase {
    @Test
    public void testUndo() {
        Controller controller = new Controller();
        controller.executeCommand("add this by 14 apr");
        controller.executeCommand("add that by 20 mar");

        controller.executeCommand("edit 1 desc foobar");
        System.out.println(controller.getIncompleteTasksPublic());

    }

    @Test
    public void testComplete() {
        String[] args = {"holaamigos.txt"};
        Controller controller = new Controller();

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

}
