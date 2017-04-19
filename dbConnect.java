package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbConnect {
	private Connection conn = null;
	
	public void dbConnect(String host, String username, String password, String db) {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+db, username, password);
			System.out.println("Establishing connection to the mysql server...");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error connecting to DB: " + e.getMessage().toString());
		}  
		System.out.println("Connection to database established successfully!");
	 }
	 
	 public ResultSet execute(String query){
		 Statement stmt = null;
		 ResultSet result = null;
		 
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		try {
			result = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	 }
	 
	 public void destroy() throws SQLException {
		 conn.close();
	 }
}
