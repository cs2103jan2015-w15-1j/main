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

public class Storage {
    private static final Logger logger = Logger.getLogger("VetoStorage");

    private static final String MESSAGE_ADDED = "File updated\n";
    private static final String MESSAGE_ADD_FAIL = "File not updated\n";
    private static final String DEFAULT_SAVE_FILE = "savefile.txt";
    private static final String SETTINGS_FILE_NAME = "settings.txt";
    private static final String BACKUP_FILE_NAME = "backup.txt";
    private static final String MESSAGE_SAVE_DEST = "File save destination has been confirmed. \n";
    private static final String MESSAGE_SAVE_FAIL = "File save destination failed. \n";

    private File settingsFile;
    private File saveFile;
    private File backupFile;
    private String saveFileName;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson;

    // @Tan Chia Kai A0122393L
    // search the settings file and open the save file
    public Storage() {
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

    // @Tan Chia Kai A0122393L
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

    // @Tan Chia Kai A0122393L
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

    // @Tan Chia Kai A0122393L
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

    // @Tan Chia Kai A0122393L
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
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        for (Task task : input) {
            writer.println(taskToInfoString(task));
        }
        writer.close();
        return true;
    }

    // @Tan Chia Kai A0122393L
    // converts task object to string
    private Object taskToInfoString(Task task) {
        return gson.toJson(task);
    }

    // @Tan Chia Kai A0122393L
    // reads all task objects from the save file
    public ArrayList<Task> readTasksFromFile() {
        ArrayList<Task> storage = new ArrayList<Task>();
        String text;

        initBufferedReader(saveFile);
        try {
            while ((text = reader.readLine()) != null) {
                Task task = gson.fromJson(text, Task.class);
                storage.add(task);
            }
        } catch (IOException | JsonSyntaxException e1) {
            closeBufferedReader();
            initBufferedReader(backupFile);
            logger.log(Level.INFO, "File corrupted, backup file used");
            try {
                while ((text = reader.readLine()) != null) {
                    Task task = gson.fromJson(text, Task.class);
                    storage.add(task);
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        closeBufferedReader();
        return storage;
    }

    // @Tan Chia Kai A0122393L
    // close buffered reader
    private void closeBufferedReader() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // @Tan Chia Kai A0122393L
    // initialize buffered reader
    private void initBufferedReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // @Tan Chia Kai A0122393L
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

    // @Tan Chia Kai A0122393L
    // get the name of save file
    public String getSaveFileName() {
        return saveFileName;
    }
}
