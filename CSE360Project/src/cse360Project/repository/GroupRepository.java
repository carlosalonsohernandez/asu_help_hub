package cse360Project.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cse360Project.Session;
import cse360Project.model.HelpArticle;

public class GroupRepository {
	private final Connection connection;
	private final HelpArticleRepository articleRepo;
	
	public GroupRepository(Connection connection, HelpArticleRepository articleRepo) {
		this.connection = connection;
		this.articleRepo = articleRepo;
	}
	
	public void insertGroup(long articleId, String groupIdentifiers) throws SQLException {
        // Now insert into help_article_groups with the generated article_id
        String insertGroupQuery = "INSERT INTO help_article_groups (article_id, group_name, is_special) "
                                + "VALUES (?, ?, ?)";
        try (PreparedStatement pstmtGroup = connection.prepareStatement(insertGroupQuery)) {
            for (String groupIdentifier : groupIdentifiers.split(",")) {
                pstmtGroup.setLong(1, articleId);  // Use the generated article_id
                pstmtGroup.setString(2, groupIdentifier);
                pstmtGroup.executeUpdate();
            }
        }
	}
	
	public List<List<String>> getAllGroups() throws SQLException {
	    List<List<String>> groups = new ArrayList<>();
	    String query = "SELECT DISTINCT group_name FROM help_article_groups";

	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {
	        while (rs.next()) {
	            List<String> group = new ArrayList<>();
	            group.add(rs.getString("group_name")); // Group name
	            groups.add(group);
	        }
	    }

	    return groups;
	}
	
	public void addArticleToGroup(long articleId, String groupName) throws SQLException {
	    // Add the article to the group
	    String insertSql = "INSERT INTO help_article_groups (article_id, group_name) VALUES (?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
	        stmt.setLong(1, articleId);
	        stmt.setString(2, groupName);
	        stmt.executeUpdate();
	    }
	}


    public List<String> getAvailableGroups() {
        Set<String> groupSet = new HashSet<>(); // using set to avoid duplicates at first, convert to List at the end 

        String query = "SELECT group_identifiers FROM help_articles";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String groupIdentifiers = rs.getString("group_identifiers");
                if (groupIdentifiers != null && !groupIdentifiers.trim().isEmpty()) {
                    // Split by comma and add to the set
                    String[] groups = groupIdentifiers.split(",");
                    for (String group : groups) {
                        groupSet.add(group.trim()); // Trim to remove any extra spaces
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convert the Set back to a List and return
        return new ArrayList<>(groupSet);
    }

	public List<HelpArticle> getArticlesByLevels(List<String> levels) {
	    // SQL query to filter articles based on levels
		List<HelpArticle> articles = new ArrayList<>();
	    String query = "SELECT * FROM help_articles WHERE level IN (" +
	                   levels.stream().map(l -> "'" + l + "'").collect(Collectors.joining(", ")) + ")";
	    // Execute the query and return the result
	    try(PreparedStatement stmt = connection.prepareStatement(query);
	    	ResultSet rs = stmt.executeQuery()) {
	    	{
	    	
	    	articles = articleRepo.getArticlesFromResultSet(rs);

	    	}
	    	
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return articles;
	}
	
	// Get all available levels in the DB
	public List<String> getAvailableLevels() {
	    List<String> levels = new ArrayList<>();
	    String query = "SELECT DISTINCT level FROM help_articles";

	    try (PreparedStatement stmt = connection.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {

	        // Iterate through result set and add each level to the list
	        while (rs.next()) {
	            levels.add(rs.getString("level"));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace(); // Log the exception for debugging
	    }

	    return levels;
	}

	public boolean hasSensitiveGroup(String groupIdentifiers) {
		
	    String[] selectedGroupIdentifiers = groupIdentifiers.split(",");

	    // Check if any of the selected groups are already sensitive
	    for (String groupIdentifier : selectedGroupIdentifiers) {
	        String query = "SELECT is_special FROM help_article_groups WHERE group_name = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setString(1, groupIdentifier);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next() && rs.getBoolean("is_special")) {
	            	return true;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();  // Handle exception properly
	        }
	    }
	    return false;
	}



	public void markAsSensitive(String groupIdentifiers, long articleId) {
	    String[] selectedGroupIdentifiers = groupIdentifiers.split(",");


	    // Loop through each group identifier
	    for (String groupIdentifier : selectedGroupIdentifiers) {
	        try {
	        	System.out.println("Working with group identifier: " + groupIdentifier);
	            // First check if the group exists in the help_article_groups table
	            String checkGroupQuery = "SELECT COUNT(*) FROM help_article_groups WHERE group_name = ?";
	            try (PreparedStatement pstmtCheck = connection.prepareStatement(checkGroupQuery)) {
	                pstmtCheck.setString(1, groupIdentifier);
	                ResultSet rs = pstmtCheck.executeQuery();
	                if (rs.next() && rs.getInt(1) == 0) {
	                	System.out.println("Group doesn't exist, trying to insert");
	                    // If the group doesn't exist, insert it as a new group
	                	String insertGroupQuery = "INSERT INTO help_article_groups (article_id, group_name, is_special) VALUES (?, ?, ?)";
	                	try (PreparedStatement pstmtInsert = connection.prepareStatement(insertGroupQuery)) {
	                	    pstmtInsert.setLong(1, articleId); 
	                	    pstmtInsert.setString(2, groupIdentifier);
	                	    pstmtInsert.setBoolean(3, true); // Mark it as sensitive
	                	    pstmtInsert.executeUpdate();
	                	}
	                }
	            }

	            // Now that the group exists, mark it as sensitive (if not already)
	            if (!hasSensitiveGroup(groupIdentifier)) {
	                String updateQuery = "UPDATE help_article_groups SET is_special = true WHERE group_name = ?";
	                try (PreparedStatement pstmtUpdate = connection.prepareStatement(updateQuery)) {
	                    pstmtUpdate.setString(1, groupIdentifier);
	                    pstmtUpdate.executeUpdate();
	                }
	            }

	            // Grant the current user admin rights (CRUD) for this sensitive group
	            String insertAdminRights = "INSERT INTO special_access_group_members (group_id, user_id, role) "
	                                     + "VALUES ((SELECT article_id FROM help_article_groups WHERE group_name = ?), ?, 'CRUD')";
	            try (PreparedStatement pstmtAdmin = connection.prepareStatement(insertAdminRights)) {
	                pstmtAdmin.setString(1, groupIdentifier);
	                pstmtAdmin.setInt(2, Session.getInstance().getCurrentUser().getId());
	                pstmtAdmin.executeUpdate();
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();  // Handle exception properly
	        }
	    }
	}
	
}
