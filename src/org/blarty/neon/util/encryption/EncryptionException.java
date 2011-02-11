
package org.jini.projects.neon.util.encryption;


/**
 * 
 * EncryptionExcpetion is thrown whenever there is an issue with Encryption
 
 *
 */
public class EncryptionException extends Exception {

	
	public EncryptionException() {
		super();
	}

	
	public EncryptionException(String message) {
		super(message);
	}

	
	public EncryptionException(Throwable cause) {
		super(cause);
	}

	
	public EncryptionException(String message, Throwable cause) {
		super(message, cause);
	}

}
