package AdminGUI;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

public class encryption {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "TheBestSecretKey"; // Fixed key, but should be 16/24/32 bytes

    // Method to encrypt a string
    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    // Method to decrypt a string
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    // Generate a new key
    private static Key generateKey() throws Exception {
        byte[] keyValue = KEY.getBytes();
        return new SecretKeySpec(keyValue, ALGORITHM);
    }

    // Method to encrypt the contents of a JSON file
    public static void encryptJsonFile(String inputFilePath, String outputFilePath) throws Exception {
        // Read the contents of the JSON file
        String fileContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));

        // Encrypt the contents
        String encryptedData = encrypt(fileContent);

        // Write the encrypted data to a file
        Files.write(Paths.get(outputFilePath), encryptedData.getBytes());

        System.out.println("File encrypted successfully: " + outputFilePath);
    }

    public static void decryptJsonFile(String inputFilePath, String outputFilePath) throws Exception {
        // Read the encrypted data from the file
        String encryptedData = new String(Files.readAllBytes(Paths.get(inputFilePath)));

        // Decrypt the data
        String decryptedData = decrypt(encryptedData);

        // Write the decrypted data back to a JSON file
        Files.write(Paths.get(outputFilePath), decryptedData.getBytes());

        System.out.println("File decrypted successfully: " + outputFilePath);
    }


}