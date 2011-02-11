package org.jini.projects.neon.sample.ajax;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import org.jini.projects.neon.annotations.Agent;
import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.web.WebUtils;

@Agent(init = "initme",name="AjaxSample", namespace="neon.web")

public class AjaxSampleAgent implements PresentableAgent, AjaxSample{
	
	
	private ArrayList<DisplayableItem> items = new ArrayList<DisplayableItem>();
	
	
	public boolean initme(){
		items.add( new DisplayableItem("Widget A is the best A type widget there is!", "WDGTA", "Widget A",12,19.99));
		items.add( new DisplayableItem("Gadget 1 is the cheapest type of gadget on the market", "GDGT1", "Gadget 1",120,9.99));
	
		return true;
	
	}
	@Override
	public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate)
			throws RemoteException {
		try {
			if(action.equals("index"))
				return new EngineInstruction("xsl",getClass().getResource("index.xsl"), WebUtils.convertObjectToXML("items", items));
			if(action.equals("display")){
				String id = ((String[]) params.get("id"))[0];
				System.out.println("Looking for item: " + id);
				for(DisplayableItem d: items){
					if(d.getIdentifier().equals(id))
						return new EngineInstruction("xsl",getClass().getResource("display.xsl"), WebUtils.convertObjectToXML("item",d));
				}			
			}
		} catch (BadFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[]args){
		new AjaxSampleAgent().initme();
	}
	
	
	@Override
	public URL getTemplate(String type, String action) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void displayDetails(DisplayRequest item) {
	}
	@Override
	public void renderMain() {
		// TODO Auto-generated method stub
		
	}
}
