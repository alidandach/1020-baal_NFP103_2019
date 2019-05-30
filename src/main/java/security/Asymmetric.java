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
     *
     * @throws NoSuchAlgorithmException occur
     */
    public Asymmetric(int keyLength)throws NoSuchAlgorithmException {
        //generate pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keyLength);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

    }

    /**
     * getter private key
     *
     * @return private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * getter public key
     *
     * @return
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     *
     * @param message message to be encrypt
     *
     * @return encrypted data in form of byte
     *
     * @throws BadPaddingException occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException occur
     * @throws NoSuchPaddingException occur
     * @throws NoSuchAlgorithmException occur
     */
    public byte[] encrypt(String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    /**
     * decryption data
     *
     * @param message message to be encrypted
     *
     * @return encrypted data in form of byte
     *
     * @throws BadPaddingException occur
     * @throws IllegalBlockSizeException occur
     * @throws InvalidKeyException occur
     * @throws NoSuchPaddingException occur
     * @throws NoSuchAlgorithmException occur
     */
    public byte[] decrypt(String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(message.getBytes());
    }
}
