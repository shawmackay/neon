

package org.jini.projects.neon.util.encryption;

/**
 * A String Implementation of the Encrypted Object for Dealing with Encrypting Strings
 *
 */
public class StringEncryptedObject extends EncryptedObject {

	/**
	 * A method to encrypt the given String
	 *
	 * @param toEncrypt The String to encrypt
	 */
	public StringEncryptedObject(String toEncrypt) throws EncryptionException {
		super(toEncrypt);
	}

	/**
	 * A method to encrypt the given String and set the Masker to use.
	 *
	 * @param toEncrypt The String to encrypt
	 * @param masker    The Masker to use
	 */
	public StringEncryptedObject(String toEncrypt, Masker masker) throws EncryptionException {
		super(toEncrypt, masker);
	}

	/**
	 * Returns the Decrypted String
	 *
	 * @return
	 */
	public String getDecryptedString() throws EncryptionException {
		return (String) getDecryptedObject();
	}
}
