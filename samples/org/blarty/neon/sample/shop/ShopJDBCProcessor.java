package org.jini.projects.neon.sample.shop;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor;

public class ShopJDBCProcessor implements JDBCProcessor {

	Connection conn;

	private List myList = new ArrayList();

	public ShopJDBCProcessor() {

	}


	public void execute() {
		// TODO Auto-generated method stub
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from items");
			while (rs.next())
				myList.add(new AvailableItemsDescription(rs.getString("code"), rs.getString("description"), rs.getDouble("unitprice"), rs.getInt("available")));
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Object getResults() {
		// TODO Auto-generated method stub
		return myList;
	}


	public void setConnection(Connection conn) {
		// TODO Auto-generated method stub
		this.conn = conn;
	}

}
