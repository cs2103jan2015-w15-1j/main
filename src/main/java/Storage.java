package main.java;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

//@author A0122393L
public class Storage {
    private static final Logger logger = Logger.getLogger("VetoStorage");

    private static final String MESSAGE_ADDED = "File updated\n";
    private static final String MESSAGE_ADD_FAIL = "File not updated\n";
    private static final String DEFAULT_SAVE_FILE = "savefile.txt";
    private static final String SETTINGS_FILE_NAME = "settings.txt";
    private static final String BACKUP_FILE_NAME = "backup.txt";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";
    private static final String MESSAGE_SAVE_FAIL = "File save destination failed. \n";

    private static Storage storage;
    private static File settingsFile;
    private File saveFile;
    private File backupFile;
    private String saveFileName;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson;

    // search the settings file and open the save file
    public static Storage getInstance() {
        if (storage == null || !settingsFile.exists()) {
            storage = new Storage();
        }
        return storage;
    }

    private Storage() {
        gson = new Gson();
        settingsFile = new File(SETTINGS_FILE_NAME);
        createIfMissingFile(settingsFile);
        saveFileName = getSaveFileNameFromSettingsFile(settingsFile);
        updateSettingsFile(saveFileName);
        saveFile = new File(saveFileName);
        createIfMissingFile(saveFile);
        backupFile = new File(BACKUP_FILE_NAME);
        createIfMissingFile(backupFile);
        backupFile.setWritable(false);
        logger.log(Level.INFO, "Storage Initialised");
    }

    // update settings file on the changes of save file directory
    private void updateSettingsFile(String fileName) {
        try {
            writer = new PrintWriter(settingsFile, "UTF-8");
            writer.println(fileName);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // get the directory of the save file from settings file
    private String getSaveFileNameFromSettingsFile(File fileName) {
        String text = "";
        initBufferedReader(fileName);
        try {
            if ((text = reader.readLine()) == null) {
                text = DEFAULT_SAVE_FILE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeBufferedReader();
        return text;
    }

    // create the file if not found
    private void createIfMissingFile(File fileName) {
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
                logger.log(Level.INFO, fileName.toString()
                        + " have been created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update necessary files
    public String updateFiles(ArrayList<Task> input) {
        Boolean hasUpdatedSaveFile = false;
        Boolean hasUpdatedBackup = false;

        hasUpdatedSaveFile = writeTasksToFile(saveFile, input);
        backupFile.setWritable(true);
        hasUpdatedBackup = writeTasksToFile(backupFile, input);
        backupFile.setWritable(false);

        if (hasUpdatedSaveFile && hasUpdatedBackup) {
            return String.format(MESSAGE_ADDED);
        } else {
            return String.format(MESSAGE_ADD_FAIL);
        }
    }

    // writes all task objects in the list to the save file
    private Boolean writeTasksToFile(File fileName, ArrayList<Task> input) {
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            for (Task task : input) {
                writer.println(taskToJson(task));
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        writer.close();
        return true;
    }

    // converts task object to string
    private Object taskToJson(Task task) {
        return gson.toJson(task);
    }

    // select file to read
    public ArrayList<Task> readFile() {
        ArrayList<Task> storage = new ArrayList<Task>();
        storage = readSavedTasks(saveFile);
        if (storage.isEmpty()) {
            logger.log(Level.INFO,
                    "File corrupted, try restoring from backup file");
            storage = readSavedTasks(backupFile);
        }
        return storage;
    }

    // reads tasks in the file
    private ArrayList<Task> readSavedTasks(File saveFile) {
        ArrayList<Task> storageData;
        String text = "";
        storageData = new ArrayList<Task>();

        try {
            if (!initBufferedReader(saveFile)) {
                return storageData;
            }
            while ((text = reader.readLine()) != null) {
                Task task = gson.fromJson(text, Task.class);
                storageData.add(task);
            }
        } catch (IOException | JsonSyntaxException e) {
            storageData = new ArrayList<Task>();
        }
        closeBufferedReader();
        return storageData;
    }

    // close buffered reader
    private void closeBufferedReader() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // initialize buffered reader
    private Boolean initBufferedReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    // change save file directory
    public String setSaveFileDirectory(String input) {
        saveFileName = input;
        if (saveFile.renameTo(new File(saveFileName))) {
            updateSettingsFile(saveFileName);
            saveFile = new File(saveFileName);
            createIfMissingFile(saveFile);
            logger.log(Level.INFO,
                    "File directory changed to " + saveFile.toString());
            return MESSAGE_SAVE_DEST;
        } else {
            return MESSAGE_SAVE_FAIL;
        }
    }

    // get the name of save file
    public String getSaveFileName() {
        return saveFileName;
    }
}
