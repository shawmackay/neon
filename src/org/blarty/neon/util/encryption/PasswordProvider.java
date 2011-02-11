package org.jini.projects.neon.util.encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 
 * This class provisions classes that allow the retrieval of a password string.
 * This works using the standard Java Spi mechanism i.e. "/META-INF/services/org.jini.projects.neon.PasswordProvider" which
 * should contain a single line with the fully qualified classname of the provisioning class.
 * and allows overrides via System property <code>org.jini.projects.neon.PasswordProvider</code>
 * If neither an Spi file, or a system property exists, the class will return an instance of HCPasswordProvider
 */

//TODO: Clean-up

public class PasswordProvider {

	static final PasswordProvider PROV = new PasswordProvider();

	private PasswordInstance theInstance = null;

	private PasswordProvider() {

	}

	//TODO: private/public?
	PasswordInstance getPasswordInstance() throws PasswordException {
		if (theInstance == null) {
			theInstance = loadPasswordInstance();
		}
		return theInstance;

	}

	private PasswordInstance loadPasswordInstance() throws PasswordException {
		//TODO: Replace with the Factory Utils?
		Class piClass = null;
		URL resource =
			this.getClass().getResource(
				"/META-INF/services/org.jini.projects.neon.PasswordProvider");
		try {
			BufferedReader reader =
				new BufferedReader(
					new InputStreamReader(resource.openStream()));
			String piClassName = reader.readLine();
			piClass = Class.forName(piClassName);
		} catch (IOException e) {
			throw new PasswordException(e);
		} catch (ClassNotFoundException e) {
			throw new PasswordException(e);
		} catch (NullPointerException e) {
			//TODO: Not sure about this...
			//resource does not exist
			//fall through
		}

		//System property overrides the SPI definition
		String piClassName =
			System.getProperty(
				"org.jini.projects.neon.PasswordProvider");
		if (piClassName != null)
			try {
				piClass = Class.forName(piClassName);
			} catch (ClassNotFoundException e1) {
				throw new PasswordException(e1);
			}
		if (piClass == null)
			piClass = HCPasswordProvider.class;
		try {
			return (PasswordInstance) piClass.newInstance();
		} catch (InstantiationException e2) {
			throw new PasswordException(e2);
		} catch (IllegalAccessException e2) {
			throw new PasswordException(e2);
		}
		//TODO: return new HCPasswordProvider(); ???
	}

	/**
	 * Obtains the password instance object that will allow the retirieval of the password string
	 * @return
	 */
	public static PasswordInstance getInstance() throws PasswordException {
		return PROV.getPasswordInstance();
	}
}
