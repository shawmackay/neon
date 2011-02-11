
package org.jini.projects.neon.util.encryption;

import java.io.Serializable;

/**
 * Returns '********' for the given object.
 *
 */
public class StarredMasker implements Masker {

	private static StarredMasker starredMasker;

	private StarredMasker() {
	}

	public static StarredMasker getInstance() {
		if (starredMasker == null) {
			starredMasker = new StarredMasker();
		}

		return starredMasker;
	}

	private static final String STARS = "********";

	public String mask(Serializable in) {
		return STARS;
	}
}
