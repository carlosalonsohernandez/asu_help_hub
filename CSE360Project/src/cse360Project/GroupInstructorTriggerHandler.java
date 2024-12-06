package cse360Project;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupInstructorTriggerHandler implements Trigger {

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        long groupId = (long) newRow[0]; // Assuming group_id is the first column
        int instructorId = (int) newRow[1]; // Assuming instructor_id is the second column

        // Count instructors for this group
        String countQuery = "SELECT COUNT(*) FROM group_instructors WHERE group_id = ?";
        try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
            countStmt.setLong(1, groupId);
            try (ResultSet rs = countStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 1) {
                    // If it's the first instructor, update their rights
                    String updateQuery = "UPDATE group_instructors "
                            + "SET has_admin_rights = TRUE, has_view_rights = TRUE "
                            + "WHERE group_id = ? AND instructor_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setLong(1, groupId);
                        updateStmt.setInt(2, instructorId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

}