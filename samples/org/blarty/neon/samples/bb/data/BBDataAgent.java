package org.jini.projects.neon.samples.bb.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.web.WebUtils;

public class BBDataAgent extends AbstractAgent {
	public String dataFolder = System.getProperty("user.home") + "/.neonbb";
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		File f = new File(dataFolder);
		if(!f.exists())
			f.mkdirs();
		return true;
	}
	
	public boolean writePost(Post p){
		try {
			StringBuffer myBuffer = new StringBuffer(WebUtils.convertObjectToXML("post", p));
			File f = new File(dataFolder+"/post"+p.getIdentifier());
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.append(myBuffer);
			writer.flush();
			writer.close();
		} catch (BadFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String args[]){
		DefaultPost p = new DefaultPost();
		p.setSubject("Hello");
		p.setPostedDate(new Date());
		p.setPoster("Calum");
		p.setContent("Test message");
		p.setIdentifier("One");
	
		BBDataAgent ag = new BBDataAgent();
		ag.init();
		ag.writePost(p);
		Post newPost = ag.readPost("One");
		System.out.println("Poster was: " + newPost.getPoster());
	}
	
	public Post readPost(String id){
		File f = new File(dataFolder+"/post"+id);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			StringBuffer buffer = new StringBuffer();
			String line = reader.readLine();
			while(line!=null){
				buffer.append(line);
				line = reader.readLine();
			}
			return (Post) WebUtils.convertXMLToObject(buffer.toString(),"post", DefaultPost.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ThreadSummary getSummary(int topicID){
		return null;
	}

}
