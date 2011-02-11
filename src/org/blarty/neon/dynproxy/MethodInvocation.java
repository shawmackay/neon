package org.jini.projects.neon.dynproxy;

import java.io.Serializable;

import org.jini.projects.neon.agents.Meta;

public class MethodInvocation implements Serializable {
	private String name;
	private String methodsignature;
	private Object[] params;
	private Meta invocationMetaData;
	public MethodInvocation(Meta invocationMetaData, String methodsignature, String name, Object[] params) {
		super();
		this.invocationMetaData = invocationMetaData;
		this.methodsignature = methodsignature;
		this.name = name;
		this.params = params;
	}
	public String getName() {
		return name;
	}
	public String getMethodsignature() {
		return methodsignature;
	}
	public Object[] getParams() {
		return params;
	}
	public Meta getInvocationMetaData() {
		return invocationMetaData;
	}
	
	
}
