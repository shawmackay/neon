package org.jini.projects.neon.util.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jini.projects.neon.users.agents.KeyStoreUtility;

/**
 * A Utility Class for dealing with Encryption.
 * 
 */
public final  class EncryptionUtils {

	
	
//	static {
//		initialise("conf/keystore", "keystore".toCharArray(), "johns", "openup".toCharArray(),);
//	}

	private static KeyStoreUtility ksu;
	private static PrivateKey privKey;
	private static PublicKey pub;

	
	public static void initialise(String keystoreLocation, char[] keystorePassword, String defaultUser, char[] defaultPw, String keystoreType){
		try {
			ksu = new KeyStoreUtility(keystoreType, keystoreLocation, keystorePassword);
			Object[] arr = ksu.recoverPrivateKey(defaultUser, keystorePassword, defaultPw);
			privKey = (PrivateKey) arr[0];
			pub = ksu.recoverPublicKey(defaultUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO: Test

	/**
	 * A method to encrypt the current object into an EncryptedObject
	 * 
	 * @param in
	 *            The Serializable Object to Encrypt
	 * @return The Encrypted Object
	 */
	public static EncryptedObject encryptObject(Serializable in) throws EncryptionException {
		return new EncryptedObject(in);
	}

	/**
	 * A method to encrypt the current object into an EncryptedObject and
	 * Displayed using the given Masker
	 * 
	 * @param in
	 *            The Serializable Objec to Encrypt
	 * @param masker
	 *            The Masker Class to use to display the Encrypted Object
	 * @return The Encrypted Object
	 */
	public static EncryptedObject encryptObject(Serializable in, Masker masker) throws EncryptionException {
		return new EncryptedObject(in, masker);
	}

	/**
	 * Generates a Base64 Encoded digest for a specific input String
	 * 
	 * @param input
	 *            the string to generate a digest for
	 * @return a Base64 Encoded digest format
	 * @throws EncryptionException
	 *             if the encoding or digest algorithms are not known or
	 *             unsupported
	 */
	public static String generateHash(String input) throws EncryptionException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(input.getBytes("UTF-8"));
			byte[] digest = md.digest();
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptionException(e);
		}
	}

	/**
	 * Takes a stored Encoded String and compares it with an unecoded string by
	 * comparing the digests
	 * 
	 * @param input
	 *            the unencoded string to check
	 * @param hashed
	 *            the stored encoded string
	 * @return whether the two strings have the same digest and are hence the
	 *         same
	 * @throws EncryptionException
	 *             if the encoding or digest algorithms are not known or
	 *             unsupported
	 */
	public static boolean compareWithHash(String input, String hashed) throws EncryptionException {
		try {
			// Decode the stored digest
			BASE64Decoder dec = new BASE64Decoder();
			byte[] currentDigest = dec.decodeBuffer(hashed);

			// Setup a digest for the input value
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes("UTF-8"));
			byte[] digest = md.digest();

			if (Arrays.equals(digest, currentDigest)) {
				return true;
			} else {
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptionException(e);
		}
	}

	public static byte[] encryptObjectWithKey(Serializable object, PublicKey pub) throws NoSuchAlgorithmException, NoSuchPaddingException, Exception, BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException,
			InvalidKeyException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream daos = new ObjectOutputStream(baos);

		//System.out.println("Creating a key");

		Cipher rsacipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		//System.out.println("Initilising Cipher");
		rsacipher.init(Cipher.ENCRYPT_MODE, pub);

		KeyGenerator rijndaelKeyGen = KeyGenerator.getInstance("AES");
		rijndaelKeyGen.init(128);
		//System.out.println("Generating Session key....");
		Key rijndaelKey = rijndaelKeyGen.generateKey();
		//System.out.println("Done generating key");

		byte[] encodeKeyBytes = rsacipher.doFinal(rijndaelKey.getEncoded());
		daos.writeInt(encodeKeyBytes.length);
		daos.write(encodeKeyBytes);
		SecureRandom rand = new SecureRandom();
		byte[] iv = new byte[16];
		rand.nextBytes(iv);

		daos.write(iv);
		IvParameterSpec spec = new IvParameterSpec(iv);

		Cipher symmetricCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		symmetricCipher.init(Cipher.ENCRYPT_MODE, rijndaelKey, spec);

		//System.out.println("Encrypting the Object");
		SealedObject so = new SealedObject(object, symmetricCipher);
		daos.writeObject(so);
		daos.flush();
		daos.close();
		return baos.toByteArray();
		// System.out.println("Unencrypting the object");
		// String unecryppted = (String) so.getObject(pub);
		// System.out.println("Credit card number = " + unecryppted);
	}

	public static Object decryptObjectWithDefaultKey(byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, IOException,
			ClassNotFoundException {
		return decryptObjectwithKey(bytes, privKey);
	}

	public static Object decryptObjectwithKey(byte[] bytes, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
		byte[] encryptedKeyBytes = new byte[ois.readInt()];
		ois.readFully(encryptedKeyBytes);
		rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] rijndaelKeyBytes = rsaCipher.doFinal(encryptedKeyBytes);
		SecretKey rijndaelKey = new SecretKeySpec(rijndaelKeyBytes, "AES");
		byte[] iv = new byte[16];
		ois.read(iv);
		IvParameterSpec spec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, rijndaelKey, spec);
		SealedObject so = (SealedObject) ois.readObject();
		//System.out.println("Getting the Object");
		Object ob = so.getObject(cipher);
		return ob;
	}

	public static void initialiseEncryptedVersion(Serializable in, Serializable out) throws Exception {
		initialiseEncryptedVersion(in, true, pub, out);

	}

	public static void initialiseEncryptedVersion(Serializable in, boolean encryptIn, Serializable out) throws Exception {
		initialiseEncryptedVersion(in, encryptIn, pub, out);

	}

	public static void initialiseEncryptedVersion(Serializable in, PublicKey pub, Serializable out) throws Exception {
		initialiseEncryptedVersion(in, true, pub, out);
	}

	public static Object findEncryptedEncryptedVersion(Serializable in, PublicKey pub) throws Exception {

		String name = in.getClass().getName();

		String[] parts = name.split("\\.");
		parts[parts.length - 1] = "Encrypted" + parts[parts.length - 1];
		StringBuffer newName = new StringBuffer();
		for (String part : parts) {
			newName.append(part + ".");
		}
		newName.deleteCharAt(newName.length() - 1);
		String newClassName = new String(newName);
		//System.out.println("Looking for class: " + newClassName);
		Class c = null;
		try {
			c = Class.forName(newClassName);
		} catch (Exception e) {
		}
		if (c == null) {
			System.out.println("Encrypted version of object class not found");
			return in;
		} else {
			try {
				//System.out.println("Loading Encrypted: ");
				Object o = c.newInstance();
				Map m = new TreeMap();
				Field[] encAttributes = c.getFields();
				Field[] normalAttributes = in.getClass().getFields();
				for (Field normalAttribute : normalAttributes) {
					m.put(normalAttribute.getName(), normalAttribute);
				}
				for (Field encAttrib : encAttributes) {
					if (m.containsKey(encAttrib.getName())) {
						Object value = ((Field) m.get(encAttrib.getName())).get(in);
						encAttrib.set(o, value);
					}
				}
				if (o instanceof CryptoHolder) {
					CryptoHolder ch = (CryptoHolder) o;
					ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(in, pub)));
				}
				//System.out.println("Object: " + o);
				return o;
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalBlockSizeException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidAlgorithmParameterException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidKeyException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}
	}

	public static Object findEncryptedEncryptedVersion(Serializable in) throws Exception {

		String name = in.getClass().getName();

		String[] parts = name.split("\\.");
		parts[parts.length - 1] = "Encrypted" + parts[parts.length - 1];
		StringBuffer newName = new StringBuffer();
		for (String part : parts) {
			newName.append(part + ".");
		}
		newName.deleteCharAt(newName.length() - 1);
		String newClassName = new String(newName);
		//System.out.println("Looking for class: " + newClassName);
		Class c = null;
		try {
			c = Class.forName(newClassName);
		} catch (Exception e) {
		}
		if (c == null) {
			System.out.println("Encrypted version of object class not found");
			return in;
		} else {
			try {
			//	System.out.println("Loading Encrypted: ");
				Object o = c.newInstance();
				Map m = new TreeMap();
				Field[] encAttributes = c.getFields();
				Field[] normalAttributes = in.getClass().getFields();
				for (Field normalAttribute : normalAttributes) {
					m.put(normalAttribute.getName(), normalAttribute);
				}
				for (Field encAttrib : encAttributes) {
					if (m.containsKey(encAttrib.getName())) {
						Object value = ((Field) m.get(encAttrib.getName())).get(in);
						encAttrib.set(o, value);
					}
				}
				if (o instanceof CryptoHolder) {
					CryptoHolder ch = (CryptoHolder) o;
					ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(in, pub)));
				}
				//System.out.println("Object: " + o);
				return o;
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalBlockSizeException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidAlgorithmParameterException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidKeyException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}
	}
	
	
	public static Object findEncryptedVersion(Serializable in, Serializable toEncrypt, PublicKey pub) throws Exception {

		String name = in.getClass().getName();

		String[] parts = name.split("\\.");
		parts[parts.length - 1] = "Encrypted" + parts[parts.length - 1];
		StringBuffer newName = new StringBuffer();
		for (String part : parts) {
			newName.append(part + ".");
		}
		newName.deleteCharAt(newName.length() - 1);
		String newClassName = new String(newName);
		//System.out.println("Looking for class: " + newClassName);
		Class c = null;
		try {
			c = Class.forName(newClassName);
		} catch (Exception e) {
		}
		if (c == null) {
			System.out.println("Encrypted version of object class not found");
			return in;
		} else {
			try {
				//System.out.println("Loading Encrypted: ");
				Object o = c.newInstance();
				Map m = new TreeMap();
				Field[] encAttributes = c.getFields();
				Field[] normalAttributes = in.getClass().getFields();
				for (Field normalAttribute : normalAttributes) {
					m.put(normalAttribute.getName(), normalAttribute);
				}
				for (Field encAttrib : encAttributes) {
					if (m.containsKey(encAttrib.getName())) {
						Object value = ((Field) m.get(encAttrib.getName())).get(in);
						encAttrib.set(o, value);
					}
				}
				if (o instanceof CryptoHolder) {
					CryptoHolder ch = (CryptoHolder) o;
					if (toEncrypt != null)
						ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(toEncrypt, pub)));
				}
				//System.out.println("Object: " + o);
				return o;
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalBlockSizeException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidAlgorithmParameterException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidKeyException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}

	}

	public static Object findEncryptedVersion(Serializable in, Serializable toEncrypt) throws Exception {

		String name = in.getClass().getName();

		String[] parts = name.split("\\.");
		parts[parts.length - 1] = "Encrypted" + parts[parts.length - 1];
		StringBuffer newName = new StringBuffer();
		for (String part : parts) {
			newName.append(part + ".");
		}
		newName.deleteCharAt(newName.length() - 1);
		String newClassName = new String(newName);
		//System.out.println("Looking for class: " + newClassName);
		Class c = null;
		try {
			c = Class.forName(newClassName);
		} catch (Exception e) {
		}
		if (c == null) {
			System.out.println("Encrypted version of object class not found");
			return in;
		} else {
			try {
			//	System.out.println("Loading Encrypted: ");
				Object o = c.newInstance();
				Map m = new TreeMap();
				Field[] encAttributes = c.getFields();
				Field[] normalAttributes = in.getClass().getFields();
				for (Field normalAttribute : normalAttributes) {
					m.put(normalAttribute.getName(), normalAttribute);
				}
				for (Field encAttrib : encAttributes) {
					if (m.containsKey(encAttrib.getName())) {
						Object value = ((Field) m.get(encAttrib.getName())).get(in);
						encAttrib.set(o, value);
					}
				}
				if (o instanceof CryptoHolder) {
					CryptoHolder ch = (CryptoHolder) o;
					if (toEncrypt != null)
						ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(toEncrypt, pub)));
				}
				//System.out.println("Object: " + o);
				return o;
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (NoSuchPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadPaddingException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalBlockSizeException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidAlgorithmParameterException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvalidKeyException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
			return null;
		}

	}
	
	public static void initialiseEncryptedVersion(Serializable in, boolean encryptIn, PublicKey pub, Serializable out) throws Exception {

		try {
		//	System.out.println("Scanning Encrypted: ");
			Object o = out;
			Class c = out.getClass();
			Map m = new TreeMap();
			Field[] encAttributes = c.getFields();
			Field[] normalAttributes = in.getClass().getFields();
			for (Field normalAttribute : normalAttributes) {
				m.put(normalAttribute.getName(), normalAttribute);
			}
			for (Field encAttrib : encAttributes) {
				if (m.containsKey(encAttrib.getName())) {
					Object value = ((Field) m.get(encAttrib.getName())).get(in);
					encAttrib.set(o, value);
				}
			}
			if (o instanceof CryptoHolder) {
				CryptoHolder ch = (CryptoHolder) o;
				if (encryptIn)
					ch.setEncryptedObject(new EncryptedHolder(EncryptionUtils.encryptObjectWithKey(in, pub)));
			}
//			System.out.println("Object: " + o);

		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (BadPaddingException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalBlockSizeException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidAlgorithmParameterException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeyException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(EncryptionUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
