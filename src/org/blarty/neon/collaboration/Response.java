package org.jini.projects.neon.collaboration;

import java.io.Serializable;

/**
 *Represents a response from a message call
 */
public class Response implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 3257852086325949239L;
    private Object objResponse;

	public void setResponseObject(Object objResponse) {
		this.objResponse = objResponse;
	}

	public Object getResponseObject() {
		return objResponse;
	}
}
