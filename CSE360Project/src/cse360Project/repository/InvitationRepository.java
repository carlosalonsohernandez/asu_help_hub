package cse360Project.repository;

import java.sql.*;
import java.util.List;

import cse360Project.model.Invitation;

public class InvitationRepository {

    private Connection connection;

    public InvitationRepository(Connection connection) {
        this.connection = connection;
    }

    // Insert a new invitation into the invitations table
    public void insertInvitation(Invitation invitation) throws SQLException {
        String query = "INSERT INTO invitations (invite_code, used) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, invitation.getInviteCode());
            pstmt.setBoolean(2, invitation.isUsed());
            pstmt.executeUpdate();
        }
    }

    // Retrieve an invitation by invite code
    public Invitation getInvitationByCode(String inviteCode) throws SQLException {
        String query = "SELECT invite_code, used FROM invitations WHERE invite_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invitation invitation = new Invitation(rs.getString("invite_code"), rs.getBoolean("used"));
                    return invitation;
                } else {
                    return null;
                }
            }
        }
    }
    
    
    // Store an invite
	public void storeInvite(String inviteCode, List<String> roles) throws SQLException {
	    // Insert invitation code into the invitations table (no role_id here)
	    String insertInviteQuery = "INSERT INTO invitations (invite_code, used) VALUES (?, false)";
	    
	    try (PreparedStatement insertInviteStmt = connection.prepareStatement(insertInviteQuery)) {
	        insertInviteStmt.setString(1, inviteCode);
	        insertInviteStmt.executeUpdate();
	    }

	    // Insert each role associated with the invitation into the invitation_roles table
	    String getRoleIdQuery = "SELECT id FROM roles WHERE role_name = ?";
	    String insertRoleQuery = "INSERT INTO invitation_roles (invite_code, role_id) VALUES (?, ?)";

	    for (String role : roles) {
	        int roleId;

	        // Get the role ID for each role
	        try (PreparedStatement getRoleIdStmt = connection.prepareStatement(getRoleIdQuery)) {
	            getRoleIdStmt.setString(1, role);
	            try (ResultSet rs = getRoleIdStmt.executeQuery()) {
	                if (rs.next()) {
	                    roleId = rs.getInt("id");
	                } else {
	                    throw new SQLException("Role not found: " + role);
	                }
	            }
	        }

	        // Insert the invite code and role ID into the invitation_roles table
	        try (PreparedStatement insertRoleStmt = connection.prepareStatement(insertRoleQuery)) {
	            insertRoleStmt.setString(1, inviteCode);
	            insertRoleStmt.setInt(2, roleId);
	            insertRoleStmt.executeUpdate();
	        }
	    }
	}

    // Update the status of an invitation (e.g., mark as used)
    public void updateInvitationStatus(String inviteCode, boolean used) throws SQLException {
        String query = "UPDATE invitations SET used = ? WHERE invite_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, used);
            pstmt.setString(2, inviteCode);
            pstmt.executeUpdate();
        }
    }

    // Delete an invitation
    public void deleteInvitation(String inviteCode) throws SQLException {
        String query = "DELETE FROM invitations WHERE invite_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            pstmt.executeUpdate();
        }
    }
    
    public void markAsUsed(String inviteCode) throws SQLException {
        String query = "UPDATE invitations SET used = true WHERE invite_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            pstmt.executeUpdate();
        }
    }
}