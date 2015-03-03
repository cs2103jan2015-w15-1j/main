import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Storage {
    private static final String MESSAGE_ERROR = "Error: %s\n";
    private static final String MESSAGE_ADDED = "File updated\n";
    
    private File saveFile;

    public Storage(String fileName) {
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
        return null;
    }

    public ArrayList<Task> getTasksFromFile() {
        return null;
    }
}
