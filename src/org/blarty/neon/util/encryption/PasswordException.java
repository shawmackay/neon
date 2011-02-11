
package org.jini.projects.neon.util.encryption;

/**
 * Exception thrown when the password cannot be retrieved
 */
public class PasswordException extends Exception {
	/**
	 * 
	 */
	public PasswordException() {
		super();

	}

	public PasswordException(String message) {
		super(message);

	}

	public PasswordException(Throwable cause) {
		super(cause);

	}

	public PasswordException(String message, Throwable cause) {
		super(message, cause);

	}

}
