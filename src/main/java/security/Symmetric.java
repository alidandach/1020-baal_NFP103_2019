package security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Symmetric {

    private SecretKey secretKey;

    public static byte[] encrypt(byte[] plaintext, String encodedKey, byte[] IV) throws Exception {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, originalKey, ivSpec);

        //Perform Encryption
        return cipher.doFinal(plaintext);

    }

    public static byte[] decrypt(byte[] cipherText, String encodedKey, byte[] IV) throws Exception {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(originalKey.getEncoded(), "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        //Perform Decryption
        return cipher.doFinal(cipherText);
    }

    /**
     * getter secret key
     *
     * @return String
     */
    public byte[] getSecretKey() {
        return secretKey.getEncoded();
    }

    /**
     * setter for secret key
     *
     * @throws NoSuchAlgorithmException occur
     */
    public void setKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        // Generate Key
        secretKey = keyGenerator.generateKey();

        // Generating IV.
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

    }

    /**
     * method used to encrypt data using eas algorithm
     *
     * @param plaintext in form of byte
     * @return encrypted data
     * @throws BadPaddingException       occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException       occur
     * @throws NoSuchPaddingException    occur
     * @throws NoSuchAlgorithmException  occur
     */
    public byte[] encrypt(byte[] plaintext) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        //Perform Encryption
        return cipher.doFinal(plaintext);
    }

    /**
     * method used to decrypt data using eas algorithm
     *
     * @param cipherText encrypted message
     * @return decrypted data
     * @throws BadPaddingException       occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException       occur
     * @throws NoSuchPaddingException    occur
     * @throws NoSuchAlgorithmException  occur
     */
    public byte[] decrypt(byte[] cipherText) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);


        //Perform Decryption
        return cipher.doFinal(cipherText);
    }
}
