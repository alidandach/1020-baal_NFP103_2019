package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Symmetric {

    private SecretKeySpec secretKey;

    /**
     * Constructor
     *
     * @param key String key used in encryption and decryption
     *
     * @throws NoSuchAlgorithmException occur
     */
    public Symmetric(String key) throws NoSuchAlgorithmException{
        setKey(key);
    }

    /**
     * getter secret key
     *
     * @return String
     */
    public String getSecretKey(){
        return new String(Base64.getDecoder().decode(secretKey.getEncoded()));
    }


    /**
     * setter for secret key
     *
     * @param key secret key
     *
     * @throws NoSuchAlgorithmException occur
     */
    public void setKey(String key) throws NoSuchAlgorithmException {

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        secretKey = new SecretKeySpec(keyBytes, "AES");

    }

    /**
     * method used to encrypt data using eas algorithm
     *
     * @param message String to be encrypt
     *
     * @return encrypted data
     *
     * @throws BadPaddingException occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException occur
     * @throws NoSuchPaddingException occur
     * @throws NoSuchAlgorithmException occur
     */
    public String encrypt(String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * method used to decrypt data using eas algorithm
     *
     * @param message encrypted message
     *
     * @return decrypted data
     *
     * @throws BadPaddingException occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException occur
     * @throws NoSuchPaddingException occur
     * @throws NoSuchAlgorithmException occur
     */
    public String decrypt(String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
    }
}
