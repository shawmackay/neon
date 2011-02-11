

package org.jini.projects.neon.util.encryption;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This method is used to encrypt an object and provides various methods for encrypting, decrypting and masking the object.
 *
*/
public class EncryptedObject implements Serializable {

	private static int WRAP_OBJECT = 0;
	private static int WRAP_INT = 1;
	private static int WRAP_DOUBLE = 2;
	private static int WRAP_CHAR = 3;
	private static int WRAP_BYTE = 4;
	private static int WRAP_FLOAT = 5;
	private static int WRAP_SHORT = 6;
	private static int WRAP_LONG = 7;

	private int type;

	private static int ITERATIONS = 100;

	private SealedObject encrypted;

	private Masker theMasker;
	
	private static Logger logger = Logger.getLogger("neon.encryption");

	/**
	 * A constructor which encrypts the given Serilizable Object
	 *
	 * @param in The Serilizable Object to encrypt
	 */
	public EncryptedObject(Serializable in) throws EncryptionException {
		this(in, StarredMasker.getInstance());
	}

	/**
	 * A constructor which encrypts the given Serilizable Object and sets the Masker to use.
	 *
	 * @param in     The Serilizable Object to encrypt
	 * @param masker The Masker Class to use
	 */
	public EncryptedObject(Serializable in, Masker masker) throws EncryptionException {
		try {
			encrypt(in);
		} catch (Exception e) {
			throw new EncryptionException("Object encryption failed!", e);

		}
	}
	private Object decrypt()
		throws
			InvalidKeyException,
			NoSuchAlgorithmException,
			IOException,
			ClassNotFoundException,
			InvalidKeySpecException,
			PasswordException {

		PasswordInstance password = PasswordProvider.getInstance();
		PBEKeySpec keySpec;

		keySpec = new PBEKeySpec(password.getPassword());
		SecretKeyFactory keyFactory =
			SecretKeyFactory.getInstance("PBEWithMD5AndDES");

		SecretKey key = keyFactory.generateSecret(keySpec);

		return encrypted.getObject(key);
	}

	private void encrypt(Serializable o)
		throws
			InvalidKeySpecException,
			NoSuchAlgorithmException,
			NoSuchPaddingException,
			InvalidKeyException,
			InvalidAlgorithmParameterException,
			IllegalBlockSizeException,
			IOException,
			PasswordException {
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);

		PasswordInstance password = PasswordProvider.getInstance();
		PBEKeySpec keySpec;

		keySpec = new PBEKeySpec(password.getPassword());

		SecretKeyFactory keyFactory =
			SecretKeyFactory.getInstance("PBEWithMD5AndDES");

		SecretKey key = keyFactory.generateSecret(keySpec);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		encrypted = new SealedObject(o, cipher);

	}

	/**
	 * Returns the Encrypted Object
	 *
	 * @return The Encrypted Object
	 */
	public Object getObject() {
		return encrypted;
	}

	/**
	 * Returns the Decrypted Object
	 *
	 * @return THe Decrypted Object
	 */
	public Object getDecryptedObject() throws EncryptionException {
		try {
			return decrypt();
		} catch (InvalidKeyException e) {
			throw new EncryptionException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e);
		} catch (InvalidKeySpecException e) {
			throw new EncryptionException(e);
		} catch (IOException e) {
			throw new EncryptionException(e);
		} catch (ClassNotFoundException e) {
			throw new EncryptionException(e);
		} catch (PasswordException e) {
			throw new EncryptionException(e);
		}
	}

	/**
	 * Returns a Masked Version of the Object
	 *
	 * @return A Masked Version of the Object
	 */
	public String getMaskedObject() {
		return theMasker.mask(encrypted);
	}

	public String toString() {
		return getMaskedObject();
	}

	/**
	 * Sets the Masker on the current Encrypted Object
	 *
	 * @param masker The Masker to use
	 */
	public void setMasker(Masker masker) {
		theMasker = masker;
	}

}
