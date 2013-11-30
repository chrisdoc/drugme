package at.fhooe.drugme.utils;

import android.content.Context;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author s1210455002
 */
public class Crypto {

    private Context context;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PreferenceStorage pStorage;
    private KeyPairGenerator keyGen;
    private KeyPair keyPair;
    private static final int LENGTH_MODULUS = 2048;

    public Crypto(Context context) {
        this.context = context;
        pStorage = new PreferenceStorage(context);

        if (!pStorage.entryExists("prk")) {
            generateCredentials();
        }
    }

    /**
     * method which generates RSA key pairs and stores in preference storage
     */
    private void generateCredentials() {
        try {

            keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(LENGTH_MODULUS);
            keyPair = keyGen.generateKeyPair();

            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();

            pStorage.putBytes("prk", privateKey.getEncoded());
            pStorage.putBytes("pub", publicKey.getEncoded());
            System.out.println(publicKey.getEncoded().length);
            try {
                publicKey =
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
            } catch (InvalidKeySpecException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param input
     * @return
     */
    public String decodeCipher(String input) {

        try {
            privateKey = (PrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(
                            new PKCS8EncodedKeySpec(pStorage.getBytes("prk")));


            byte[] decodedInput = Converter.base64DecodeToBytes(input);

            byte[] decryptedKeyParam = rsaDecrypt(Arrays.copyOfRange(decodedInput, 0, 256), privateKey);

            byte[] encryptedMessage = Arrays.copyOfRange(decodedInput, 256, decodedInput.length);

            /**
             * encryption key = the first 32 bytes and IV the last 16 bytes
             */

            byte[] keyBytes = Arrays.copyOfRange(decryptedKeyParam, 0, 32);
            byte[] ivBytes = Arrays.copyOfRange(decryptedKeyParam, 32, 48);

            Key key;
            Cipher out;

            key = new SecretKeySpec(keyBytes, "AES");

            out = Cipher.getInstance("AES/GCM/NoPadding", "BC");


            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));

            byte[] dec = out.doFinal(encryptedMessage);

            return new String(new String(dec));

        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] AES_GCM_Encrypt(byte[] keyParam, byte[] ivParam, byte[] msg) {
        Key key;
        Cipher in;

        key = new SecretKeySpec(keyParam, "AES");

        try {

            in = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            in.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivParam));

            byte[] enc = in.doFinal(msg);

            return enc;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] AES_GCM_Decrypt(byte[] keyParam, byte[] ivParam, byte[] encryptedMsg) {

        Key key;
        Cipher out;

        key = new SecretKeySpec(keyParam, "AES");

        try {
            out = Cipher.getInstance("AES/GCM/NoPadding", "BC");

            out.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivParam));

            byte[] dec = out.doFinal(encryptedMsg);

            return dec;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * a dummy server side encryption method
     *
     * @param msg :
     * @return
     */
    public String testServer(String msg) {

        try {

             publicKey =
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pStorage.getBytes("pub")));

            String x = Converter.base64Encode((rsaEncrypt("This is fun".getBytes(), publicKey)));

            SecureRandom secureRandom = new SecureRandom();
            byte[] randomKey = new byte[32];
            byte[] randomIv = new byte[16];

            secureRandom.nextBytes(randomIv);
            secureRandom.nextBytes(randomKey);

            byte[] encryptedMsg = AES_GCM_Encrypt(randomKey, randomIv, msg.getBytes());

            byte[] keyParams = Converter.concatArray(randomKey, randomIv);

            // RSA encryption
            byte[] encryptedKeyParam = rsaEncrypt(keyParams, publicKey);

            byte[] encryptedPacket = Converter.concatArray(encryptedKeyParam, encryptedMsg);

           //  Log.d(" encrypted packet length  = ", encryptedKeyParam.length + "  + " + encryptedMsg.length);

            return Converter.base64Encode(encryptedPacket);


        } catch (InvalidKeySpecException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;

    }

    public String getPublicKey() {
       return pStorage.getString("pub");
    }

    public String encode(String input) {
        return null;
    }

    public String decode(String input) {
        return null;
    }

    /**
     * RSA encrypt function (RSA / ECB / PKCS1-Padding)
     *
     * @param msg
     * @param key
     * @return
     */
    private static byte[] rsaEncrypt(byte[] msg, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(msg);
        } catch (Exception e) {
            // Logger.e(e.toString());
        }
        return null;
    }

    /**
     * RSA decrypt function (RSA / ECB / PKCS1-Padding)
     *
     * @param encrypted
     * @param key
     * @return
     */
    private static byte[] rsaDecrypt(byte[] encrypted, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            // Logger.e(e.toString());
        }
        return null;
    }

    public static final String sha256Hash(byte[] msg) {
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashed = sha256.digest(msg);
            return Converter.getHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
}
