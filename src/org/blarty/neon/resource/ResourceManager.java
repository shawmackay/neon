package org.jini.projects.neon.resource;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.VirtualFileSystem;

public class ResourceManager {
	private FileSystemManager fsManager;
	private VirtualFileSystem vfs;
	private FileObject root;

	private static ResourceManager rm;

	public static ResourceManager getResourceManager() {
		if (rm == null)
			rm = new ResourceManager();
		return rm;
	}

	private ResourceManager() {
		try {
			fsManager = VFS.getManager();
			vfs = (VirtualFileSystem) fsManager.createVirtualFileSystem("vfs://").getFileSystem();
			FileSystemOptions options = vfs.getFileSystemOptions();
			System.out.println(options);
			root = vfs.getRoot();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createMountPoint(String mountpoint, String path) {
		try {
			vfs.addJunction(mountpoint, vfs.getFileSystemManager().resolveFile(path));

		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileObject getFile(String path) {
		try {

			return root.resolveFile(path);
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
