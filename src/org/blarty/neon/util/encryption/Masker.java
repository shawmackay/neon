

package org.jini.projects.neon.util.encryption;

import java.io.Serializable;

/**
 * A Masker interface, for returning a Masked Version of the given object.
 *
 */
public interface Masker extends Serializable {

	public String mask(Serializable in);
}
