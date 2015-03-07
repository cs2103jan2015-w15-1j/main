import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Storage {
    private static final String MESSAGE_ADDED = "File updated\n";
    private static final String MESSAGE_DIR_CHANGED_SUCCESSFUL = "Save file directory changed\n";
    private static final String MESSAGE_DIR_CHANGED_FAILED = "Failed to change file directory";
    private static final String DEFAULT_SAVE_FILE = "savefile.txt";
    private static final String SETTINGS_FILE_NAME = "settings.txt";

    private File settingsFile;
    private File saveFile;
    private String saveFileName;
    private BufferedReader reader;
    private PrintWriter writer;

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
        if (task.getTaskStatus()) {
            string += "T ";
        } else {
            string += "F ";
        }
        string += task.getRawInfo();
        return string;
    }

    public ArrayList<Task> getTasksFromFile() {
        ArrayList<Task> storage = new ArrayList<Task>();
        String text;
        String info;
        Boolean isCompleted = false;

        initBufferedReader(saveFile);
        try {
            while ((text = reader.readLine()) != null) {
                isCompleted = checkCompletion(text);
                info = text.substring(text.indexOf(' ')).trim();
                Task task = new Task(info);
                if (isCompleted) {
                    task.markAsComplete();
                }
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

    private Boolean checkCompletion(String text) {
        if (text.substring(0, text.indexOf(' ')).equals("T")) {
            return true;

        } else {
            return false;
        }
    }

    private void initBufferedReader(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String setSaveFileDirectory(String input) {
        saveFileName = input;
        if (saveFile.renameTo(new File(saveFileName))) {
            updateSettingsFile(saveFileName);
            saveFile = new File(saveFileName);
            createIfMissingFile(saveFile);
            return MESSAGE_DIR_CHANGED_SUCCESSFUL;
        } else {
            return MESSAGE_DIR_CHANGED_FAILED;
        }
    }

    public File getSaveFile() {
        return saveFile;
    }
}
