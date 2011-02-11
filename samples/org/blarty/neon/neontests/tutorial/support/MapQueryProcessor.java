/*
* Copyright 2005 neon.jini.org project 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*       http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/


/*
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.support
 * QueryProcessor.java Created on 13-Oct-2003
 */
package org.jini.projects.neon.neontests.tutorial.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author calum
 */
public class MapQueryProcessor implements JDBCProcessor {
    public static final long serialVersionUID = -8688353038678786870L;
	transient Connection conn;
	StringBuffer procResults;
	/**
	 *  
	 */
	public MapQueryProcessor() {
        
		super();
		// URGENT Complete constructor stub for QueryProcessor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor#setConnection(java.sql.Connection)
	 */
	public void setConnection(Connection conn) {
		// URGENT Complete method stub for setConnection
        System.out.println("Connection Set");
		this.conn = conn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor#execute()
	 */
	public void execute() {        
		try {
           // procResults = new StringBuffer(256);
           
            System.out.println("Adding & Executing now.....");
			// URGENT Complete method stub for execute
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from mier ");
			buildResultsHeader(rs.getMetaData());
            int i=1;
			while (rs.next())
				buildRow(rs,i++);
			buildResultsFooter();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// URGENT Handle SQLException
			e.printStackTrace();
		}

	}

	private void buildResultsHeader(ResultSetMetaData meta) throws SQLException {
		procResults = new StringBuffer(256);
        
		procResults.append("<html><body><table border=1width=100%><font size=-2><tr>");
        
        procResults.append("<th><font color=\"red\">Row</font></th>");
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			procResults.append("<th><font color=\"red\">" + meta.getColumnName(i) + "</font></th>");
		}
		procResults.append("</font></tr>");
	}

	private void buildRow(ResultSet rs, int rownum) throws SQLException {
		procResults.append("<tr>");
        
        procResults.append("<td><font color=\"blue\">"+ rownum + "</font></td>");
		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			procResults.append("<td>" + rs.getObject(i).toString() + "</td>");
		}
		procResults.append("</tr>");
	}

	private void buildResultsFooter() {
		procResults.append("</font></table></body></html>");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor#getResults()
	 */
	public Object getResults() {
		// URGENT Complete method stub for getResults
		return procResults.toString();
	}

}
