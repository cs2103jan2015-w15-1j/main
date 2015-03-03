import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Test;

public class StorageTest {

    private BufferedReader reader;

    // Test the getTasksFromFile method
    @Test
    public void testGetTasksFromFile() {
        String testData1[] = { "attend meeting later today",
                "attend meeting later at 1200-1400 on 20 Feb",
                "finish homework by 20 Feb" };
        Boolean testData2[] = { true, true, false };

        ArrayList<Task> tempData = new ArrayList<Task>();
        int i = 0;

        Storage test = new Storage("Test1.txt");
        tempData = test.getTasksFromFile();
        for (Task task : tempData) {
            assertEquals(testData1[i], task.getRawInfo());
            assertEquals(testData2[i], task.getTaskStatus());
            i++;
        }
    }
    
    // Test the writeTasktoFile method
    @Test
    public void testWriteTasktoFile() {
        // parameters
        ArrayList<Task> tempData = new ArrayList<Task>();
        String testData1[] = { "attend meeting later today",
                "finish assignment by 13 Feb", "finish homework by 20 Feb" };
        Boolean testData2[] = { false, true, false };
        String testOutput[] = { "F attend meeting later today",
                "T finish assignment by 13 Feb", "F finish homework by 20 Feb" };

        // setting up ArrayList and writing to file
        Storage test = new Storage("Test2");
        for (int i = 0; i < testData1.length; i++) {
            Task task = new Task(testData1[i]);
            if (testData2[i] == true) {
                task.markAsComplete();
            }
            tempData.add(task);
        }
        test.writeTasksToFile(tempData);

        // checks file data
        String output;
        File fileName = new File ("test2.txt");
        int i=0;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((output = reader.readLine()) != null) {
                assertEquals(output,testOutput[i]);
                i++;
            }
        } catch (Exception e) {
            return;
        }
    }
}
