package cse360Project.service;

import java.sql.SQLException;
import java.util.List;

import cse360Project.repository.GroupRepository;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

public class GroupService {
	private final GroupRepository groupRepo;
	
	public GroupService(GroupRepository groupRepo) {
		this.groupRepo = groupRepo;
	}
	
	
	public void loadGroupsIntoTable(TableView<List<String>> tableView) {
	    try {
	        // Retrieve group data from the repository
	        List<List<String>> groupData = groupRepo.getAllGroups();
	        System.out.println("GROUP DATA: " + groupData.toString());

	        // Clear existing items and add new group data
	        tableView.getItems().clear();
	        tableView.getItems().addAll(groupData);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Optionally show an error message in the UI
	        showError("Error loading groups: " + e.getMessage());
	    }
	}
	
    // helper function
    public void showError(String message) {
    	System.out.println(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	
}
