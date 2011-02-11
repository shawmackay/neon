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
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial
 * JDBCAgentImpl.java
 * Created on 13-Oct-2003
 */
package org.jini.projects.neon.neontests.tutorial.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.jini.config.ConfigurationException;

import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.TransactionalAgent;
import org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor;

/**
 * @author calum
 */

@Injection
public class JDBCAgentImpl extends AbstractAgent implements JDBCAgent, TransactionalAgent {
    private String dburl;

    private String password;

    private String user;

    private String drivername;

    private transient Connection theConnection;

    // private Logger dbAgentLog =
    // Logger.getLogger("neon.org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.JDBCAgent");
    /**
     * 
     */
    
    
    public JDBCAgentImpl() {
        super();
       this.name="Jdbc";
       this.namespace="example";
        // URGENT Complete constructor stub for JDBCAgentImpl
    }

    /*
     * (non-Javadoc)
     * 
     * /** Creates the connection to the database
     */
    public boolean init() {
        try {
            net.jini.config.Configuration config = getAgentConfiguration();
           
            Class.forName(drivername);
        } catch (ClassNotFoundException e) {
            // URGENT Handle ClassNotFoundException
            e.printStackTrace();
            return false;
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            theConnection = DriverManager.getConnection(dburl, user, password);
        } catch (SQLException e1) {
            // URGENT Handle SQLException
            e1.printStackTrace();
            return false;
        }
        getAgentLogger().finer("Connection to database made");
        return true;
    }

    public JDBCProcessor InvokeJDBCProcessor(JDBCProcessor processor) {
        processor.setConnection(theConnection);
        processor.execute();
        // System.out.println("Resultsback: " + processor.getResults());
        return processor;
    }

    /*
     * @see org.jini.projects.neon.host.transactions.Transactional#abort()
     */
    public void abort() {
        try {
            // TODO Complete method stub for abort
            theConnection.rollback();
        } catch (SQLException e) {
            // URGENT Handle SQLException
            e.printStackTrace();
        }     
    }

    /*
     * @see org.jini.projects.neon.host.transactions.Transactional#commit()
     */
    public void commit() {
        // TODO Complete method stub for commit

        try {
            theConnection.commit();
        } catch (SQLException e) {
            // URGENT Handle SQLException
            e.printStackTrace();
        }        
    }

    /*
     * @see org.jini.projects.neon.host.transactions.Transactional#prepare()
     */
    public boolean prepare() {
        // TODO Complete method stub for prepare

        return false;
    }

	public String getDburl() {
		return dburl;
	}

	public void setDburl(String dburl) {
		this.dburl = dburl;
		System.out.println("JDBCAgent connecting to: " + dburl);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}


}
