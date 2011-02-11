/*
 * neon : org.jini.projects.neon.deploy
 * 
 * 
 * NeonPreferredClassLoader.java
 * Created on 19-Jul-2005
 * 
 * NeonPreferredClassLoader
 *
 */

package org.jini.projects.neon.deploy;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

import net.jini.loader.pref.PreferredClassLoader;

/**
 * @author calum
 */
public class NeonPreferredClassLoader extends PreferredClassLoader {	
	public NeonPreferredClassLoader(URL[] urls, ClassLoader parent, String exportAnnotation, boolean requireDownloadPerm) {
		super(urls, parent,exportAnnotation,requireDownloadPerm);		
        //System.out.println("Created new NeonPreferredClassLoader: original Export Annotation: " + exportAnnotation);
	}
	
	
	static void addPermissionsForURLs(URL[] urls, PermissionCollection perms, boolean forLoader) {
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			try {
				URLConnection urlConnection = url.openConnection();
				Permission p = urlConnection.getPermission();
				if (p != null) {
					if (p instanceof FilePermission) {
						/*
						 * If the codebase is a file, the permission required to
						 * actually read classes from the codebase URL is the
						 * permission to read all files beneath the last
						 * directory in the file path, either because JAR files
						 * can refer to other JAR files in the same directory,
						 * or because permission to read a directory is not
						 * implied by permission to read the contents of a
						 * directory, which all that might be granted.
						 */
						String path = p.getName();
						int endIndex = path.lastIndexOf(File.separatorChar);
						if (endIndex != -1) {
							path = path.substring(0, endIndex + 1);
							if (path.endsWith(File.separator)) {
								path += "-";
							}
							Permission p2 = new FilePermission(path, "read");
							if (!perms.implies(p2)) {
								perms.add(p2);
							}
						} else {
							/*
							 * No directory separator: use permission to read
							 * the file.
							 */
							if (!perms.implies(p)) {
								perms.add(p);
							}
						}
					} else {
						if (!perms.implies(p)) {
							perms.add(p);
						}

						/*
						 * If the purpose of these permissions is to grant them
						 * to an instance of a URLClassLoader subclass, we must
						 * add permission to connect to and accept from the host
						 * of non-"file:" URLs, otherwise the getPermissions()
						 * method of URLClassLoader will throw a security
						 * exception.
						 */
						if (forLoader) {
							// get URL with meaningful host component
							URL hostURL = url;
							for (URLConnection conn = urlConnection; conn instanceof JarURLConnection;) {
								hostURL = ((JarURLConnection) conn).getJarFileURL();
								conn = hostURL.openConnection();
							}
							String host = hostURL.getHost();
							if (host != null && p.implies(new SocketPermission(host, "resolve"))) {
								Permission p2 = new SocketPermission(host, "connect,accept");
								if (!perms.implies(p2)) {
									perms.add(p2);
								}
							}
						}
					}
				}
			} catch (IOException e) {
				/*
				 * This shouldn't happen, although it is declared to be thrown
				 * by openConnection() and getPermission(). If it does, don't
				 * bother granting or requiring any permissions for this URL.
				 */
			}

		}
	}

	static AccessControlContext getLoaderAccessControlContext(URL[] urls) {
		/*
		 * The approach used here is taken from the similar method
		 * getAccessControlContext() in the sun.applet.AppletPanel class.
		 */
		// begin with permissions granted to all code in current policy
		PermissionCollection perms = (PermissionCollection) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				CodeSource codesource = new CodeSource(null, (Certificate[]) null);
				Policy p = java.security.Policy.getPolicy();
				if (p != null) {
					return p.getPermissions(codesource);
				} else {
					return new Permissions();
				}
			}
		});

		// createClassLoader permission needed to create loader in context
		perms.add(new RuntimePermission("createClassLoader"));

		// add permissions to read any "java.*" property
		perms.add(new java.util.PropertyPermission("java.*", "read"));

		// add permissions required to load from codebase URL path
		addPermissionsForURLs(urls, perms, true);

		/*
		 * Create an AccessControlContext that consists of a single protection
		 * domain with only the permissions calculated above.
		 */
		ProtectionDomain pd = new ProtectionDomain(new CodeSource((urls.length > 0 ? urls[0] : null), (Certificate[]) null), perms);
		return new AccessControlContext(new ProtectionDomain[]{pd});
	}
	
	
}
