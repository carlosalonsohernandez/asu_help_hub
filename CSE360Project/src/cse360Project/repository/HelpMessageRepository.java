package cse360Project.repository;

import cse360Project.model.HelpMessage;
import cse360Project.model.MessageType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HelpMessageRepository {
	public Connection connection;

    public HelpMessageRepository(Connection connection) {
        this.connection = connection;
    }

    // Insert a new help message
    public void insertHelpMessage(HelpMessage helpMessage) throws SQLException {
        String query = "INSERT INTO help_messages (user_id, message_type, content, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, helpMessage.getUserId());
            stmt.setString(2, helpMessage.getMessageType().name());
            stmt.setString(3, helpMessage.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(helpMessage.getTimestamp()));
            stmt.executeUpdate();
        }
    }

    // Retrieve all help messages
    public List<HelpMessage> getAllHelpMessages() throws SQLException {
        List<HelpMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM help_messages ORDER BY timestamp DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HelpMessage msg = new HelpMessage(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        MessageType.valueOf(rs.getString("message_type")),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                messages.add(msg);
            }
        }
        return messages;
    }

    // Retrieve help messages by user ID
    public List<HelpMessage> getHelpMessagesByUserId(int userId) throws SQLException {
        List<HelpMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM help_messages WHERE user_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HelpMessage msg = new HelpMessage(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            MessageType.valueOf(rs.getString("message_type")),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    messages.add(msg);
                }
            }
        }
        return messages;
    }
    
    public void deleteHelpMessage(int id) throws SQLException {
        String query = "DELETE FROM help_messages WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Additional methods as needed (e.g., delete, update)
}