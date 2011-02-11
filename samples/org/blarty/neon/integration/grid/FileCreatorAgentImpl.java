package org.jini.projects.neon.integration.grid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.TransactionalAgent;

@Exportable
public class FileCreatorAgentImpl extends AbstractAgent implements FileCreatorAgent, TransactionalAgent{
	private File f;
	
	public FileCreatorAgentImpl() {
	this.name = "FileCreator";
	this.namespace = "neon.samples";
	}
	
	private String message;
	
	/* (non-Javadoc)
	 * @see org.jini.projects.neon.integration.grid.FileCreatorAgent#setMessage(java.lang.String)
	 */
	public void setMessage(String message) throws RemoteException{
		this.message = message;
	}
	
	public void abort() {
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(f));
			fw.append("Abort occured: Mesg:" + message);
			fw.newLine();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void commit() {
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(f));
			fw.append("Commit occured: Mesg:" + message);
			fw.newLine();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean prepare() {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean init() {
		f = new File(System.getProperty("user.home") + File.separatorChar + "FileCreator.txt");
		return true;
	}
	

}
