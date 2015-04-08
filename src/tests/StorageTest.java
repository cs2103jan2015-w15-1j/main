package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import main.java.DateParser;
import main.java.Storage;
import main.java.Task;

import org.junit.After;
import org.junit.Test;

//@author A0122393L
public class StorageTest {
    String defaultDirectory = "savefile.txt";
    String newDirectory = "saveRenamedFile.txt";
    String newDirectory2 = "memory.txt";
    String SettingsDirectory = "settings.txt";
    String backupDirectory = "backup.txt";
    File defaultFile = new File(defaultDirectory);
    File backupFile = new File(backupDirectory);
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
        Storage test = Storage.getInstance();
        String testData = "Data Testing";
        String text = "";

        // test moving save file
        assertEquals(true, defaultFile.exists());
        assertEquals(false, testFile.exists());
        assertEquals(true,
                test.moveSaveFileDirectory(newDirectory));
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
        assertEquals(true,
                test.moveSaveFileDirectory(newDirectory2));
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
        
        System.out.println("End of settings test");
    }

    // Test the write and read the task from the file
    @Test
    public void testWriteAndRead() {
        // data for testing
        String[] data = { "attend meeting later at 1200-1400 on 20 Feb",
                "finish homework by 20 Feb", "meet boss later today" };
        ArrayList<Task> tempData = new ArrayList<Task>();
        ArrayList<Task> readData = new ArrayList<Task>();
        createArrayListOfTask(data, tempData);

        Storage test = Storage.getInstance();
        // write data to storage
        assertEquals(true, test.updateFiles(tempData));
        // read data from storage
        readData = test.readFile();
        compareData(tempData, readData);
        
        System.out.println("End of write and read test");
    }

    // Test backup file
    @Test
    public void testBackup() {
        // data for testing
        String[] data = { "attend meeting later at 1200-1400 on 20 Feb",
                "finish homework by 20 Feb", "meet boss later today" };
        ArrayList<Task> tempData = new ArrayList<Task>();
        ArrayList<Task> readData = new ArrayList<Task>();
        createArrayListOfTask(data, tempData);

        Storage test = Storage.getInstance();
        assertEquals(true, test.updateFiles(tempData));
        
        // corrupting data
        try {
            writer = new PrintWriter(defaultFile, "UTF-8");
            writer.println("corrupting the desired save file format");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        readData = test.readFile();
        compareData(tempData, readData);
        
        // if save file is not found
        defaultFile.delete();
        readData = test.readFile();
        compareData(tempData, readData);
        
        // if backup is corrupted too, all data will be lost
        try {
            backupFile.setWritable(true);
            writer = new PrintWriter(backupFile, "UTF-8");
            writer.println("corrupting the desired save file format");
            writer.close();
            backupFile.setWritable(false);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        readData = test.readFile();
        assertEquals(true, readData.isEmpty());
        
        // if backup file is not found
        readData = test.readFile();
        assertEquals(true, readData.isEmpty());
        
        System.out.println("End of backup test");
    }

    // method to set-up the ArrayList
    private void createArrayListOfTask(String[] data, ArrayList<Task> tempData) {
        DateParser parser = DateParser.getInstance();
        for (String string : data) {
            parser.parse(string);
            ArrayList<LocalDateTime> parsedDates = parser.getDates();
            String parsedWords = parser.getParsedWords();
            String nonParsedWords = parser.getNotParsedWords();
            tempData.add(new Task(string, parsedDates, parsedWords, nonParsedWords));
        }
    }

    // method to compare and check all variables of tasks
    private void compareData(ArrayList<Task> tempData, ArrayList<Task> readData) {
        for (int i = 0; i < readData.size(); i++) {
            assertEquals(tempData.get(i).isCompleted(), readData.get(i)
                    .isCompleted());
            assertEquals(tempData.get(i).getDescription(), readData.get(i)
                    .getDescription());
            assertEquals(tempData.get(i).isCompleted(), readData.get(i)
                    .isCompleted());
            assertEquals(tempData.get(i).getDate(), readData.get(i).getDate());
            assertEquals(tempData.get(i).getStartTime(), readData.get(i)
                    .getStartTime());
            assertEquals(tempData.get(i).getEndTime(), readData.get(i)
                    .getEndTime());
        }
    }

    @After
    public void deleteAllFile() {
        testFile.delete();
        testFile2.delete();
        defaultFile.delete();
        settingsFile.delete();
        backupFile.delete();
    }
}
