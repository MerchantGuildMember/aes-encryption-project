
package project;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;


public class project {

    public static Scanner scanner = new Scanner(System.in);
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int n = 128;
    private static SecretKey key;
    private static final String DATA_DIR = "data/";
    private static final String KEY_FILE = DATA_DIR + "key.txt";
    private static final String IV_FILE = DATA_DIR + "iv.txt";
    private static GCMParameterSpec iv;

    static void main() {
        String title = "\nAES Encryption/Decryption menu";
        String[] menu_options = {
                "[1] Encrypt a File",
                "[2] Decrypt a File",
                "[3] Export key (do after running [1])",
                "[4] Export IV (do after running [1])",
                "[5] Import key",
                "[6] Import IV",
                "[7] Exit the program",
        };
        try {
            while(true) {
                MenuUtil.displayMenu(menu_options, title);
                System.out.print("Enter: ");
                int user_input = 0;
                try {
                    user_input = MenuUtil.getMenuChoice(menu_options.length);
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, try again.");
                    continue;
                }
                switch(user_input) {
                    case 1:
                        encryptFirstStage();
                        break;
                    case 2:
                        decryptFirstStage();
                        break;
                    case 3:
                        System.out.println("Trying to export to key.txt ...");
                        exportKey();
                        break;
                    case 4:
                        System.out.println("Trying to export to iv.txt ...");
                        exportIV();
                        break;
                    case 5:
                        System.out.println("Trying to import from key.txt ...");
                        importKey();
                        break;
                    case 6:
                        System.out.println("Trying to import from iv.txt ...");
                        importIV();
                        break;
                    case 7:
                        System.out.println("Shutting program down...");
                        System.exit(0);
                        break;
                    default:
                        break;
                }

            }
        } catch (Exception e) {
            System.out.println("Unexpected error has occurred: "+e.getMessage());
            System.out.println("Returning to main menu\n");
        }
    }

    public static void encryptFirstStage() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
            System.out.println("\n File Encryption ");
            System.out.print("Enter the filename to encrypt: ");
            String file_name = scanner.nextLine().trim();

            while(file_name.isEmpty()) {
                System.out.println("Error: Filename cannot be empty!");
                System.out.print("Enter the filename to encrypt: ");
                file_name = scanner.nextLine().trim();
            }

            File file = new File(DATA_DIR+file_name);
            if(!file.exists()) {
                System.out.println("Error File 'data/"+ file_name + "' not found");
                System.out.println("Ensure the file exists and that you have the right name");
                return;
            }

            File encrypted_file = new File(DATA_DIR+"enc_"+file_name);
            if(!encrypted_file.exists()) {
                try {
                    encrypted_file.createNewFile();
                } catch (IOException e) {
                    System.out.println("Failed to create file, returning to main menu!");
                    return;
                }
            } else {
                System.out.print("File " + encrypted_file.getName() + " already exists. Overwrite? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if(!response.equals("y")) {
                    System.out.println("Operation cancelled");
                    return;
                }
            }

            if(Objects.isNull(key)) {
//                System.out.println("reaching null check 123123");
                key = generateKey(n);
            }
            if(Objects.isNull(iv)) {
                iv = generateIv();
            }

        encryptFile(ALGORITHM,key,iv,file,encrypted_file);

    }

    public static void decryptFirstStage() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        System.out.println("\nFile Decryption");
        System.out.print("Enter the filename to decrypt: ");
        String file_name = scanner.nextLine().trim();

        while(file_name.isEmpty()) {
            System.out.println("Error: Filename cannot be empty!");
            System.out.print("Enter the filename to decrypt: ");
            file_name = scanner.nextLine().trim();
        }
        if(!file_name.startsWith("enc_")) {
            System.out.println("Error: Filename is missing leading \"enc_\", fix this before trying again");
            return;
        }

        File file = new File(DATA_DIR+file_name);
        if(!file.exists()) {
            System.out.println("Error File 'data/"+ file_name + "' not found");
            System.out.println("Ensure the file exists and that you have the right name");
            return;
        }

        // assuming user is going to be reading from a file they just encrypted
        File decrypted_file = null;
        if(file_name.startsWith("enc_")) {
            decrypted_file = new File(DATA_DIR+"dec_"+(file_name.substring(file_name.indexOf("_") + 1)));
        } else {
            decrypted_file = new File(DATA_DIR+"dec_"+file_name);
        }
        if(!decrypted_file.exists()) {
            try {
                decrypted_file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create file, returning to main menu!");
                return;
            }
        } else {
            System.out.print("File " + decrypted_file.getName() + " already exists. Overwrite? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if(!response.equals("y")) {
                System.out.println("Operation cancelled");
                return;
            }
        }

        if (Objects.isNull(key)) {
            System.out.println("Error: No key loaded. Please import a key first (option 5).");
            return;
        }
        if (Objects.isNull(iv)) {
            System.out.println("Error: No IV loaded. Please import an IV first (option 6).");
            return;
        }

        decryptFile(ALGORITHM,key,iv,file,decrypted_file);

    }

    //    Sharifi, R. (2025) 'Java AES encryption and decryption', Baeldung. Available at: https://www.baeldung.com/java-aes-encryption-decryption (Accessed: 10 December 2025).

    public static void encryptFile(String algorithm,
                                   SecretKey key,
                                   GCMParameterSpec iv,
                                   File input_file,
                                   File output_file)
            throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        // instantiating a cipher instance with "AES/GCM/NoPadding"
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream input_stream = new FileInputStream(input_file);
        FileOutputStream output_stream = new FileOutputStream(output_file);
        byte[] buffer = input_stream.readAllBytes();
        int bytes_read;
        while ((bytes_read = input_stream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytes_read);
            if (output != null) {
                output_stream.write(output);
            }
        }
        byte[] output_bytes = cipher.doFinal(buffer);
        output_stream.write(output_bytes);
        input_stream.close();
        output_stream.close();

        System.out.println("Check data folder for the file");
    }

    //    Sharifi, R. (2025) 'Java AES encryption and decryption', Baeldung. Available at: https://www.baeldung.com/java-aes-encryption-decryption (Accessed: 10 December 2025).

    public static void decryptFile(String algorithm,
                                   SecretKey key,
                                   GCMParameterSpec iv,
                                   File input_file,
                                   File output_file)
            throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream input_stream = new FileInputStream(input_file);
        FileOutputStream output_stream = new FileOutputStream(output_file);
        byte[] buffer = new byte[64];
        int bytes_read;
        while ((bytes_read = input_stream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytes_read);
            if (output != null) {
                output_stream.write(output);
            }
        }
        byte[] output_bytes = cipher.doFinal();
        if (output_bytes != null) {
            output_stream.write(output_bytes);
        }
        input_stream.close();
        output_stream.close();

        System.out.println("Check data folder for the file");
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        if(!Objects.isNull(key)) {
            return key;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static void exportKey() {
        if(Objects.isNull(key)) {
            System.out.println("Key is null, generate a key first and try again.");
            return;
        }
        byte[] output_bytes = key.getEncoded();
        File output_file = new File(KEY_FILE);
        if(!output_file.exists()) {
            try {
                output_file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create exporting key file! Exiting program...");
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream output_stream = new FileOutputStream(output_file);
            output_stream.write(output_bytes);
            output_stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Exported!\n");
    }
    public static void exportIV() {
        if(Objects.isNull(iv)) {
            System.out.println("IV is null, generate key first and try again.");
            return;
        }

        byte[] output_bytes = iv.getIV();
        File output_file = new File(IV_FILE);
        if(!output_file.exists()) {
            try {
                output_file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create exporting iv file! Exiting program...");
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream output_stream = new FileOutputStream(output_file);
            output_stream.write(output_bytes);
            output_stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Exported!\n");
    }

    public static void importKey() {
        File input_file = new File(KEY_FILE);
        if(!input_file.exists()) {
            System.out.println("File doesn't exist");
            return;
        }
        try {
            FileInputStream input_stream = new FileInputStream(input_file);
            byte[] input_bytes = input_stream.readAllBytes();
            input_stream.close();
            System.out.println("Imported");
            key = new SecretKeySpec(input_bytes, "AES");
        } catch (IOException e) {
            throw new RuntimeException("Error reading key file: " + e.getMessage());
        }
    }

    public static void importIV() {
        byte[] input_bytes = new byte[12];
        File input_file = new File(IV_FILE);
        if(!input_file.exists()) {
            System.out.println("File doesn't exist");
            return;
        }
        try {
            FileInputStream input_stream = new FileInputStream(input_file);
            input_stream.read(input_bytes);
            input_stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Imported");
        iv = new GCMParameterSpec(128, input_bytes);
    }

    public static GCMParameterSpec generateIv() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
    }


}