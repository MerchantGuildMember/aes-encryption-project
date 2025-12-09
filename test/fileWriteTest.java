package project;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class fileWriteTest {

    @Test
    public void firstTest() throws FileNotFoundException {
        String pathname = "data/test1.txt";
        File file = new File(pathname);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Reached else statement!");
            FileOutputStream fos = new FileOutputStream(file);
            String cheesecakeDeclaration = "Cheesecake is very delicious and yummy";
            byte[] byteList = cheesecakeDeclaration.getBytes();
            try {
                fos.write(byteList);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                System.out.println("ERROR: Failed to write to file: "+file.getAbsolutePath());
                throw new RuntimeException(e);
            }
            Scanner fileReader = new Scanner(file);
            String byteCode = "";
            System.out.println("Reached while loop!");
            while(fileReader.hasNextLine()) {
                byteCode = byteCode + " " + fileReader.next();
            }
            byteCode = byteCode.substring(1,byteCode.length());

            Assertions.assertArrayEquals(byteList, byteCode.getBytes(), "Should have written Cheesecake is very delicious and yummy to the file.");
        }
    }
}
