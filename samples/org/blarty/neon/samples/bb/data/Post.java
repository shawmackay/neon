package org.jini.projects.neon.samples.bb.data;

import java.util.Date;

public interface Post {
	public Date getPostedDate();
	public String getContent();
	public String getSubject();
	public String getPoster();
	public String getIdentifier();
}
