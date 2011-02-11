
/*
* Copyright 2005 neon.jini.org project 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*       http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

/*
 * neon : org.jini.projects.neon.agents.util
 * InvalidViewer.java
 * Created on 19-Apr-2005
 *InvalidViewer
 */
package org.jini.projects.neon.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;

import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.transformers.XMLTransformer;

/**
 * Outputs the contents of a message stored in an invalid message store, in XML format.
 * @author calum
 */
public class InvalidViewer {
	public static void main(String[] args){		
		if(args.length==0)
			new InvalidViewer(null);
		else
			new InvalidViewer(args[0]);
	}
	
	public InvalidViewer(String filename){
		if(filename==null){
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
				filename = chooser.getSelectedFile().getAbsolutePath();
			else
				System.exit(0);
		}
		File f = new File(filename);
		if(!f.exists()){
			System.out.println("File does not exist - exiting....\n");
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
			Object obj = ois.readObject();
			ois.close();
			if(obj instanceof Message){
				XMLTransformer trans = new XMLTransformer();
				System.out.println(trans.transform((Message) obj));
			}
		} catch (FileNotFoundException e) {
			// TODO Handle FileNotFoundException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Handle ClassNotFoundException
			e.printStackTrace();
		}
	}
	
	static void showHelp(){
		System.out.println("InvalidViewer [filename]");		
	}
}
