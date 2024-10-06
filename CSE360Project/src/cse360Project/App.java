package cse360Project;

//Test Test Jeremy
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.layout.GridPane;

public class App extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    // Entry point for JavaFX application
    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Title for the window
    	databaseHelper.connectToDatabase();
        primaryStage.setTitle("CSE 360 Help System");

        // Labels and Text Fields
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Buttons
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setOnAction(e -> {
            System.out.println(emailField.getText());
            try {
				loginFlow(emailField.getText(), passwordField.getText());
			} catch (SQLException e1) { 
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        registerButton.setOnAction(e -> {
            System.out.println("Register clicked");
            showRegistrationPage(primaryStage, emailField.getText());
        });

        // Layouts
        VBox layout = new VBox(10); 
        layout.getChildren().addAll(emailLabel, emailField, passwordLabel, passwordField);

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().addAll(loginButton, registerButton);

        layout.getChildren().add(buttonLayout); // Add buttons below the form
        
        layout.setPadding(new Insets(20, 20, 20, 20));

        // Set up the scene with the layout
        Scene scene = new Scene(layout, 300, 200);

        // Set the scene on the stage
        primaryStage.setScene(scene);

        // Show the stage (window)
        primaryStage.show();
    }
    
    /***
     * Very basic boolean login that just prints to the command line. 
     * TODO: Implement auth and current session logic to actually login a user.
     * **/
    public void loginFlow(String email, String password) throws SQLException
    {
    	if(databaseHelper.login(email, password)) {
    		System.out.println("LOGGEDIN!");
    	} else {
    		System.out.println("notlogged");
    	}
    	
    }

    public void showRegistrationPage(Stage stage, String email) {
        stage.setTitle("Registration");

        // GridPane layout with padding
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Adds padding to avoid elements near edges
        gridPane.setHgap(10); // Horizontal spacing between elements
        gridPane.setVgap(10); // Vertical spacing between elements

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label middleNameLabel = new Label("Middle Name (Optional):");
        TextField middleNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label preferredNameLabel = new Label("Preferred Name (Optional):");
        TextField preferredNameField = new TextField();

        // Role selection
        Label roleLabel = new Label("Register as:");
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("IA", "User", "Admin"); 

        // Submit Button
        Button registerButton = new Button("Register");

        // Add elements to the GridPane
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(firstNameLabel, 0, 2);
        gridPane.add(firstNameField, 1, 2);
        gridPane.add(middleNameLabel, 0, 3);
        gridPane.add(middleNameField, 1, 3);
        gridPane.add(lastNameLabel, 0, 4);
        gridPane.add(lastNameField, 1, 4);
        gridPane.add(preferredNameLabel, 0, 5);
        gridPane.add(preferredNameField, 1, 5);
        gridPane.add(roleLabel, 0, 6);
        gridPane.add(roleChoiceBox, 1, 6);
        gridPane.add(registerButton, 1, 7);

        // Set the Scene and show the Stage
        Scene scene = new Scene(gridPane, 400, 300); // Width and Height
        stage.setScene(scene);
    }
    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}