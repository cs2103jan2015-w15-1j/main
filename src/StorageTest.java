import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class StorageTest {
    String newDirectory = "C:\\Users\\user\\Documents\\GitHub\\saveRenamedFile.txt";
    String newDirectory2 = "C:\\Users\\user\\Documents\\GitHub\\main\\memory.txt";
    String SettingsDirectory = "settings.txt";
    File testFile = new File(newDirectory);
    File testFile2 = new File(newDirectory2);
    File settingsFile = new File(SettingsDirectory);
    PrintWriter writer;
    BufferedReader reader;

    @Test
    public void testSettings() {
        Storage test = new Storage();
        String testData = "Data Testing";
        String text = "";

        // test moving files
        assertEquals(true, test.getSaveFile().exists());
        assertEquals(false, testFile.exists());
        test.setSaveFileDirectory(newDirectory);
        assertEquals(true, test.getSaveFile().exists());

        // test adding data and moving files with data
        try {
            writer = new PrintWriter(testFile, "UTF-8");
            writer.println(testData);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(false, testFile2.exists());
        test.setSaveFileDirectory(newDirectory2);
        assertEquals(true, testFile2.exists());
        try {
            reader = new BufferedReader(new FileReader(testFile2));
            text = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(testData, text);

        // reset to default
        assertEquals(true, testFile2.delete());
        assertEquals(true, settingsFile.delete());
    }
}
