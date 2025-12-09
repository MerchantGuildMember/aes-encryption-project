package project;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class project {
    private static boolean firstStartUp = true;
    private static int menuDelayMS = 20;
    private static final int numberAES = 128;
    private static final String algorithm = "AES/GCM/NoPadding";
    private static final String dataPath = "data/";
    private static String fileName = "";
    private static String outputFileName = "";
//    private static File outputFile = new File(outputFileName);

    public static Scanner keyboard = new Scanner(System.in);


    public static void main(String[] args) {
        String input;

        System.out.println("Current working directory: " + System.getProperty("user.dir"));


        do{
                menu();

                System.out.print("Your input: ");
                input = keyboard.nextLine();
                if(input.equals("1")) {
                    input = "-1";
                    String canFileName = fileNameInput();
                    if(canFileName.equals("-1")) {
                        System.out.println("User exited file name input...");
                    } else {
                        fileName = dataPath + canFileName;
                        System.out.println("filename: "+fileName);
                        outputFileName = fileName.substring(0,fileName.indexOf(".")) + "_enc.txt";
                        System.out.println("output: " + outputFileName);
                        groupedEncryption();
                    }
                    System.out.println("reached encrypt");
                } else if(input.equals("2")) {
                    input = "-1";
                    groupedDecryption();
                    System.out.println("reached decrypt");
                } else if(input.equals("3")) {
                    exitProgram();
                    System.out.println("reached exit");
                } else {
                    input = "-1";
                    System.out.println("Invalid input, please try again!\n");
                }
            } while(!input.equals("1") || !input.equals("2") ||!input.equals("3") );

    }
    public static String fileNameInput() {
        System.out.println("Assuming text files are in data folder.");
        System.out.println("Please input file name, make sure to include file extension | [-1] to exit: ");
        String canFileName = "";
        do {
            canFileName = keyboard.nextLine();
            if(canFileName.equals("-1")) {return canFileName;}
            else if(!(new File(dataPath + canFileName).exists())) {System.out.println("File not found: " + new File(dataPath + canFileName).getAbsolutePath());}
            System.out.println(canFileName);
        } while(!canFileName.endsWith(".txt") || !(new File(dataPath + canFileName).exists()));

        return canFileName;
    }

    public static File createOutputFile(String fileName) {
        File outputFile = new File(fileName);
        if(!outputFile.exists()) {
            try {
                if(outputFile.createNewFile()) {
                    System.out.println("File created: " + outputFile.getName());
                }
            } catch (IOException e) {
                System.out.println("ERROR: Unable to create new file!");
                throw new RuntimeException(e);
            }
        }
        return outputFile;
    }

    public static File fileToEncrypt(String path) {
        File file = new File(path);
        return file;
    }

    public static File fileToDecrypt(String path) {
        File file = new File(outputFileName);
        return file;
    }

    public static void groupedEncryption() {
        try {
            encryptFile(algorithm,generateKey(numberAES),generateIv(),fileToEncrypt(fileName),createOutputFile(outputFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }
    public static void groupedDecryption() {
        try {
            decryptFile(algorithm,generateKey(numberAES),generateIv(),fileToDecrypt(outputFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }


    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static GCMParameterSpec generateIv() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
    }

    public static void encryptFile(String algorithm, SecretKey key, GCMParameterSpec iv,
                                   File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String algorithm, SecretKey key, GCMParameterSpec iv,
                                   File inputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                System.out.print(Arrays.toString(output));
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            System.out.print(Arrays.toString(outputBytes));
        }
        inputStream.close();
    }

    public static void exitProgram() {
        System.out.println("\n....Shutting down....");
        System.exit(0);
    }

    public static void menu() {
        String divider = "==========================================";
        String menu = "Greetings, please choose one of the following options:\n[1] Encrypt a File\n[2] Decrypt a File\n[3] Quit\n";

        if(!firstStartUp) { menuDelayMS = 0;}

        System.out.println("\n\n\n"+divider);
        for(int i = 0; i < menu.length(); i++) {
            try {
                Thread.sleep(project.menuDelayMS);
                System.out.print(menu.charAt(i));
                Thread.sleep(project.menuDelayMS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(divider);

        firstStartUp = false;

    }

}
