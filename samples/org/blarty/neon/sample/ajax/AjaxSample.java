package org.jini.projects.neon.sample.ajax;

import org.jini.projects.neon.annotations.Render;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.sample.shop.SimpleDetailsForm;

public interface AjaxSample extends Collaborative{
	@Render(action="index")
	public void renderMain();
	
	@Render(action="display", populate={DisplayRequest.class},cacheable=false, contentType="text/xml")
    public void displayDetails(DisplayRequest item);
}
