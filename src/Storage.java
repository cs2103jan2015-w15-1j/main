import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Storage {
    private static final String MESSAGE_ADDED = "File updated\n";
    private static final String DEFAULT_SAVE_FILE = "savefile.txt";
    private static final String SETTINGS_FILE_NAME = "settings.txt";

    private File settingsFile;
    private File saveFile;
    private String saveFileName;
    private BufferedReader reader;
    private PrintWriter writer; 
    Gson gson = new Gson();

    public Storage() {
        settingsFile = new File(SETTINGS_FILE_NAME);
        createIfMissingFile(settingsFile);
        saveFileName = getSaveFileNameFromSettingsFile(settingsFile);
        updateSettingsFile(saveFileName);
        saveFile = new File(saveFileName);
        createIfMissingFile(saveFile);
    }

    private void updateSettingsFile(String fileName) {
        try {
            writer = new PrintWriter(settingsFile, "UTF-8");
            writer.println(fileName);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

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

    private void createIfMissingFile(File fileName) {
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private Object taskToInfoString(Task task) {
        String string = "";
        string = gson.toJson(task);
        return string;
    }

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

    private void closeBufferedReader() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBufferedReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Boolean setSaveFileDirectory(String input) {
        if (input.contains(" ")) {
            input = input.substring(input.indexOf(' ')).trim();
        }
        saveFileName = input;
        if (saveFile.renameTo(new File(saveFileName))) {
            updateSettingsFile(saveFileName);
            saveFile = new File(saveFileName);
            createIfMissingFile(saveFile);
            return true;
        } else {
            return false;
        }
    }

    public File getSaveFile() {
        return saveFile;
    }
}
