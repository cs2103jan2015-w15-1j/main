import java.io.File;
import java.util.ArrayList;

public class Storage {
    private File saveFile;

    public Storage(String fileName) {
        saveFile = new File(fileName);
        createMissingFile(saveFile);
    }

    private void createMissingFile(File saveFile2) {
    }

    public String writeTasksToFile(ArrayList<Task> input) {
        return null;
    }

    public ArrayList<Task> getTasksFromFile() {
        return null;
    }
}
