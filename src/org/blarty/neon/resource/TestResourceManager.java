package org.jini.projects.neon.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

public class TestResourceManager {
	public static void main(String[] args){
		ResourceManager rm  = ResourceManager.getResourceManager();
		rm.createMountPoint("/tmp", "file://c:/temp");
		rm.createMountPoint("/home", "file://c:/Users/calum");
		rm.createMountPoint("/var/file", "res:org/jini/projects/neon/resource/package.html");
		FileObject newFile = rm.getFile("/home/vfs.txt");
		try {
			newFile.createFile();
			BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(newFile.getContent().getOutputStream()));
			bos.write("Hello from VFS!");
			bos.flush();
			bos.close();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileObject readFile = rm.getFile("/var/file");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(readFile.getContent().getInputStream()));
			String line = reader.readLine();
			while(line!=null){
				System.out.println(line +"\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
