package org.jini.projects.neon.util.encryption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * A Password Instance that loads a password from a file where the password is stored in cleartext.
 * The file is stored in the system property <code>neon.passwordfile</code>

 */
public class ClearTextPasswordProvider implements PasswordInstance {

	public char[] getPassword() throws PasswordException {
		String passwordFile =
			System.getProperty(
				"neon.passwordfile");
		if (passwordFile == null)
			throw new PasswordException("Password file property (neon.passwordfile) not set!");
		File pf = new File(passwordFile);
		if (!pf.exists())
			throw new PasswordException(
				"Password file " + passwordFile + " does not exist!");
		try {
			BufferedReader reader =
				new BufferedReader(new FileReader(passwordFile));
			String password = reader.readLine();
			reader.close();
			return password.toCharArray();
		} catch (FileNotFoundException e) {
			throw new PasswordException(
				"Password file " + passwordFile + " does not exist!", e);

		} catch (IOException e) {
			throw new PasswordException(
				"Error reading password file: " + passwordFile, e);

		}

	}

}
