package cse360Project;
import java.io.UnsupportedEncodingException;
import java.sql.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import java.sql.ResultSet;

/*******
 * <p> DatabaseHelper Class </p>
 * 
 * <p> Description: An database utility class which helps with various controls surrounding the database. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */
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
			insertDefaultRoles(); // Insert the default roles if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	public Connection getConnection() {
		return this.connection;
	}
	
	private void createTables() throws SQLException {
	    // User table
	    String userTable = "CREATE TABLE IF NOT EXISTS users ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "email VARCHAR(255) UNIQUE, "
	            + "username VARCHAR(255) UNIQUE NOT NULL, "
	            + "firstName VARCHAR(255), "
	            + "middleName VARCHAR(255), "
	            + "lastName VARCHAR(255), "
	            + "preferredName VARCHAR(255), "
	            + "hashedPassword VARCHAR(255) NOT NULL, "
	            + "randSalt VARCHAR(255) NOT NULL, "
	            + "otp VARCHAR(10), "
	            + "otp_expiration TIMESTAMP)";
	    statement.execute(userTable);

	    // Roles table
	    String rolesTable = "CREATE TABLE IF NOT EXISTS roles ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "role_name VARCHAR(20) UNIQUE NOT NULL)";
	    statement.execute(rolesTable);

	    // Invitations table
	    String invitationsTable = "CREATE TABLE IF NOT EXISTS invitations ("
	            + "invite_code VARCHAR(255) PRIMARY KEY, "
	            + "used BOOLEAN DEFAULT false)";
	    statement.execute(invitationsTable);

	    // Invitation roles relationship table
	    String invitationsRolesTable = "CREATE TABLE IF NOT EXISTS invitation_roles ("
	            + "invite_code VARCHAR(255), "
	            + "role_id INT, "
	            + "FOREIGN KEY (invite_code) REFERENCES invitations(invite_code) ON DELETE CASCADE, "
	            + "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE, "
	            + "PRIMARY KEY (invite_code, role_id))";
	    statement.execute(invitationsRolesTable);

	    // User roles table
	    String userRolesTable = "CREATE TABLE IF NOT EXISTS user_roles ("
	            + "user_id INT NOT NULL, "
	            + "role_id INT NOT NULL, "
	            + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE, "
	            + "PRIMARY KEY (user_id, role_id))";
	    statement.execute(userRolesTable);

	    // HelpArticles table
	    String helpArticlesTable = "CREATE TABLE IF NOT EXISTS help_articles ("
	            + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
	            + "header VARCHAR(255) NOT NULL, "
	            + "level VARCHAR(50) NOT NULL, "
	            + "title VARCHAR(255) NOT NULL, "
	            + "short_description TEXT NOT NULL, "
	            + "keywords TEXT, "  // Stores a comma-separated list of keywords
	            + "body TEXT NOT NULL, "
	            + "links TEXT, " // Comma-separated list of URLs or link identifiers
	            + "group_identifiers TEXT, "  // Comma-separated list of group IDs
	            + "is_sensitive BOOLEAN DEFAULT false, "  // Flag indicating if article has restricted access
	            + "safe_title VARCHAR(255), "  // Title without sensitive information
	            + "safe_description TEXT "  // Description without sensitive information
	            + ")";
	    statement.execute(helpArticlesTable);

	    // Create help_article_groups table to associate articles with groups
	    String helpArticleGroupsTable = "CREATE TABLE IF NOT EXISTS help_article_groups ("
	            + "article_id BIGINT NOT NULL, "
	            + "group_name VARCHAR(100) NOT NULL, "
	            + "FOREIGN KEY (article_id) REFERENCES help_articles(id) ON DELETE CASCADE, "
	            + "PRIMARY KEY (article_id, group_name))";
	    statement.execute(helpArticleGroupsTable);

	    // Keywords table
	    String keywordsTable = "CREATE TABLE IF NOT EXISTS keywords ("
	            + "keyword_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
	            + "keyword VARCHAR(255) UNIQUE NOT NULL)";
	    statement.execute(keywordsTable);

	    // ArticleKeywords table
	    String articleKeywordsTable = "CREATE TABLE IF NOT EXISTS article_keywords ("
	            + "article_id BIGINT, "
	            + "keyword_id BIGINT, "
	            + "FOREIGN KEY (article_id) REFERENCES help_articles(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (keyword_id) REFERENCES keywords(keyword_id) ON DELETE CASCADE, "
	            + "PRIMARY KEY (article_id, keyword_id))";
	    statement.execute(articleKeywordsTable);

	    // Backups table
	    String backupsTable = "CREATE TABLE IF NOT EXISTS backups ("
	            + "backup_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
	            + "file_name VARCHAR(255) NOT NULL, "
	            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "user_id INT, "
	            + "FOREIGN KEY (user_id) REFERENCES users(id))";
	    statement.execute(backupsTable);
	}
	// insert default roles into roles table if not already there 
	private void insertDefaultRoles() throws SQLException {
	    // Insert Admin role if not exists
	    String insertAdmin = "INSERT INTO roles (role_name) "
	            + "SELECT * FROM (SELECT 'admin') AS tmp "
	            + "WHERE NOT EXISTS (SELECT role_name FROM roles WHERE role_name = 'admin') LIMIT 1";
	    statement.execute(insertAdmin);

	    // Insert Student role if not exists
	    String insertStudent = "INSERT INTO roles (role_name) "
	            + "SELECT * FROM (SELECT 'student') AS tmp "
	            + "WHERE NOT EXISTS (SELECT role_name FROM roles WHERE role_name = 'student') LIMIT 1";
	    statement.execute(insertStudent);

	    // Insert Instructor role if not exists
	    String insertInstructor = "INSERT INTO roles (role_name) "
	            + "SELECT * FROM (SELECT 'instructor') AS tmp "
	            + "WHERE NOT EXISTS (SELECT role_name FROM roles WHERE role_name = 'instructor') LIMIT 1";
	    statement.execute(insertInstructor);
	}

	// KEEP _--------------___-------_--_--_-_----_-_---
	// Method to validate an invitation code and store the roles in the session
	

	// Method to delete the tables from the database
	public void dropTables() throws SQLException {
	    String dropQuery;

	    // Drop tables with foreign key dependencies first
	    dropQuery = "DROP TABLE IF EXISTS article_keywords";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS article_groups";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS user_roles";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS invitation_roles";
	    statement.executeUpdate(dropQuery);

	    // Drop independent tables next
	    dropQuery = "DROP TABLE IF EXISTS backups";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS invitations";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS help_articles";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS groups";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS keywords";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS users";
	    statement.executeUpdate(dropQuery);

	    dropQuery = "DROP TABLE IF EXISTS roles";
	    statement.executeUpdate(dropQuery);

	    System.out.println("All tables have been dropped.");
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

	// Command line display Users. Using for debug purposes.
	public void displayUsersByUser() throws UnsupportedEncodingException, Exception {
	    String sql = "SELECT users.id, users.email, users.username, users.hashedPassword, users.randSalt, "
	               + "GROUP_CONCAT(roles.role_name SEPARATOR ', ') AS roles, users.firstName "
	               + "FROM users "
	               + "LEFT JOIN user_roles ON users.id = user_roles.user_id "
	               + "LEFT JOIN roles ON user_roles.role_id = roles.id "
	               + "GROUP BY users.id"; 

	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery(sql); 

	    while (rs.next()) { 
	        int id = rs.getInt("id"); 
	        String email = rs.getString("email"); 
	        String username = rs.getString("username");
	        String password = rs.getString("hashedPassword"); 
	        String randSalt = rs.getString("randSalt");  
	        String roles = rs.getString("roles"); 
	        String firstName = rs.getString("firstName");

	        // Display values 
	        System.out.println("ID: " + id); 
	        System.out.println("Username: " + username); 
	        System.out.println("Email: " + email); 
	        System.out.println("Hashed Password: " + Base64.getDecoder().decode(password)); 
	        System.out.println("Rand Salt: " + Base64.getDecoder().decode(randSalt));
	        System.out.println("Roles: " + roles); 
	        System.out.println("First Name: " + firstName); 
	        System.out.println("----------------------------------------"); // Separator for clarity
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