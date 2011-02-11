
package org.jini.projects.neon.util.encryption;


/**
 * 
 * A simple Password provider where the password is hardcoded into the class
 */
public class HCPasswordProvider implements PasswordInstance {

	public char[] getPassword() throws PasswordException {
		// TODO What is this for??
		return "mypassword".toCharArray();
	}

}
