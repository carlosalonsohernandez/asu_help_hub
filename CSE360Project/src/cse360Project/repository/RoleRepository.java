package cse360Project.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cse360Project.model.Role;

public class RoleRepository {

    private Connection connection;

    public RoleRepository(Connection connection) {
        this.connection = connection;
    }

    // Get role ID by role name --- READ
    public int getRoleIdByName(String roleName) throws SQLException {
        String query = "SELECT id FROM roles WHERE role_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Role not found: " + roleName);
                }
            }
        }
    }
    
    // helper function to see if a role exists
    public boolean doesRoleExist(String roleName) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM roles WHERE role_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0; // Return true if the count is greater than 0
                }
            }
        }
        return false; // Return false if no result was found
    }
    

    // Assign a role to a user --- UPDATE
    public void assignRoleToUser(int userId, int roleId) throws SQLException {
        String insertUserRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserRoleSql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }
    
    public void assignRoleToUser(int userId, String roleName) throws SQLException {
        String insertUserRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        if(!doesRoleExist(roleName)) {
        	insertRole(roleName);
        }
        
        int roleId = getRoleIdByName(roleName);
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserRoleSql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }

    // Remove a role from a user --- UPDATE
    public void removeRoleFromUser(int userId, int roleId) throws SQLException {
        String deleteUserRoleSql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserRoleSql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }
    
    public void removeRoleFromUser(int userId, String roleName) throws SQLException {
        String deleteUserRoleSql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        int roleId = getRoleIdByName(roleName);
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserRoleSql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }


    // Get all roles for user --- READ
    public List<Role> getRolesForUser(int userId) throws SQLException {
        String query = "SELECT r.id, r.role_name FROM roles r " +
                       "JOIN user_roles ur ON r.id = ur.role_id " +
                       "WHERE ur.user_id = ?";
        List<Role> roles = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    int roleId = resultSet.getInt("id");
                    String roleName = resultSet.getString("role_name");
                    roles.add(new Role(roleId, roleName)); // Using the Role(int id, String roleName) constructor
                }
            }
        }
        
        return roles;
    }

    // Add a new role to the roles table --- CREATE
    public void insertRole(String roleName) throws SQLException {
        String query = "INSERT INTO roles (role_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleName);
            pstmt.executeUpdate();
        }
    }

    // Delete a role by role name --- DELETE
    public void deleteRole(String roleName) throws SQLException {
        String query = "DELETE FROM roles WHERE role_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, roleName);
            pstmt.executeUpdate();
        }
    }

    // Update an existing role name --- UPDATE
    public void updateRoleName(int roleId, String newRoleName) throws SQLException {
        String query = "UPDATE roles SET role_name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newRoleName);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }
    
    public List<String> getRoleNamesByInvitationCode(String inviteCode) throws SQLException {
        List<String> roleNames = new ArrayList<>();
        String query = "SELECT r.role_name FROM roles r "
                     + "JOIN invitation_roles ir ON r.id = ir.role_id "
                     + "WHERE ir.invite_code = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    roleNames.add(rs.getString("role_name"));
                }
            }
        }
        return roleNames;
    }
}