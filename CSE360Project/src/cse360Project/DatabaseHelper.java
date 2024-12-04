package cse360Project;
import java.io.UnsupportedEncodingException;
import java.sql.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
			displayHelpArticles();
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
	
	private void dropTables() throws SQLException {
	    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
	         Statement statement = connection.createStatement()) {

	        // Query to retrieve all tables in the database
	        String fetchTablesQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'";
	        List<String> tablesToDrop = new ArrayList<>();

	        try (ResultSet resultSet = statement.executeQuery(fetchTablesQuery)) {
	            while (resultSet.next()) {
	                tablesToDrop.add(resultSet.getString("TABLE_NAME"));
	            }
	        }

	        System.out.println("Tables found in the database: " + tablesToDrop);

	        // Drop each table in the list
	        for (String table : tablesToDrop) {
	            try {
	                System.out.println("Dropping table: " + table);
	                statement.executeUpdate("DROP TABLE IF EXISTS " + table + " CASCADE");
	            } catch (SQLException e) {
	                System.err.println("Error dropping table " + table + ": " + e.getMessage());
	            }
	        }

	        System.out.println("All tables have been dropped successfully.");
	    } catch (SQLException e) {
	        System.err.println("Error while dropping all tables: " + e.getMessage());
	        throw e; // Rethrow exception for critical issues
	    }
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
	
	
	public void displayHelpArticles() throws SQLException {
	    String sql = "SELECT id, header, level, title, short_description, keywords, "
	               + "body, links, group_identifiers, is_sensitive, safe_title, safe_description "
	               + "FROM help_articles";

	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);

	    while (rs.next()) {
	        long id = rs.getLong("id");
	        String header = rs.getString("header");
	        String level = rs.getString("level");
	        String title = rs.getString("title");
	        String shortDescription = rs.getString("short_description");
	        String keywords = rs.getString("keywords");
	        String body = rs.getString("body");
	        String links = rs.getString("links");
	        String groupIdentifiers = rs.getString("group_identifiers");
	        boolean isSensitive = rs.getBoolean("is_sensitive");
	        String safeTitle = rs.getString("safe_title");
	        String safeDescription = rs.getString("safe_description");

	        // Display values
	        System.out.println("ID: " + id);
	        System.out.println("Header: " + header);
	        System.out.println("Level: " + level);
	        System.out.println("Title: " + title);
	        System.out.println("Short Description: " + shortDescription);
	        System.out.println("Keywords: " + keywords);
	        System.out.println("Body: " + body);
	        System.out.println("Links: " + links);
	        System.out.println("Group Identifiers: " + groupIdentifiers);
	        System.out.println("Is Sensitive: " + isSensitive);
	        System.out.println("Safe Title: " + safeTitle);
	        System.out.println("Safe Description: " + safeDescription);
	        System.out.println("----------------------------------------"); // Separator for clarity
	    }

	    // Clean up
	    rs.close();
	    stmt.close();
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