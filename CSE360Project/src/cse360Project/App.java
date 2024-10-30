package cse360Project;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.control.ButtonBar;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cse360Project.model.Role;
import cse360Project.repository.InvitationRepository;
import cse360Project.repository.RoleRepository;
import cse360Project.repository.UserRepository;
import cse360Project.service.InvitationService;
import cse360Project.service.UserService;
import javafx.scene.layout.GridPane;

/*******
 * <p> App Class </p>
 * 
 * <p> Description: An application class which controls the main flow of the GUI and application. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */

public class App extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static UserRepository userRepo = null;
	private static RoleRepository roleRepo = null;
	private static InvitationRepository inviteRepo = null;
	private static UserService userService = null;
	private static InvitationService inviteService = null;
    // Entry point for JavaFX application
    @Override
    public void start(Stage primaryStage) throws UnsupportedEncodingException, Exception {
        // Title for the window
        databaseHelper.connectToDatabase();
        String s = databaseHelper.isDatabaseEmpty() ? "empty" : "not empty";
        
        // establish necessary repos
        userRepo = new UserRepository(databaseHelper.getConnection());
        roleRepo = new RoleRepository(databaseHelper.getConnection());
        inviteRepo = new InvitationRepository(databaseHelper.getConnection());
        
        // establish services
        userService = new UserService(userRepo, roleRepo);
        inviteService = new InvitationService(inviteRepo, roleRepo);
        
        databaseHelper.displayUsersByUser();
        System.out.println(s);
        primaryStage.setTitle("ASU Help Hub");

        // Labels and Text Fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Invite code fields
        Label inviteCodeLabel = new Label("Invite Code (For Registration):");
        TextField inviteCodeField = new TextField();

        // Buttons
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setOnAction(e -> {
            System.out.println(usernameField.getText());
            try {
                loginFlow(usernameField.getText(), passwordField.getText(), primaryStage);
                System.out.println("Hello " + (Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getFirstName(): "null"));
                //System.out.println("Hello " + Session.getInstance().getCurrentUser().getFirstName()); 
                if(Session.getInstance().getCurrentUser() != null)
                {
                    for (Role role : roleRepo.getRolesForUser(Session.getInstance().getCurrentUser().getId())) {
                        System.out.println("Role: " + role);
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        registerButton.setOnAction(e -> {
            System.out.println("Register clicked");
            
            try {
				if (databaseHelper.isDatabaseEmpty()) {
				    // Directly show the registration page if the database is empty
				    showRegistrationPage(primaryStage);
				} else {
				    // Check the invite code
				    String inviteCode = inviteCodeField.getText();
				    if (inviteService.validateInvitationCode(inviteCode)) {
				        // Proceed to registration if the invite code is valid
				        showRegistrationPage(primaryStage);
				    } else {
				        // error message
				        showAlert("Invalid invite code. Please try again.");
				    }
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        // Layouts
        VBox layout = new VBox(10);
        layout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField,
                                    inviteCodeLabel, inviteCodeField);

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(loginButton, registerButton);

        layout.getChildren().add(buttonLayout);
        
        layout.setPadding(new Insets(20, 20, 20, 20));

        // Set up the scene with the layout
        Scene scene = new Scene(layout, 300, 250); // Increased height for the invite code field

        // Set the scene on the stage
        primaryStage.setScene(scene);

        // Show the stage (window)
        primaryStage.show();
    }

    // Method to show an alert for invalid invite codes
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /***
     * method to dictate flow of login. Logic as follows: Login or OTP? > Multiple Roles? > Admin?
     ***/ 
    public void loginFlow(String username, String password, Stage primaryStage) throws Exception {
        if (userService.login(username, password)) {
            // Check if OTP was just used
            if (Session.getInstance().isOTPUsed()) {
                showResetPassword(primaryStage); // Show the reset password page
                return; // Exit the login flow
            }

            // Continue with the regular flow
            if (Session.getInstance().getCurrentUser().getFirstName() != null) {
                List<Role> roleList = roleRepo.getRolesForUser(Session.getInstance().getCurrentUser().getId());
                
                List<String> roles = roleList.stream().map(Role::getRoleName).toList();
                

                if (roles.stream().anyMatch(role -> role.equalsIgnoreCase("admin"))) {
                    if (roles.size() > 1) {
                        // User has multiple roles, show popup to select a role
                        showRoleSelectionPopup(roles, primaryStage);
                    } else {
                        // Only admin role
                        showAdminHomePage(primaryStage);
                    }
                } else {
                    // User is not an admin, go to student/instructor home page
                    showStudentAndInstructorHomePage(primaryStage);
                }
            } else {
                // Finish your account login
                showFinishSetup(primaryStage);
            }
        } else {
        	showAlert("Invalid credentials. Not logged in.");
            System.out.println("not logged in");
        }
    }

    // If an OTP is used, we require the user to reset their password
    private void showResetPassword(Stage stage) {
        stage.setTitle("Update Password");

        // GridPane layout with padding
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
        gridPane.setHgap(10); // Horizontal spacing between elements
        gridPane.setVgap(10); // Vertical spacing between elements

        Label usernameLabel = new Label("Username:");
        // Display the username from the session
        Label usernameField = new Label(Session.getInstance().getCurrentUser().getUsername());

        Label passwordLabel = new Label("New Password:");
        PasswordField passwordField = new PasswordField();

        Label passwordLabel2 = new Label("Confirm Password:");
        PasswordField passwordField2 = new PasswordField();

        // Warning label for password mismatch (initially invisible)
        Label warningLabel = new Label("Passwords must match.");
        warningLabel.setTextFill(Color.RED);
        warningLabel.setVisible(false); // Hide the label initially

        // Update Button
        Button updateButton = new Button("Update Password");

        updateButton.setOnAction(e -> {
            // Check if passwords match
            if (passwordField.getText().equals(passwordField2.getText())) {
                // Hide the warning label if passwords match
                warningLabel.setVisible(false);
                
                try {
                    // Update the user's password in the database
                    userService.updateUserPassword(Session.getInstance().getCurrentUser(), passwordField.getText());
                    System.out.println("Password updated successfully!");
                    start(stage); // Optionally, redirect to another page or refresh
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                // Show the warning label if passwords do not match
                warningLabel.setVisible(true);
            }
        });

        // Add elements to the GridPane
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(passwordLabel2, 0, 3);
        gridPane.add(passwordField2, 1, 3);
        gridPane.add(updateButton, 1, 4);
        gridPane.add(warningLabel, 1, 5); // Add warning label below the Update button

        // Set the Scene and show the Stage
        Scene scene = new Scene(gridPane, 400, 300); // Width and Height
        stage.setScene(scene);
    }

	// Method to show a popup for role selection
    private void showRoleSelectionPopup(List<String> roles, Stage primaryStage) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Role");
        dialog.setHeaderText("Please select your role for this session:");

        ListView<String> roleListView = new ListView<>();
        roleListView.getItems().addAll(roles);

        VBox vbox = new VBox(new Label("Select a role:"), roleListView);
        dialog.getDialogPane().setContent(vbox);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return roleListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedRole -> {
            if (selectedRole.equals("admin")) {
                showAdminHomePage(primaryStage);
            } else {
                showStudentAndInstructorHomePage(primaryStage);
            }
        });
    }

    
  
   // Show the registration page 
    public void showRegistrationPage(Stage stage) {
        stage.setTitle("Registration");

        // GridPane layout with padding
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
        gridPane.setHgap(10); // Horizontal spacing between elements
        gridPane.setVgap(10); // Vertical spacing between elements
        
        Label emailLabel = new Label("Username:");
        TextField emailField = new TextField();
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        
        Label passwordLabel2 = new Label("Confirm Password:");
        PasswordField passwordField2 = new PasswordField();

        // Warning label for password mismatch (initially invisible)
        Label warningLabel = new Label("Passwords must match.");
        warningLabel.setTextFill(Color.RED);
        warningLabel.setVisible(false); // Hide the label initially

        // Submit Button
        Button registerButton = new Button("Register");
        
        registerButton.setOnAction(e -> {
            // Check if passwords match
            if (passwordField.getText().equals(passwordField2.getText())) {
                // Hide the warning label if passwords match
                warningLabel.setVisible(false);
                
                try {
                	if(databaseHelper.isDatabaseEmpty())
                	{
                    	userService.register(emailField.getText(), passwordField.getText(), List.of("admin"));
                        System.out.println("User registered with admin privileges successfully!");
                	}
                	else
                	{
                    	userService.register(emailField.getText(), passwordField.getText(), Session.getInstance().getInvitedRoles());
                        System.out.println("User registered successfully!");
                	}
                	start(stage);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                // Show the warning label if passwords do not match
                warningLabel.setVisible(true);
            }
        });

        // Add elements to the GridPane
        gridPane.add(emailLabel, 0, 1);
        gridPane.add(emailField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(passwordLabel2, 0, 3);
        gridPane.add(passwordField2, 1, 3);
        gridPane.add(registerButton, 1, 4);
        gridPane.add(warningLabel, 1, 5); // Add warning label below the Register button

        // Set the Scene and show the Stage
        Scene scene = new Scene(gridPane, 400, 300); // Width and Height
        stage.setScene(scene);
    }
    
    public void showAdminHomePage(Stage stage)
    {
    	 stage.setTitle("Home");
         
         // GridPane layout with padding
         GridPane gridPane = new GridPane();
         gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
         gridPane.setHgap(10); // Horizontal spacing between elements
         gridPane.setVgap(10); // Vertical spacing between elements
         
         Label helloLabel = new Label("Welcome and hello admin " + Session.getInstance().getCurrentUser().getFirstName() + " " + Session.getInstance().getCurrentUser().getLastName() + "!");

         // logout 
         Button logoutButton = new Button("Logout");
         Button generateInviteButton = new Button("Generate Invite");
         Button manageUsersButton = new Button("Manage Users");
         
         logoutButton.setOnAction(e -> {
         	Session.getInstance().clear();
         	try {
 				start(stage);
 			} catch (UnsupportedEncodingException e1) {
 				// TODO Auto-generated catch block
 				e1.printStackTrace();
 			} catch (Exception e1) {
 				// TODO Auto-generated catch block
 				e1.printStackTrace();
 			}

         });

         generateInviteButton.setOnAction(e -> {
          	try {
  				showAdminInvitePage(stage);
  			} catch (Exception e1) {
  				// TODO Auto-generated catch block
  				e1.printStackTrace();
  			}

          });
         
         manageUsersButton.setOnAction(e -> {
           	try {
   				showManageUsersPage(stage);
   			} catch (Exception e1) {
   				// TODO Auto-generated catch block
   				e1.printStackTrace();
   			}

           });
        
         
         // Add elements to the GridPane
         gridPane.add(helloLabel, 0, 1);
         gridPane.add(generateInviteButton, 1, 2);
         gridPane.add(manageUsersButton, 1, 3);
         gridPane.add(logoutButton, 1, 4);

         ScrollPane scrollPane = new ScrollPane();
         scrollPane.setContent(gridPane);

         // Set the Scene and show the Stage
         Scene scene = new Scene(scrollPane, 400, 300); // Width and Height
         stage.setScene(scene);
    }
    


    public void showAdminInvitePage(Stage stage) {
        stage.setTitle("Generate Invitation");

        // GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Role selection using checkboxes
        Label roleLabel = new Label("Select Roles:");
        CheckBox studentCheckBox = new CheckBox("Student");
        CheckBox adminCheckBox = new CheckBox("Admin");
        CheckBox instructorCheckBox = new CheckBox("Instructor");

        // Text field for the invitation code
        Label inviteCodeLabel = new Label("Enter Invitation Code:");
        TextField inviteCodeField = new TextField();

        // Generate button
        Button generateButton = new Button("Generate Invitation");
        Label messageLabel = new Label();

        Button backButton = new Button("Back");
        
        generateButton.setOnAction(e -> {
            // Gather selected roles
            List<String> selectedRoles = new ArrayList<>();
            if (studentCheckBox.isSelected()) {
                selectedRoles.add("student");
            }
            if (adminCheckBox.isSelected()) {
                selectedRoles.add("admin");
            }
            if (instructorCheckBox.isSelected()) {
                selectedRoles.add("instructor");
            }

            String inviteCode = inviteCodeField.getText();
            
            if (!selectedRoles.isEmpty() && !inviteCode.isEmpty()) {
                try {
                    // Store invitation code with selected roles in the database
                    inviteRepo.storeInvite(inviteCode, selectedRoles);
                    messageLabel.setText("Invitation generated successfully with roles: " + String.join(", ", selectedRoles));
                    messageLabel.setTextFill(Color.GREEN);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error generating invitation.");
                    messageLabel.setTextFill(Color.RED);
                }
            } else {
                messageLabel.setText("Please select at least one role and enter a valid invite code.");
                messageLabel.setTextFill(Color.RED);
            }
        });
        
        backButton.setOnAction(e -> {
        	showAdminHomePage(stage);
        });

        // Layout positioning
        gridPane.add(roleLabel, 0, 0);
        gridPane.add(studentCheckBox, 1, 0);
        gridPane.add(adminCheckBox, 1, 1);
        gridPane.add(instructorCheckBox, 1, 2);
        gridPane.add(inviteCodeLabel, 0, 3);
        gridPane.add(inviteCodeField, 1, 3);
        gridPane.add(generateButton, 1, 4);
        gridPane.add(messageLabel, 1, 5);
        gridPane.add(backButton, 1, 6);

        // Scene setup
        Scene scene = new Scene(gridPane, 400, 250);
        stage.setScene(scene);
        stage.show();
    }


    public void showManageUsersPage(Stage stage) {
        stage.setTitle("Manage Student Accounts");

        // Create GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Label for page title
        Label titleLabel = new Label("Manage Student Accounts");
        gridPane.add(titleLabel, 0, 0, 2, 1); // Span two columns

        // Table to display user accounts
        TableView<List<String>> tableView = new TableView<>();
        TableColumn<List<String>, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0))); // Access username
        TableColumn<List<String>, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1))); // Access name
        TableColumn<List<String>, String> roleCol = new TableColumn<>("Roles");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2))); // Access roles

        tableView.getColumns().addAll(usernameCol, nameCol, roleCol);
        userService.loadUsersIntoTable(tableView); // Load user data into the table
        gridPane.add(tableView, 0, 1, 2, 1); // Span two columns

        // Reset Password button
        Button resetPasswordButton = new Button("Reset Password");
        resetPasswordButton.setOnAction(e -> {
            List<String> selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    userService.resetUserPassword(selectedUser.get(0));
                } catch (Exception e1) {
                    e1.printStackTrace();
                } // Assuming username is at index 0
            }
        });
        gridPane.add(resetPasswordButton, 0, 2);

        // Delete User button
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setOnAction(e -> {
            List<String> selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Create a confirmation dialog
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Deletion");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Are you sure you want to delete this user?");

                // Add custom buttons for Yes and No
                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

                // Show the confirmation dialog and wait for a response
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == yesButton) {
                    // User confirmed deletion
                    userRepo.deleteUserAccount(selectedUser.get(0)); // Assuming username is at index 0
                    // Refresh view to update table
                    userService.loadUsersIntoTable(tableView); // Reload users without reinitializing the entire page
                } else {
                    // User canceled the deletion, no action needed
                    System.out.println("Deletion aborted.");
                }
            } else {
                // Handle the case where no user is selected
                System.out.println("No user selected for deletion.");
            }
        });
        gridPane.add(deleteUserButton, 1, 2);

        // Manage Roles button
        Button manageRolesButton = new Button("Manage Roles");
        manageRolesButton.setOnAction(e -> {
            List<String> selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String username = selectedUser.get(0); // Assuming username is at index 0
                try {
                    showManageRolesDialog(username);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else {
                System.out.println("No user selected for role management.");
            }
        });
        gridPane.add(manageRolesButton, 0, 3);

        // Refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            userService.loadUsersIntoTable(tableView); // Reload users into the table
        });
        gridPane.add(refreshButton, 1, 3);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            showAdminHomePage(stage); // Navigate back to admin home
        });
        gridPane.add(backButton, 0, 4);

        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            Session.getInstance().clear();
            try {
                start(stage); // Redirect to login page
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        gridPane.add(logoutButton, 1, 4);

        // Set the Scene and show the Stage
        Scene scene = new Scene(gridPane, 600, 400);
        stage.setScene(scene);
    }

    // Method to show the dialog for managing roles
    private void showManageRolesDialog(String username) throws SQLException {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Manage Roles for " + username);

        // Create a VBox layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Load current roles for the user
        List<String> currentRoles = roleRepo.getRolesForUser(userRepo.getUserIdByUsername(username))
        		.stream().map(Role::getRoleName).toList(); 
        ListView<String> rolesListView = new ListView<>(FXCollections.observableArrayList(currentRoles));
        
        // Add button to add role
        TextField addRoleField = new TextField();
        addRoleField.setPromptText("Enter role to add");
        Button addRoleButton = new Button("Add Role");
        addRoleButton.setOnAction(e -> {
            String newRole = addRoleField.getText().trim();
            if (!newRole.isEmpty()) {
                try {
					roleRepo.assignRoleToUser(userRepo.getUserIdByUsername(username), newRole);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // Implement this method in your repo
                rolesListView.getItems().add(newRole);
                addRoleField.clear();
            }
        });

        // Add button to remove selected role
        Button removeRoleButton = new Button("Remove Selected Role");
        removeRoleButton.setOnAction(e -> {
            String selectedRole = rolesListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                try {
					roleRepo.removeRoleFromUser(userRepo.getUserIdByUsername(username), selectedRole);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // Implement this method in your repo
                rolesListView.getItems().remove(selectedRole);
            }
        });

        vbox.getChildren().addAll(new Label("Current Roles:"), rolesListView, addRoleField, addRoleButton, removeRoleButton);

        // Set the Scene and show the Stage
        Scene scene = new Scene(vbox, 300, 300);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    

	public void showStudentAndInstructorHomePage(Stage stage) {
        stage.setTitle("Home");
        
        // GridPane layout with padding
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
        gridPane.setHgap(10); // Horizontal spacing between elements
        gridPane.setVgap(10); // Vertical spacing between elements
        
        Label helloLabel = new Label("Welcome and hello " + Session.getInstance().getCurrentUser().getFirstName() + " " + Session.getInstance().getCurrentUser().getLastName() + "!");

        // logout 
        Button logoutButton = new Button("Logout");
        
        logoutButton.setOnAction(e -> {
        	Session.getInstance().clear();
        	try {
				start(stage);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        });

        // Add elements to the GridPane

        gridPane.add(helloLabel, 0, 1);
        gridPane.add(logoutButton, 1, 2);
        
        // use scroll pane for potentially long list of users
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);

        // Set the Scene and show the Stage
        Scene scene = new Scene(scrollPane, 400, 300); // Width and Height
        stage.setScene(scene);
    }
    

    // Finish setting up your account:
    public void showFinishSetup(Stage stage) {
        stage.setTitle("Finish setting up your account");

        // GridPane layout with padding
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
        gridPane.setHgap(10); // Horizontal spacing between elements
        gridPane.setVgap(10); // Vertical spacing between elements

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label middleNameLabel = new Label("Middle Name (Optional):");
        TextField middleNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label preferredNameLabel = new Label("Preferred Name (Optional):");
        TextField preferredNameField = new TextField();

        // Update button
        Button updateButton = new Button("Update");
        
        updateButton.setOnAction(e -> {
            System.out.println("Update clicked");
            try {
				userRepo.updateUserById(Session.getInstance().getCurrentUser().getId(), emailField.getText(), firstNameField.getText(), middleNameField.getText(), lastNameField.getText(), preferredNameField.getText());
				System.out.println(Session.getInstance().toString());
				start(stage);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            //showRegistrationPage();
        });

        // Add elements to the GridPane
        gridPane.add(emailLabel, 0, 1);
        gridPane.add(emailField, 1, 1);
        gridPane.add(firstNameLabel, 0, 2);
        gridPane.add(firstNameField, 1, 2);
        gridPane.add(middleNameLabel, 0, 3);
        gridPane.add(middleNameField, 1, 3);
        gridPane.add(lastNameLabel, 0, 4);
        gridPane.add(lastNameField, 1, 4);
        gridPane.add(preferredNameLabel, 0, 5);
        gridPane.add(preferredNameField, 1, 5);
        gridPane.add(updateButton, 1, 6);

        // Wrap in scoll pane to get the scrolling feature
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        // Set the Scene and show the Stage
        Scene scene = new Scene(scrollPane, 400, 300); // Width and Height
        stage.setScene(scene);
    }
    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}