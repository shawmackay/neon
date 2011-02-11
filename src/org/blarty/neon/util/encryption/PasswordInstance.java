
package org.jini.projects.neon.util.encryption;


/**
 * 
 * This interface enables the retrieval of a password string 
 *
 */
public interface PasswordInstance {

	/**
	 * Returns the password for encrypting/decrypting of an object
	 * @return the password string as a character array
	 * @throws PasswordException if there is an error when retrieving the password string
	 */

	public char[] getPassword() throws PasswordException;
}
