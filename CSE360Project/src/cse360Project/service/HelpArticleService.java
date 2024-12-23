package cse360Project.service;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cse360Project.model.HelpArticle;
import cse360Project.repository.GroupRepository;
import cse360Project.repository.HelpArticleRepository;

/*******
 * <p> HelpArticleService Class </p>
 * 
 * <p> Description: Service class which deals with business logic on top of the data layer. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */

public class HelpArticleService {
    private final HelpArticleRepository articleRepo;
    private final GroupRepository groupRepo;

    public HelpArticleService(HelpArticleRepository articleRepo, GroupRepository groupRepo) {
        this.articleRepo = articleRepo;
        this.groupRepo = groupRepo;
    }

    public void createNewArticle(HelpArticle article) {
        try {
            articleRepo.createArticle(article);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadArticlesIntoTable(TableView<List<String>> tableView) {
    	loadArticlesIntoTable(tableView, null, null);
    }
    
    public void loadArticlesIntoTable(TableView<List<String>> tableView, List<String> selectedGroups) {
    	loadArticlesIntoTable(tableView, selectedGroups, null);
    }
    
    public void loadArticlesIntoTable(TableView<List<String>> tableView, List<String> selectedGroups, List<String> selectedLevels) {
        try {
            List<HelpArticle> articles;

            // Check if both selectedGroups and selectedLevels have values
            if ((selectedGroups != null && !selectedGroups.isEmpty()) && (selectedLevels != null && !selectedLevels.isEmpty())) {
                // Get articles filtered by both groups and levels
                articles = articleRepo.getArticlesByGroupsAndLevels(selectedGroups, selectedLevels);
                System.out.println("Filtered by groups and levels: " + articles.size());
            } else if (selectedGroups != null && !selectedGroups.isEmpty()) {
                // Filter by groups only
                articles = articleRepo.getArticlesByGroups(selectedGroups);
                System.out.println("Filtered by groups only: " + articles.size());
            } else if (selectedLevels != null && !selectedLevels.isEmpty()) {
                // Filter by levels only
                articles = articleRepo.getArticlesByLevels(selectedLevels);
                System.out.println("Filtered by levels only: " + articles.size());
            } else {
                // No filtering, load all articles
                articles = articleRepo.getAllArticles();
                System.out.println("No filters applied, loaded all articles: " + articles.size());
            }

            // Clear the table and add the articles to it
            tableView.getItems().clear();
            System.out.println("Fetched Articles: " + articles.size());
            for (HelpArticle article : articles) {
                tableView.getItems().add(article.toList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateArticle(long id, HelpArticle article) {
        try {
            articleRepo.updateArticle(id, article);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteArticle(long id) {
        try {
            articleRepo.deleteArticle(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    



    public void restoreArticles() {
        VBox restoreDialogPane = new VBox();
        
        // Create TextInputDialog to get the restore file name
        TextInputDialog restoreDialog = new TextInputDialog();
        restoreDialog.setTitle("Restore Articles");
        restoreDialog.setHeaderText("Specify Restore File Name");
        restoreDialog.setContentText("Enter the backup file name (with .backup extension):");

        // Add checkbox for merging
        CheckBox mergeCheckBox = new CheckBox("Merge?");
        mergeCheckBox.setSelected(false); // Default to not merging
        restoreDialogPane.getChildren().addAll(restoreDialog.getDialogPane(), mergeCheckBox);

        // Show dialog and wait for a response
        restoreDialog.showAndWait().ifPresent(fileName -> {
            if (fileName != null && !fileName.trim().isEmpty()) {
                boolean merge = mergeCheckBox.isSelected(); // Get merge option
                try {
					articleRepo.restoreArticles(fileName.trim(), merge);
				} catch (IOException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showInfo("Restore successful from " + fileName);
            } else {
                showError("File name cannot be empty.");
            }
        });
    }

    public void searchArticlesByKeyword(String keyword, TableView<List<String>> tableView) {
        try {
            List<HelpArticle> articles = articleRepo.searchArticles(keyword);
            tableView.getItems().clear();
            for (HelpArticle article : articles) {
                tableView.getItems().add(article.toList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void createNewArticleForm() {
        // Open a new window for creating an article
        Stage createArticleStage = new Stage();
        createArticleStage.setTitle("Create New Article");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create input fields
        TextField levelField = new TextField();
        TextField titleField = new TextField();
        TextField shortDescriptionField = new TextField();
        TextArea bodyField = new TextArea();
        TextField keywordsField = new TextField(); // Comma-separated keywords
        TextField linksField = new TextField(); // Comma-separated links
        TextField groupIdentifiersField = new TextField(); // Comma-separated group identifiers

        // Adding input fields to the grid
        gridPane.add(new Label("Level:"), 0, 0);
        gridPane.add(levelField, 1, 0);
        gridPane.add(new Label("Title:"), 0, 1);
        gridPane.add(titleField, 1, 1);
        gridPane.add(new Label("Short Description:"), 0, 2);
        gridPane.add(shortDescriptionField, 1, 2);
        gridPane.add(new Label("Body:"), 0, 3);
        gridPane.add(bodyField, 1, 3);
        gridPane.add(new Label("Authors (comma-separated):"), 0, 4);
        gridPane.add(keywordsField, 1, 4);
        gridPane.add(new Label("Links (comma-separated):"), 0, 5);
        gridPane.add(linksField, 1, 5);
        gridPane.add(new Label("Group Identifiers (comma-separated):"), 0, 6);
        gridPane.add(groupIdentifiersField, 1, 6);

        // Create button to save the article
        Button createButton = new Button("Create Article");
        createButton.setOnAction(e -> {
            try {
                // Create a new article object
                HelpArticle article = new HelpArticle(
                        null, // ID will be auto-generated
                        groupIdentifiersField.getText() + ":" + levelField.getText() + ":" + titleField.getText(),
                        levelField.getText(),
                        titleField.getText(),
                        shortDescriptionField.getText(),
                        Set.of(keywordsField.getText().split(",")),
                        bodyField.getText(),
                        List.of(linksField.getText().split(",")),
                        Set.of(groupIdentifiersField.getText().split(",")),
                        false,
                        titleField.getText(), // Safe title
                        shortDescriptionField.getText() // Safe description
                );

                // Save the article to the database
                long articleId = articleRepo.createArticle(article);

                // Add the article to the specified groups
                String groupIdentifiers = groupIdentifiersField.getText();
                if (groupIdentifiers != null && !groupIdentifiers.trim().isEmpty()) {
                    addToGroups(articleId, groupIdentifiers); // Call the service to handle group relationships
                }

                createArticleStage.close(); // Close the form
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Error creating article: " + ex.getMessage());
            }
        });

        gridPane.add(createButton, 1, 9);
        Scene scene = new Scene(gridPane, 400, 500);
        createArticleStage.setScene(scene);
        createArticleStage.show();
    }
    
	public void addToGroups(long articleId, String groupIdentifiers) throws SQLException {
	    if (groupIdentifiers == null || groupIdentifiers.trim().isEmpty()) {
	        throw new IllegalArgumentException("Group identifiers cannot be null or empty.");
	    }

	    String[] groups = groupIdentifiers.split(",");

	    for (String groupName : groups) {
	        groupName = groupName.trim(); // Clean up whitespace
	        if (!groupName.isEmpty()) {
	            groupRepo.addArticleToGroup(articleId, groupName); // Call the repository method
	        }
	    }
	}

    public void deleteArticle(String articleHeader) {
        boolean deleted = articleRepo.deleteArticleByHeader(articleHeader);
		if (!deleted) {
		    showError("No article found with the given header.");
		}
    }

    public void viewGroupsForm() {
        // Open a new window for managing groups
        Stage manageGroupsStage = new Stage();
        manageGroupsStage.setTitle("Manage Groups");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // You can implement group management functionality here
        // For example, display current groups, add or remove groups

        // Sample label to show functionality
        Label infoLabel = new Label("Group management features will be implemented here.");
        gridPane.add(infoLabel, 0, 0);

        Scene scene = new Scene(gridPane, 400, 200);
        manageGroupsStage.setScene(scene);
        manageGroupsStage.show();
    }
    
    public List<Map<String, Object>> getArticlesForNonAdmin() {
        return articleRepo.fetchArticlesForNonAdmin();
    }
    
    
    public void viewArticleForm(String articleHeader) {
        // Load the existing article data from the repository using the header
        HelpArticle existingArticle = articleRepo.getArticleByHeader(articleHeader);
        if (existingArticle == null) {
            showError("Article not found.");
            return;
        }

        // Open a new window for viewing the article
        Stage viewArticleStage = new Stage();
        viewArticleStage.setTitle("View Article");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Display article data in read-only fields
        Label headerLabel = new Label(existingArticle.getHeader());
        Label levelLabel = new Label(existingArticle.getLevel());
        Label titleLabel = new Label(existingArticle.getTitle());
        Label shortDescriptionLabel = new Label(existingArticle.getShortDescription());
        Label bodyLabel = new Label(existingArticle.getBody());
        Label keywordsLabel = new Label(String.join(",", existingArticle.getKeywords()));
        Label linksLabel = new Label(String.join(",", existingArticle.getLinks()));
        Label groupIdentifiersLabel = new Label(String.join(",", existingArticle.getGroupIdentifiers()));
        Label sensitiveLabel = new Label(existingArticle.isSensitive() ? "Yes" : "No");

        // Adding article details to the grid
        gridPane.add(new Label("Header:"), 0, 0);
        gridPane.add(headerLabel, 1, 0);
        gridPane.add(new Label("Level:"), 0, 1);
        gridPane.add(levelLabel, 1, 1);
        gridPane.add(new Label("Title:"), 0, 2);
        gridPane.add(titleLabel, 1, 2);
        gridPane.add(new Label("Short Description:"), 0, 3);
        gridPane.add(shortDescriptionLabel, 1, 3);
        gridPane.add(new Label("Body:"), 0, 4);
        gridPane.add(bodyLabel, 1, 4);
        gridPane.add(new Label("Keywords (comma-separated):"), 0, 5);
        gridPane.add(keywordsLabel, 1, 5);
        gridPane.add(new Label("Links (comma-separated):"), 0, 6);
        gridPane.add(linksLabel, 1, 6);
        gridPane.add(new Label("Group Identifiers (comma-separated):"), 0, 7);
        gridPane.add(groupIdentifiersLabel, 1, 7);
        gridPane.add(new Label("Is Sensitive:"), 0, 8);
        gridPane.add(sensitiveLabel, 1, 8);

        // Adding a close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> viewArticleStage.close());
        gridPane.add(closeButton, 1, 9);

        // Set up the scene and show the stage
        Scene scene = new Scene(gridPane, 400, 500);
        viewArticleStage.setScene(scene);
        viewArticleStage.show();
    }

    public void updateArticleForm(String articleHeader) {
        // Load the existing article data from the repository using the header
        HelpArticle existingArticle = articleRepo.getArticleByHeader(articleHeader);
        if (existingArticle == null) {
            showError("Article not found.");
            return;
        }

        // Open a new window for updating the article
        Stage updateArticleStage = new Stage();
        updateArticleStage.setTitle("Update Article");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Create input fields pre-filled with existing article data
        TextField headerField = new TextField(existingArticle.getHeader());
        TextField levelField = new TextField(existingArticle.getLevel());
        TextField titleField = new TextField(existingArticle.getTitle());
        TextField shortDescriptionField = new TextField(existingArticle.getShortDescription());
        TextField keywordsField = new TextField(String.join(",", existingArticle.getKeywords()));
        TextField linksField = new TextField(String.join(",", existingArticle.getLinks()));
        TextField groupIdentifiersField = new TextField(String.join(",", existingArticle.getGroupIdentifiers()));
        CheckBox sensitiveCheckBox = new CheckBox("Is Sensitive");
        sensitiveCheckBox.setSelected(existingArticle.isSensitive());

        // Adding input fields to the grid
        gridPane.add(new Label("Header:"), 0, 0);
        gridPane.add(headerField, 1, 0);
        gridPane.add(new Label("Level:"), 0, 1);
        gridPane.add(levelField, 1, 1);
        gridPane.add(new Label("Title:"), 0, 2);
        gridPane.add(titleField, 1, 2);
        gridPane.add(new Label("Short Description:"), 0, 3);
        gridPane.add(shortDescriptionField, 1, 3);
        gridPane.add(new Label("Authors (comma-separated):"), 0, 4);
        gridPane.add(keywordsField, 1, 4);
        gridPane.add(new Label("Links (comma-separated):"), 0, 5);
        gridPane.add(linksField, 1, 5);
        gridPane.add(new Label("Group Identifiers (comma-separated):"), 0, 6);
        gridPane.add(groupIdentifiersField, 1, 6);
        gridPane.add(sensitiveCheckBox, 1, 7);

        // Create button to save the updated article
        Button updateButton = new Button("Update Article");
        updateButton.setOnAction(e -> {
            try {
                HelpArticle updatedArticle = new HelpArticle(
                        existingArticle.getId(),
                        headerField.getText(),
                        levelField.getText(),
                        titleField.getText(),
                        shortDescriptionField.getText(),
                        Set.of(keywordsField.getText().split(",")),
                        existingArticle.getBody(), // Keep the original body
                        List.of(linksField.getText().split(",")),
                        Set.of(groupIdentifiersField.getText().split(",")),
                        sensitiveCheckBox.isSelected(),
                        titleField.getText(), // Assuming safeTitle is the same as title
                        shortDescriptionField.getText() // Assuming safeDescription is the same as shortDescription
                );
                articleRepo.updateArticle(existingArticle.getId(), updatedArticle);
                updateArticleStage.close(); // Close the form
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Error updating article: " + ex.getMessage());
            }
        });

        gridPane.add(updateButton, 1, 8);
        Scene scene = new Scene(gridPane, 400, 450);
        updateArticleStage.setScene(scene);
        updateArticleStage.show();
    }
    
    public void loadArticlesIntoTableByLevel(TableView<HelpArticle> tableView, List<String> levels) {
        // Implement database logic to filter articles by the `level` attribute
        List<HelpArticle> filteredArticles = articleRepo.getArticlesByLevels(levels);
        tableView.getItems().setAll(filteredArticles);
    }

    // helper functions
    public void showError(String message) {
    	System.out.println(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait(); // Wait for the user to close the dialog
    }
}