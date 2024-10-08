package cse360Project;
import java.io.UnsupportedEncodingException;
import java.sql.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.sql.ResultSet;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase;AUTO_SERVER=TRUE";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		// Updated user table creation
		String userTable = "CREATE TABLE IF NOT EXISTS users ("
		        + "id INT AUTO_INCREMENT PRIMARY KEY, "
		        + "email VARCHAR(255) UNIQUE, "
		        + "username VARCHAR(255) UNIQUE NOT NULL, "
		        + "oneTimeFlag BOOLEAN, " 
		        + "firstName VARCHAR(255), "
		        + "middleName VARCHAR(255), "  
		        + "lastName VARCHAR(255), "
		        + "preferredName VARCHAR(255), " 
		        + "role VARCHAR(20) NOT NULL, "
		        + "hashedPassword VARCHAR(255) NOT NULL, "
		        + "randSalt VARCHAR(255) NOT NULL)";
		statement.execute(userTable);
		
		String topicsTable = "CREATE TABLE IF NOT EXISTS user_topics ("
		        + "user_id INT NOT NULL, "
		        + "topic_name VARCHAR(255) NOT NULL, "
		        + "proficiency_level VARCHAR(20) DEFAULT 'intermediate', "
		        + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " // Optional
		        + "PRIMARY KEY (user_id, topic_name))";
		statement.execute(topicsTable);
	}
	
	// Method to delete the tables from the database
	public void dropTables() throws SQLException {
	    String dropQuery = "DROP TABLE IF EXISTS user_topics";
	    
	    statement.executeUpdate(dropQuery);
	    
	    dropQuery = "DROP TABLE IF EXISTS users";
	    
	    // Execute the drop table query
	    statement.executeUpdate(dropQuery);
	    
	    dropQuery = "DROP TABLE IF EXISTS cse360users";
	    
	    // Execute the drop table query
	    statement.executeUpdate(dropQuery);
	    
	    System.out.println("Tables have been dropped.");
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	// Method to empty the database by deleting all records from the relevant table(s)
	public void emptyDatabase() throws SQLException {
	    String deleteQuery = "DELETE FROM users";
	    
	    // Execute the delete query
	    statement.executeUpdate(deleteQuery);
	    
	    deleteQuery = "DELETE FROM user_topics";
	    
	    statement.executeUpdate(deleteQuery);
	    
	    System.out.println("Database has been emptied.");
	}

	// TODO: Update this to take into account the user table with more attributes
	public void register(String username, String password, String role) throws Exception {
		var passwd = new Password(password);
		String hashedPassword = Base64.getEncoder().encodeToString(passwd.getHashedPass());
		String randSalt = Base64.getEncoder().encodeToString(passwd.getSalt());
		String insertUser = "INSERT INTO users (username, role, hashedPassword, randSalt) VALUES (?, ?, ?, ?)";

		
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, role);
			pstmt.setString(3, hashedPassword);
			pstmt.setString(4, randSalt);
			pstmt.executeUpdate();
		}
	}
	
	// TODO: Update this to take into account the user table with more attributes
	public void register(String username, String password) throws Exception {
		var passwd = new Password(password);
		String hashedPassword = Base64.getEncoder().encodeToString(passwd.getHashedPass());
		String randSalt = Base64.getEncoder().encodeToString(passwd.getSalt());
		String insertUser = "INSERT INTO users (username, hashedPassword, randSalt) VALUES (?, ?, ?)";

		
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, hashedPassword);
			pstmt.setString(3, randSalt);
			pstmt.executeUpdate();
		}
	}

	public boolean login(String username, String password) throws Exception {
	    String query = "SELECT * FROM users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                // Retrieve the salt and hashed password from the database
	                String hashedPassword = rs.getString("hashedPassword");
	                String randSalt = rs.getString("randSalt");

	                // Use your password verification method to compare
	                boolean isValidPassword = Password.verifyPassword(password, 
	                                            Base64.getDecoder().decode(randSalt), 
	                                            Base64.getDecoder().decode(hashedPassword));

	                if (isValidPassword) {
	                    // Retrieve user details
	                    int userId = rs.getInt("id");
	                    
	                    // Set user session
	                    // Check if you user has set up account
	                    if (rs.getString("firstName") != null) {
	                    	System.out.println("set up!");
	                        Session.getInstance().setUser(userId, username,rs.getString("email"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("preferredName"), rs.getString("role"));
	                    } else {
	                        // Only guaranteed values
	                    	System.out.println("missing set up");
	                        Session.getInstance().setUser(userId, username);	                    }
	                    return true; // Successful login
	                }
	            }
	        }
	    }
	    return false; // Invalid login
	}
	
	public boolean doesUserExist(String email) {
	    String query = "SELECT COUNT(*) FROM users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	public boolean updateUserById(int userId, String username, String firstName, String lastName, String preferredName) throws SQLException {
	    String selectQuery = "SELECT * FROM users WHERE id = ?";
	    
	    // Query to update user details
	    String updateQuery = "UPDATE users SET firstName = ?, lastName = ?, preferredName = ? WHERE id = ?";
	    
	    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
	        selectStmt.setInt(1, userId);
	        
	        try (ResultSet rs = selectStmt.executeQuery()) {
	            if (rs.next()) {
	                // User exists, proceed with updating their details
	                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	                    updateStmt.setString(1, firstName);
	                    updateStmt.setString(2, lastName);
	                    updateStmt.setString(3, preferredName);
	                    updateStmt.setInt(4, userId);
	                    
	                    int rowsUpdated = updateStmt.executeUpdate();
	                    
	                    // Return true if update was successful
	                    return rowsUpdated > 0;
	                }
	            } else {
	                // User with the given ID dosnt exist
	                System.out.println("User with ID " + userId + " not found.");
	                return false;
	            }
	        }
	    }
	}

	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("hashedPassword"); 
			String randSalt = rs.getString("randSalt");  
			String role = rs.getString("role");  


			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Hashed Password: " + password); 
			System.out.print(", Rand Salt: " + randSalt);
			System.out.println(", Role: " + role); 
		} 
	}
	
	public void displayUsersByUser() throws UnsupportedEncodingException, Exception{
		String sql = "SELECT * FROM users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String username = rs.getString("username");
			String password = rs.getString("hashedPassword"); 
			String randSalt = rs.getString("randSalt");  
			String role = rs.getString("role");  
			String firstName = rs.getString("firstName");


			// Display values 
			System.out.print("ID: " + id); 
			System.out.print("username: " + username); 
			System.out.print(", Email: " + email); 
			System.out.println(", Hashed Password: " + Base64.getDecoder().decode(password)); 
			System.out.println(", Rand Salt: " + Base64.getDecoder().decode(randSalt));
			System.out.println(", Role: " + role); 
			System.out.println(", First Name: " + firstName); 

		} 
	}


	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	public void displayAllTablesAndColumns() throws SQLException {
	    DatabaseMetaData metaData = connection.getMetaData();
	    
	    // Get all tables in the current database schema
	    ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});
	    
	    // Loop through each table and get the columns
	    while (tables.next()) {
	        String tableName = tables.getString("TABLE_NAME");
	        System.out.println("Table: " + tableName);
	        
	        // Get all columns of the current table
	        ResultSet columns = metaData.getColumns(null, null, tableName, "%");
	        
	        while (columns.next()) {
	            String columnName = columns.getString("COLUMN_NAME");
	            String columnType = columns.getString("TYPE_NAME");
	            int columnSize = columns.getInt("COLUMN_SIZE");
	            System.out.println("\tColumn: " + columnName + " | Type: " + columnType + " | Size: " + columnSize);
	        }
	        
	        columns.close(); // Close the columns ResultSet
	    }
	    
	    tables.close(); // Close the tables ResultSet
	}

}
