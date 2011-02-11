
package org.jini.projects.neon.util.encryption;


/**
 * 
 * A simple Password provider where the password is hardcoded into the class, but prints out a message when the password is requested
 */
public class DebugHCPasswordProvider implements PasswordInstance {


	public char[] getPassword() throws PasswordException {
		System.out.println(this.getClass().getName() + " called for password");
		return "mypassword".toCharArray();
	}

}
