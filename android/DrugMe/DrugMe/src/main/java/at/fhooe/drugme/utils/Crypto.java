package at.fhooe.drugme.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import android.content.Context;

/**
 * 
 * @author s1210455002
 * 
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
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx "  + publicKey.getFormat());
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
	 * 
	 * @param xx
	 * @return
	 */
	public String decodeCipher(String input) {

		try {
				privateKey = (PrivateKey) KeyFactory.getInstance("RSA")
					.generatePrivate(
							new PKCS8EncodedKeySpec(pStorage.getBytes("prk")));
				
				
				
				byte[] decodedInput = Converter.base64DecodeToBytes(input);
				
				byte[] decrypted = rsaDecrypt(decodedInput, privateKey);
				
				return new String(decrypted);
				
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 */
	public void credential() {
		try {
			PublicKey publicKey = 
				    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pStorage.getBytes("pub")));
			
			String x = Converter.base64Encode((rsaEncrypt("This is fun".getBytes(), publicKey)));
			
			String y = decodeCipher(x);
			
			System.out.println(" encoded : " + x);

			System.out.println(" decoded : "
					+ y);
			
			
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
	}

	public String getPublicKey() {
		return null;
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
