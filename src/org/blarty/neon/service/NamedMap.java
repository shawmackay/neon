/**
 * 
 */
package org.jini.projects.neon.service;

import java.util.HashMap;

public class NamedMap extends HashMap{
	private String name;

	NamedMap(String name) {
		super();
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	
}