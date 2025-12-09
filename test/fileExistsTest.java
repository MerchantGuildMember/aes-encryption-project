package project;

import java.io.File;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class fileExistsTest {
    @Test
    public void firstTest() {
        String pathname = "data/test1.txt";
        File file = new File(pathname);
        Assertions.assertTrue(file.exists());
    }
    @Test
    public void secondTest() {
        String pathname = "data/test1.txt";
        project.createOutputFile(pathname);
        File file = new File(pathname);
        Assertions.assertTrue(file.exists());
    }
}
