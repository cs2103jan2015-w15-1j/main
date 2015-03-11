import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

public class StorageTest {
    String defaultDirectory = "savefile.txt";
    String newDirectory = "C:\\Users\\user\\Documents\\GitHub\\saveRenamedFile.txt";
    String newDirectory2 = "C:\\Users\\user\\Documents\\GitHub\\main\\memory.txt";
    String SettingsDirectory = "settings.txt";
    File defaultFile = new File(defaultDirectory);
    File testFile = new File(newDirectory);
    File testFile2 = new File(newDirectory2);
    File settingsFile = new File(SettingsDirectory);
    PrintWriter writer;
    BufferedReader reader;

    // test initializing settings file and moving save file
    @Test
    public void testSettings() {
        // delete any existing file before testing
        defaultFile.delete();
        settingsFile.delete();

        // creating the settings file for the first time
        Storage test = new Storage();
        String testData = "Data Testing";
        String text = "";

        // test moving save file
        assertEquals(true, defaultFile.exists());
        assertEquals(false, testFile.exists());
        assertEquals(true, test.setSaveFileDirectory(newDirectory));
        assertEquals(true, testFile.exists());
        assertEquals(false, defaultFile.exists());

        // test adding data and moving save file
        try {
            writer = new PrintWriter(testFile, "UTF-8");
            writer.println(testData);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(false, testFile2.exists());
        assertEquals(true, test.setSaveFileDirectory(newDirectory2));
        assertEquals(true, testFile2.exists());
        assertEquals(false, testFile.exists());
        try {
            reader = new BufferedReader(new FileReader(testFile2));
            text = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(testData, text);
    }

    // Test the write and read the task from the file
    @Test
    public void testWriteAndRead() {
        // data for testing
        String[] data = { "attend meeting later at 1200-1400 on 20 Feb",
                "finish homework by 20 Feb", "meet boss later today" };
        ArrayList<Task> tempData = new ArrayList<Task>();
        ArrayList<Task> readData = new ArrayList<Task>();
        for (String string : data) {
            tempData.add(new Task(string));
        }

        Storage test = new Storage();
        // write data to storage
        assertEquals("File updated\n", test.writeTasksToFile(tempData));
        // read data from storage
        readData = test.readTasksFromFile();
        // compare write data and read data
        for (int i = 0; i < readData.size(); i++) {
            assertEquals(tempData.get(i).getRawInfo(), readData.get(i).getRawInfo());
            assertEquals(tempData.get(i).getTaskStatus(), readData.get(i).getTaskStatus());
            assertEquals(tempData.get(i).getInfo(), readData.get(i).getInfo());
            assertEquals(tempData.get(i).getTaskStatus(), readData.get(i).getTaskStatus());
            assertEquals(tempData.get(i).getDate(), readData.get(i).getDate());
            assertEquals(tempData.get(i).getStartTime(), readData.get(i).getStartTime());
            assertEquals(tempData.get(i).getEndTime(), readData.get(i).getEndTime());
        }
    }

    @After
    public void deleteAllFile() {
        testFile.delete();
        testFile2.delete();
        defaultFile.delete();
        settingsFile.delete();
    }
}
