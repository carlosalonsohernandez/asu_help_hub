package cse360Project.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialAccessGroupRepository {
    private final Connection connection;

    public SpecialAccessGroupRepository(Connection connection) {
        this.connection = connection;
    }

    public void createGroup(String groupName, String description) throws SQLException {
        String sql = "INSERT INTO special_access_groups (group_name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupName);
            stmt.setString(2, description);
            stmt.executeUpdate();
        }
    }

    public List<String> getAllGroups() throws SQLException {
        String sql = "SELECT group_name FROM special_access_groups";
        List<String> groups = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                groups.add(rs.getString("group_name"));
            }
        }
        return groups;
    }
    
    public void addArticle(long groupId, String encryptedBody) throws SQLException {
        String sql = "INSERT INTO group_articles (group_id, encrypted_body) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            stmt.setString(2, encryptedBody);
            stmt.executeUpdate();
        }
    }

    public List<String> getArticlesByGroupId(long groupId) throws SQLException {
        String sql = "SELECT encrypted_body FROM group_articles WHERE group_id = ?";
        List<String> articles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(rs.getString("encrypted_body"));
                }
            }
        }
        return articles;
    }
    
    public void addInstructor(long groupId, int instructorId, boolean hasViewRights, boolean hasAdminRights) throws SQLException {
        String sql = "INSERT INTO group_instructors (group_id, instructor_id, has_view_rights, has_admin_rights) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            stmt.setInt(2, instructorId);
            stmt.setBoolean(3, hasViewRights);
            stmt.setBoolean(4, hasAdminRights);
            stmt.executeUpdate();
        }
    }
}