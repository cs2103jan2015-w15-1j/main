import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class StorageTest {
    String newDirectory = "C:\\Users\\user\\Downloads\\saveRenamedFile.txt";
    String newDirectory2 = "C:\\Users\\user\\Documents\\GitHub\\memory.txt";
    String SettingsDirectory = "settings.txt";
    File testFile = new File(newDirectory);
    File testFile2 = new File(newDirectory2);
    File settingsFile = new File(SettingsDirectory);
    
    @Test
    // yet to check if the details in the files are transfered along
    public void testSettings(){
        Storage test = new Storage();
        assertEquals(true, test.getSaveFile().exists());
        assertEquals(false, testFile.exists());
        test.setSaveFileDirectory(newDirectory);
        assertEquals(true, test.getSaveFile().exists());
        assertEquals(false, testFile2.exists());
        test.setSaveFileDirectory(newDirectory2);
        assertEquals(true, testFile2.exists());
        assertEquals(true, testFile2.delete());
        assertEquals(true, settingsFile.delete());
    }
}
