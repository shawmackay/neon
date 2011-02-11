package org.jini.projects.neon.render;

public class RenderResponse {
	private String contentType = "text/html";
	private boolean cacheable = true;
	
	public RenderResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public boolean isCacheable() {
		return cacheable;
	}
	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content.getBytes();
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	private byte[] content;
	public RenderResponse(boolean cacheable, String content, String contentType) {
		super();
		this.cacheable = cacheable;
		this.content = content.getBytes();
		this.contentType = contentType;
	}
	
	public RenderResponse(boolean cacheable ,byte[] content, String contentType) {
		super();
		this.cacheable = cacheable;
		this.content = content;
		this.contentType = contentType;
	}
}
