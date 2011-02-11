package org.jini.projects.neon.render;

import java.io.Serializable;
import java.net.URL;

/**
 * Class passed back by agents implementing the <code>PresentableAgent</code> interface, representing the data
 * and associated templte for generating the final output.
 * @see PresentableAgent
 * @author calum
 *
 */
public class EngineInstruction implements Serializable{
	private String engineFormat;
	private Object data;
	private URL template;
	
	
	public EngineInstruction(String engineFormat,URL template, Object data) {
		super();
		this.engineFormat = engineFormat;
		this.data = data;
		this.template = template;
	}
	public String getEngineFormat() {
		return engineFormat;
	}
	public void setEngineFormat(String engineFormat) {
		this.engineFormat = engineFormat;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public URL getTemplate() {
		return template;
	}
	public void setTemplate(URL template) {
		this.template = template;
	}
	
}
