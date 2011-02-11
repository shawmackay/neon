/**
 * 
 */
package org.jini.projects.neon.web;

public class XmlCombinerLocation{
	private String nodeName;
	private String xml;
        private boolean useNodeNameAsParent = false;
	public XmlCombinerLocation(String nodeName, String xml) {
		super();
		this.nodeName = nodeName;
		this.xml = xml;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	} 
        
        public void setNodeNameAsParent(boolean useAsParent){
            this.useNodeNameAsParent = useAsParent;
        }
        
        public boolean isNodeNameAsParent(){
            return useNodeNameAsParent;
        }
                    
        
}