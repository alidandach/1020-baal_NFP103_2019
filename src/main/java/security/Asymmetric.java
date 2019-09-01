package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class Asymmetric {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Constructor
     *
     * @param keyLength length of key to be initialize
     * @throws NoSuchAlgorithmException occur
     */
    public Asymmetric(int keyLength) throws NoSuchAlgorithmException {
        //generate pair
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keyLength, random);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

    }

    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * getter private key
     *
     * @return Private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * getter public key
     *
     * @return Public Key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * @param data data to be encrypt
     * @return encrypted data in form of byte
     * @throws BadPaddingException       occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException       occur
     * @throws NoSuchPaddingException    occur
     * @throws NoSuchAlgorithmException  occur
     */
    public byte[] encrypt(String data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * decryption data
     *
     * @param data data to be encrypted
     * @return encrypted data in form of byte
     * @throws BadPaddingException       occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException       occur
     * @throws NoSuchPaddingException    occur
     * @throws NoSuchAlgorithmException  occur
     */
    public byte[] decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

}
