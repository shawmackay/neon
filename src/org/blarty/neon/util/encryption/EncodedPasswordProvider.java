package org.jini.projects.neon.util.encryption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//TODO: MDS Style
/**
 * A Password Instance that loads a password from a file where the password is stored in Base64 encrypted form.
 * The file is stored in the system property <code>neon.passwordfile</code>
 */
public class EncodedPasswordProvider implements PasswordInstance {

	
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
			BASE64Decoder decoder = new BASE64Decoder();
			return new String(decoder.decodeBuffer(password)).toCharArray();
		} catch (FileNotFoundException e) {
			throw new PasswordException(
				"Password file " + passwordFile + " does not exist!", e);

		} catch (IOException e) {
			throw new PasswordException(
				"Error reading password file: " + passwordFile, e);

		}
	}

}
