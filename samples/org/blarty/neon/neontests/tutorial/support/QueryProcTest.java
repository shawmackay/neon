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
 * QueryProcTest.java
 * Created on 13-Oct-2003
 */
package org.jini.projects.neon.neontests.tutorial.support;

import java.sql.Connection;
import java.sql.DriverManager;



/**
 * @author calum
 */
public class QueryProcTest {

	/**
	 * 
	 */
	public QueryProcTest() {
		super();
		// URGENT Complete constructor stub for QueryProcTest
	}
	public static void main(String[] args) {
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@nts4_005:1521:SSDT", "chrisl","chrisl");
			HTMLQueryProcessor p = new HTMLQueryProcessor();
			p.setConnection(conn);
			p.execute();
			System.out.println("Results: \n\n" + p.getResults());
		}catch (Exception ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
