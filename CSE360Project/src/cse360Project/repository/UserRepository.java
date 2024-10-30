package cse360Project.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import cse360Project.Session;
import cse360Project.model.User; // Import your User class
import cse360Project.model.UserAccountInfo;

public class UserRepository {
	
	private Connection connection;

	public UserRepository(Connection connection) {
		this.connection = connection;
	}

	// Insert user by taking a User object
	public void insertUser(User user) throws SQLException {
	    String query = "INSERT INTO users (username, hashedPassword, randSalt) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, user.getUsername());
	        pstmt.setString(2, user.getHashedPassword());
	        pstmt.setString(3, user.getRandSalt());
	        pstmt.executeUpdate();
	        
	        // Retrieve the generated user ID
	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                user.setId(generatedKeys.getInt(1));
	            } else {
	                throw new SQLException("User insertion failed, no ID obtained.");
	            }
	        }
	    }
	}
	
	// Delete user by User object
	public void deleteUserAccount(User user) throws SQLException {
	    String query = "DELETE FROM users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getUsername());
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("User account deleted successfully: " + user.getUsername());
	        } else {
	            System.out.println("User not found: " + user.getUsername());
	        }
	    }
	}
	
	// Delete user by username
	public void deleteUserAccount(String username) {
	    String deleteUserSql = "DELETE FROM users WHERE username = ?";

	    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteUserSql)) {
	        deleteStmt.setString(1, username);
	        int rowsAffected = deleteStmt.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("User account deleted successfully: " + username);
	        } else {
	            System.out.println("User not found: " + username);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Update user password using a hashed password and randSalt
	public void updateUserPassword(String username, byte[] hashedPassword, byte[] randSalt) throws SQLException {
	    String query = "UPDATE users SET hashedPassword = ?, randSalt = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        // Use the provided hashed password and randSalt
	        pstmt.setString(1, Base64.getEncoder().encodeToString(hashedPassword));
	        pstmt.setString(2, Base64.getEncoder().encodeToString(randSalt));
	        pstmt.setString(3, username);
	        int affected = pstmt.executeUpdate();

	        System.out.println(affected + " rows affected by update pass");
	    }
	    System.out.println("Password updated successfully for user: " + username);
	}
	
	// Update user profile to complete using id
	public boolean updateUserById(int userId, String email, String firstName, String middleName, String lastName, String preferredName) throws SQLException {
	    String selectQuery = "SELECT * FROM users WHERE id = ?";
	    
	    // Query to update user details
	    String updateQuery = "UPDATE users SET email = ?, firstName = ?, middleName = ?, lastName = ?, preferredName = ? WHERE id = ?";
	    
	    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
	        selectStmt.setInt(1, userId);
	        
	        try (ResultSet rs = selectStmt.executeQuery()) {
	            if (rs.next()) {
	                // User exists, proceed with updating their details
	                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	                    updateStmt.setString(1, email);
	                    updateStmt.setString(2, firstName);
	                    
	                    // Set middle name, handling potential null
	                    if (middleName != null) {
	                        updateStmt.setString(3, middleName);
	                    } else {
	                        updateStmt.setNull(3, java.sql.Types.VARCHAR);
	                    }
	                    
	                    updateStmt.setString(4, lastName);
	                    
	                    // Set preferred name, handling potential null
	                    if (preferredName != null) {
	                        updateStmt.setString(5, preferredName);
	                    } else {
	                        updateStmt.setNull(5, java.sql.Types.VARCHAR);
	                    }
	                    
	                    updateStmt.setInt(6, userId);
	                    
	                    int rowsUpdated = updateStmt.executeUpdate();
	                    
	                    // Return true if update was successful
	                    Session.getInstance().setCurrentUser(getUserByUsername(Session.getInstance().getCurrentUser().getUsername()));
	                    return rowsUpdated > 0;
	                }
	            } else {
	                // User with the given ID doesn't exist
	                System.out.println("User with ID " + userId + " not found.");
	                return false;
	            }
	        }
	    }
	}
	
	// Fetch user by username, and return User object
	public User getUserByUsername(String username) throws SQLException {
	    String query = "SELECT * FROM users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            return new User(
	                rs.getInt("id"), 
	                rs.getString("email"),
	                rs.getString("username"), 
	                rs.getString("firstName"),
	                rs.getString("middleName"),
	                rs.getString("lastName"),
	                rs.getString("preferredName"),
	                rs.getString("hashedPassword"), 
	                rs.getString("randSalt"),
	                rs.getString("otp"),
	                rs.getTimestamp("otp_expiration")
	            );
	        } else {
	            return null; // or throw an exception
	        }
	    }
	}
	
	// get user id by username
    public Integer getUserIdByUsername(String username) throws SQLException {
        String selectUserSql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectUserSql)) {
            selectStmt.setString(1, username);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null; // User not found
        }
    }

    // update the users password and otp
    public void updateUserPasswordAndOtp(int userId, String hashedPassword, String randSalt, String otp, Timestamp expirationTime) throws SQLException {
        String updatePasswordSql = "UPDATE users SET hashedPassword = ?, randSalt = ?, otp = ?, otp_expiration = ? WHERE id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updatePasswordSql)) {
            updateStmt.setString(1, hashedPassword);
            updateStmt.setString(2, randSalt);
            updateStmt.setString(3, otp);
            updateStmt.setTimestamp(4, expirationTime);
            updateStmt.setInt(5, userId);
            int affected = updateStmt.executeUpdate();
            System.out.println(affected + " rows affected!!");
        }
    }
	
    // Method to retrieve roles for a user
    public List<String> getUserRoles(int userId) throws SQLException {
        String getUserRoles = "SELECT r.role_name FROM roles r "
                + "JOIN user_roles ur ON r.id = ur.role_id "
                + "WHERE ur.user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(getUserRoles)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<String> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(rs.getString("role_name"));
                }
                return roles;
            }
        }
    }
    

    public void addUserRole(int userId, int roleId) throws SQLException {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }

    public void removeUserRole(int userId, int roleId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }
    
    // fetch user based on sql result row
    public User fetchUser(ResultSet rs) throws SQLException {
        // Retrieve user details from ResultSet
        int userId = rs.getInt("id");
        String email = rs.getString("email");
        String username = rs.getString("username");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String preferredName = rs.getString("preferredName");
        String hashedPassword = rs.getString("hashedPassword");
        String randSalt = rs.getString("randSalt");
        String otp = rs.getString("otp");
        Timestamp otpExpiration = rs.getTimestamp("otpExpiration");
        
        // Create and return a User object
        return new User(userId, email, username, email, firstName, lastName, preferredName, hashedPassword, randSalt, otp, otpExpiration);
    }
    
	public void clearOtp(String username) throws SQLException {
	    String clearOtpSql = "UPDATE users SET otp = NULL, otp_expiration = NULL WHERE username = ?";
	    try (PreparedStatement clearOtpStmt = connection.prepareStatement(clearOtpSql)) {
	        clearOtpStmt.setString(1, username);
	        clearOtpStmt.executeUpdate();
	    }
	}
	
	// fetch a list of a list of user details for a table
    public List<List<String>> getUsersForTable() throws SQLException {
        String sql = "SELECT users.username, CONCAT(users.firstName, ' ', users.lastName) AS name, GROUP_CONCAT(roles.role_name) AS roles " +
                     "FROM users " +
                     "LEFT JOIN user_roles ON users.id = user_roles.user_id " +
                     "LEFT JOIN roles ON user_roles.role_id = roles.id " +
                     "GROUP BY users.id";

        List<List<String>> userDetailsList = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String name = rs.getString("name");
                String roles = rs.getString("roles");

                // Create a list of user details
                List<String> userDetails = new ArrayList<>();
                userDetails.add(username);
                userDetails.add(name);
                userDetails.add(roles);

                // Add the user details list to the main list
                userDetailsList.add(userDetails);
            }
        }
        return userDetailsList; // Return the complete list of user details
    }

    // return a list of useraccountinfo records based off all users in db
    public List<UserAccountInfo> getUserAccounts() throws SQLException {
        String sql = "SELECT users.id, users.username, users.firstName, roles.role_name " +
                     "FROM users " +
                     "LEFT JOIN user_roles ON users.id = user_roles.user_id " +
                     "LEFT JOIN roles ON user_roles.role_id = roles.id";

        List<UserAccountInfo> userAccounts = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String firstName = rs.getString("firstName");
                String role = rs.getString("role_name");

                userAccounts.add(new UserAccountInfo(id, username, firstName, role));
            }
        }
        return userAccounts; // Return the complete list of user accounts
    }
}