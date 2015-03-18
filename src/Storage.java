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

public class Storage {
    private static final Logger logger = Logger.getLogger("VetoStorage");
    
    private static final String MESSAGE_ADDED = "File updated\n";
    private static final String DEFAULT_SAVE_FILE = "savefile.txt";
    private static final String SETTINGS_FILE_NAME = "settings.txt";

    private File settingsFile;
    private File saveFile;
    private String saveFileName;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson;

    // search the settings file and open the save file
    public Storage() {
        gson = new Gson();
        settingsFile = new File(SETTINGS_FILE_NAME);
        createIfMissingFile(settingsFile);
        saveFileName = getSaveFileNameFromSettingsFile(settingsFile);
        updateSettingsFile(saveFileName);
        saveFile = new File(saveFileName);
        createIfMissingFile(saveFile);
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
                logger.log(Level.INFO, fileName.toString() + " have been created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes all task objects in the list to the save file
    public String writeTasksToFile(ArrayList<Task> input) {
        try {
            writer = new PrintWriter(saveFile, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (Task task : input) {
            writer.println(taskToInfoString(task));
        }
        writer.close();
        return String.format(MESSAGE_ADDED);
    }

    // converts task object to string
    private Object taskToInfoString(Task task) {
        return gson.toJson(task);
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeBufferedReader();
        return storage;
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
    private void initBufferedReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // change save file directory
    public Boolean setSaveFileDirectory(String input) {
        saveFileName = input;
        if (saveFile.renameTo(new File(saveFileName))) {
            updateSettingsFile(saveFileName);
            saveFile = new File(saveFileName);
            createIfMissingFile(saveFile);
            logger.log(Level.INFO, "File directory changed to " + saveFile.toString());
            return true;
        } else {
            return false;
        }
    }
   
    public String getSaveFileName() {
        return saveFileName;
    }

    // debug method for clear
    
}
