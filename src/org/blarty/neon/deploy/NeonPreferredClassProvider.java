/*
 * neon : org.jini.projects.neon.deploy
 * 
 * 
 * NeonPreferredClassProvider.java
 * Created on 19-Jul-2005
 * 
 * NeonPreferredClassProvider
 *
 */

package org.jini.projects.neon.deploy;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

import net.jini.loader.pref.PreferredClassProvider;

/**
 * @author calum
 */
public class NeonPreferredClassProvider extends PreferredClassProvider {
	
	
	
	public NeonPreferredClassProvider() {	
		super();		
	}

	public NeonPreferredClassProvider(boolean requireDlPerm) {
		super(requireDlPerm);
		//System.out.println("Created Neon Preferred Class Provider w/ reqdDlPerm Constructor");
	}

	
	protected ClassLoader createClassLoader(final URL[] urls, final ClassLoader parent, final boolean requireDlPerm) {
		// TODO Complete method stub for createClassLoader
		StringBuffer origExportAnnotationBuffer = new StringBuffer();

		NarLoader deployer = new NarLoader();
		ArrayList<URL> newURLList = new ArrayList<URL>();
		for (URL url : urls) {
			origExportAnnotationBuffer.append(url.toExternalForm() + " ");
//			if (url.toExternalForm().endsWith(".hjar")) {			
//				URL[] expandedList = deployer.deployNar(url);
//				for (URL expanded : expandedList)
//					newURLList.add(expanded);
//			} else
				newURLList.add(url);
		}
	
		final URL[] expandedurls = newURLList.toArray(new URL[]{});
		final String originalExportAnnotation = origExportAnnotationBuffer.toString().trim();

		return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new NeonPreferredClassLoader(expandedurls, parent, originalExportAnnotation, requireDlPerm);
			}
		}, NeonPreferredClassLoader.getLoaderAccessControlContext(urls));
	}

	

}
