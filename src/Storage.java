import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Storage {
    private static final String MESSAGE_ERROR = "Error: %s\n";
    private static final String MESSAGE_ADDED = "File updated\n";

    private File saveFile;
    private BufferedReader reader;

    public Storage(String fileName) {
        if (!fileName.contains(".txt")) {
            fileName += ".txt";
        }
        saveFile = new File(fileName);
        createMissingFile(saveFile);
    }

    private void createMissingFile(File fileName) {
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
        } catch (Exception e) {
            System.out.printf(MESSAGE_ERROR, e.getMessage());
        }
    }

    public String writeTasksToFile(ArrayList<Task> input) {
        try {
            PrintWriter writer = new PrintWriter(saveFile, "UTF-8");
            for (Task task : input) {
                writer.println(taskToInfoString(task));
            }
            writer.close();
        } catch (Exception e) {
            return String.format(MESSAGE_ERROR, e.getMessage());
        }
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

        try {
            initBufferedReader(saveFile);
            while ((text = reader.readLine()) != null) {
                isCompleted = checkCompletion(text);
                info = text.substring(text.indexOf(' ')).trim();
                Task task = new Task(info);
                if (isCompleted) {
                    task.markAsComplete();
                }
                storage.add(task);
            }
        } catch (Exception e) {
            System.out.printf(MESSAGE_ERROR, e.getMessage());
        }
        return storage;
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
            System.out.printf(MESSAGE_ERROR, e.getMessage());
        }
    }

    public String setSaveFileDest(String input) {
        // TODO Auto-generated method stub
        return null;
    }
}
