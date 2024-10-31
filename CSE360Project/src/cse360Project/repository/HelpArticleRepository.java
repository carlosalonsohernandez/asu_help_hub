package cse360Project.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

import cse360Project.model.HelpArticle;

public class HelpArticleRepository {
    private final Connection connection;

    public HelpArticleRepository(Connection connection) {
        this.connection = connection;
    }

    // Create a new help article
    public void createArticle(HelpArticle article) throws SQLException {
        String query = "INSERT INTO help_articles (header, level, title, short_description, keywords, body, links, group_identifiers, is_sensitive, safe_title, safe_description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, article.getHeader());
            pstmt.setString(2, article.getLevel());
            pstmt.setString(3, article.getTitle());
            pstmt.setString(4, article.getShortDescription());
            pstmt.setString(5, String.join(", ", article.getKeywords()));
            pstmt.setString(6, article.getBody());
            pstmt.setString(7, String.join(", ", article.getLinks()));
            pstmt.setString(8, String.join(", ", article.getGroupIdentifiers()));
            pstmt.setBoolean(9, article.isSensitive());
            pstmt.setString(10, article.getSafeTitle());
            pstmt.setString(11, article.getSafeDescription());
            pstmt.executeUpdate();
        }
    }

    // Retrieve all help articles
    public List<HelpArticle> getAllArticles() throws SQLException {
        List<HelpArticle> articles = new ArrayList<>();
        String query = "SELECT * FROM help_articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                articles.add(mapToHelpArticle(rs));
            }
        }
        return articles;
    }

    // Update a help article by ID
    public void updateArticle(long id, HelpArticle article) throws SQLException {
        String query = "UPDATE help_articles SET header = ?, level = ?, title = ?, short_description = ?, keywords = ?, body = ?, links = ?, group_identifiers = ?, is_sensitive = ?, safe_title = ?, safe_description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, article.getHeader());
            pstmt.setString(2, article.getLevel());
            pstmt.setString(3, article.getTitle());
            pstmt.setString(4, article.getShortDescription());
            pstmt.setString(5, String.join(", ", article.getKeywords()));
            pstmt.setString(6, article.getBody());
            pstmt.setString(7, String.join(", ", article.getLinks()));
            pstmt.setString(8, String.join(", ", article.getGroupIdentifiers()));
            pstmt.setBoolean(9, article.isSensitive());
            pstmt.setString(10, article.getSafeTitle());
            pstmt.setString(11, article.getSafeDescription());
            pstmt.setLong(12, id);
            pstmt.executeUpdate();
        }
    }

    // Delete an article by ID
    public void deleteArticle(long id) throws SQLException {
        String query = "DELETE FROM help_articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    // Search articles by keyword in title, short description, or keywords
    public List<HelpArticle> searchArticles(String keyword) throws SQLException {
        List<HelpArticle> results = new ArrayList<>();
        String query = "SELECT * FROM help_articles WHERE title LIKE ? OR short_description LIKE ? OR keywords LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapToHelpArticle(rs));
                }
            }
        }
        return results;
    }

    // Map ResultSet to HelpArticle object

	private HelpArticle mapToHelpArticle(ResultSet rs) throws SQLException {
		return new HelpArticle(
				rs.getLong("id"),
				rs.getString("header"),
				rs.getString("level"),
				rs.getString("title"),
				rs.getString("short_description"),
				// Convert CSV string to Set<String>
				Arrays.stream(rs.getString("keywords").split(","))
					  .map(String::trim) // Trim whitespace
					  .collect(Collectors.toSet()), 
				rs.getString("body"),
				// Convert CSV string to List<String>
				Arrays.asList(rs.getString("links").split(",")), // Split by comma and convert to List
				// Convert CSV string to Set<String>
				Arrays.stream(rs.getString("group_identifiers").split(","))
					  .map(String::trim) // Trim whitespace
					  .collect(Collectors.toSet()),
				rs.getBoolean("is_sensitive"),
				rs.getString("safe_title"),
				rs.getString("safe_description")
		);
    }

    public HelpArticle getArticleByHeader(String header) {
        String query = "SELECT * FROM help_articles WHERE header = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, header);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapToHelpArticle(rs);
            } else {
                return null; // Article not found
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions (logging, rethrowing, etc.)
            return null; // Return null if there was an error
        }
    }

    public boolean deleteArticleByHeader(String header) {
        String query = "DELETE FROM help_articles WHERE header = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, header);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0; // Return true if an article was deleted
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions (logging, rethrowing, etc.)
            return false; // Return false if there was an error
        }
    }
    
	public void backupArticles(String filePath, List<HelpArticle> articlesToBackup) throws IOException {
	    ObjectMapper objectMapper = new ObjectMapper(); // From Jackson library
	    objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // For readable JSON
	    objectMapper.writeValue(new File(filePath), articlesToBackup);
	}
	
	public void restoreArticles(String filePath, boolean removeExisting) throws IOException, SQLException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<HelpArticle> articlesFromBackup = objectMapper.readValue(new File(filePath), new TypeReference<List<HelpArticle>>() {});
	    if (removeExisting) {
	        // Delete all existing help articles
	        String deleteAllArticles = "DELETE FROM help_articles";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.executeUpdate(deleteAllArticles);
	        }
	    }
	    // Insert or merge articles
	    for (HelpArticle article : articlesFromBackup) {
	        if (!doesHelpArticleExist(article.getId())) {
	            // Insert new article
	            createArticle(article);
	        } else {
	            // Update existing article or skip
	            updateArticle(article.getId(), article);
	        }
	    }
	}
	

	public List<HelpArticle> getArticlesByGroups(List<String> groups) {
	    List<HelpArticle> articles = new ArrayList<>();
	    
	    if (groups == null || groups.isEmpty()) {
	        return articles; // Return empty if no groups provided
	    }

	    // Prepare the SQL query using LIKE for each group
	    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM help_articles WHERE ");
	    for (int i = 0; i < groups.size(); i++) {
	        queryBuilder.append("group_identifiers LIKE ?");
	        if (i < groups.size() - 1) {
	            queryBuilder.append(" OR "); // Use OR to match any group
	        }
	    }

	    String query = queryBuilder.toString();

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        // Set the parameters for the prepared statement
	        for (int i = 0; i < groups.size(); i++) {
	            stmt.setString(i + 1, "%" + groups.get(i) + "%"); // Wildcard for partial match
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                // Convert comma-separated strings to appropriate collections
	                Set<String> keywords = new HashSet<>();
	                String keywordString = rs.getString("keywords");
	                if (keywordString != null && !keywordString.trim().isEmpty()) {
	                    String[] keywordArray = keywordString.split(",");
	                    for (String keyword : keywordArray) {
	                        keywords.add(keyword.trim());
	                    }
	                }

	                List<String> links = new ArrayList<>();
	                String linkString = rs.getString("links");
	                if (linkString != null && !linkString.trim().isEmpty()) {
	                    String[] linkArray = linkString.split(",");
	                    for (String link : linkArray) {
	                        links.add(link.trim());
	                    }
	                }

	                Set<String> groupIdentifiers = new HashSet<>();
	                String groupIdentifierString = rs.getString("group_identifiers");
	                if (groupIdentifierString != null && !groupIdentifierString.trim().isEmpty()) {
	                    String[] groupArray = groupIdentifierString.split(",");
	                    for (String group : groupArray) {
	                        groupIdentifiers.add(group.trim());
	                    }
	                }

	                // Instantiate HelpArticle using the constructor with collections
	                HelpArticle article = new HelpArticle(
	                    rs.getLong("id"),
	                    rs.getString("header"),
	                    rs.getString("level"),
	                    rs.getString("title"),
	                    rs.getString("short_description"),
	                    keywords,
	                    rs.getString("body"),
	                    links,
	                    groupIdentifiers,
	                    rs.getBoolean("is_sensitive"),
	                    rs.getString("safe_title"),
	                    rs.getString("safe_description")
	                );

	                articles.add(article);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return articles;
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
    
	private boolean doesHelpArticleExist(long articleId) throws SQLException {
	    String query = "SELECT COUNT(*) FROM help_articles WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setLong(1, articleId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }
	    }
	    return false;
	}

}