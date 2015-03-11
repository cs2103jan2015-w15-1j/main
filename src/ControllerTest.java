import junit.framework.TestCase;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class ControllerTest extends TestCase {
    @Test
    public void testUndo() {}

    @Test
    public void testComplete() {
        String[] args = {"holaamigos.txt"};
        Controller controller = new Controller();

        controller.executeCommand("add this by 4 feb");
        assertEquals("hello", getTaskDesc(controller.getIncompleteTasksPublic()));
    }

    private ArrayList<String> getTaskDesc(ArrayList<Task> input) {
        ArrayList<String> output = new ArrayList<String>();
        for (Task task : input) {
            output.add(task.getInfo());
        }

        return output;

    }

}