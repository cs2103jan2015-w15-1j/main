import java.io.File;
import java.util.ArrayList;

public class Storage {
    private static final String MESSAGE_ERROR = "Error: %s\n";
    
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
        return null;
    }

    public ArrayList<Task> getTasksFromFile() {
        return null;
    }
}
