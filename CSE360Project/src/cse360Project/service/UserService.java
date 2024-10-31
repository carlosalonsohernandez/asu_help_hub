package cse360Project.service;

import java.sql.*;
import java.util.Base64;
import java.util.List;

import cse360Project.Password;
import cse360Project.Session;
import cse360Project.model.User;
import cse360Project.model.UserAccountInfo;
import cse360Project.repository.RoleRepository;
import cse360Project.repository.UserRepository;
import javafx.scene.control.TableView;

/*******
 * <p> UserService Class </p>
 * 
 * <p> Description: Service class which deals with business logic on top of the data layer. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */

public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void register(String username, String password, List<String> roles) throws Exception {
        // Assume Password class handles hashing and salting
        Password passwordObj = new Password(password);
        String hashedPassword = Base64.getEncoder().encodeToString(passwordObj.getHashedPass());
        String randSalt = Base64.getEncoder().encodeToString(passwordObj.getSalt());

        // Create a User object with only essential fields
        User user = new User(username, hashedPassword, randSalt);

        // Insert the user
        userRepository.insertUser(user);

        // At this point, user.getId() will be populated
        for (String roleName : roles) {
            roleRepository.assignRoleToUser(user.getId(), roleRepository.getRoleIdByName(roleName));
        }
        
        Session.getInstance().setCurrentUser(user);
    }
    
    
    public boolean login(String username, String passwordOrOtp) throws Exception {
        User user = userRepository.getUserByUsername(username);
        System.out.println(user != null ? user.toString(): "null!E#");
        if (user == null) {
            return false; // User not found
        }

        // Attempt to log in with the password
        boolean isValidPassword = Password.verifyPassword(passwordOrOtp,
                Base64.getDecoder().decode(user.getRandSalt()),
                Base64.getDecoder().decode(user.getHashedPassword()));

        if (isValidPassword) {
            // Password login successful; proceed to set user session
            Session.getInstance().setCurrentUser(user);
            Session.getInstance().setOTPUsed(false);
            return true; // Successful login
        } else {
            // Check if the user is trying to log in with an OTP
            String storedOtp = user.getOtp();
            Timestamp otpExpiration = user.getOtpExpiration();

            // Verify the OTP and check for expiration
            if (storedOtp != null && storedOtp.equals(passwordOrOtp) &&
                    (otpExpiration != null && otpExpiration.after(new Timestamp(System.currentTimeMillis())))) {
                // Clear the OTP to make it one-time use
                userRepository.clearOtp(user.getUsername());
                
                // Proceed to set user session
                Session.getInstance().setCurrentUser(user);
                Session.getInstance().setOTPUsed(true);
                return true; // Successful OTP login
            }
        }
        return false; // Invalid login
    }


    public void setUserSession(ResultSet rs) throws SQLException {
        // Use the UserRepository to fetch the User object
        User user = userRepository.fetchUser(rs);
        
        // Set user session
        Session session = Session.getInstance();
        session.setCurrentUser(user);
    }
    
    public void resetUserPassword(String username) throws Exception {
        Integer userId = userRepository.getUserIdByUsername(username);
        System.out.println("Updating with user Id: " + userId );
        if (userId == null) {
            System.out.println("User not found: " + username);
            return; // User not found, exit the method
        }

        String newPassword = Password.generateRandomPassword(); // Generate a new password
        Password pass = new Password(newPassword);

        // Generate OTP and expiration time
        String otp = Password.generateOneTimePassword();
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + (24 * 3600000)); // 24 hour expiration

        // Update the user's password and OTP information
        userRepository.updateUserPasswordAndOtp(userId, 
                                                  Base64.getEncoder().encodeToString(pass.getHashedPass()),
                                                  Base64.getEncoder().encodeToString(pass.getSalt()),
                                                  otp, 
                                                  expirationTime);

        System.out.println("Password reset successfully for user: " + username);
        // Optionally, send the OTP to the user via email or display it
    }
    
    // open endpoint to repo and handle additional password logic
	public void updateUserPassword(User user, String newPassword) throws Exception {
	    Password pass = new Password(newPassword);
	    
	    userRepository.updateUserPassword(user.getUsername(), pass.getHashedPass(), pass.getSalt());
	    
	}
	
	
    // add or remove a role
    public void manageUserRole(int userId, String role, boolean addRole) throws Exception {
        int roleId = roleRepository.getRoleIdByName(role); // Get the role ID from the RoleRepository

        if (addRole) {
            userRepository.addUserRole(userId, roleId); // Call a method in UserRepository to add the role
        } else {
            userRepository.removeUserRole(userId, roleId); // Call a method in UserRepository to remove the role
        }
    }

    public void loadUsersIntoTable(TableView<List<String>> tableView) {
        try {
            List<List<String>> users = userRepository.getUsersForTable();
            // Clear existing items
            tableView.getItems().clear();
            // Load new items into the table
            tableView.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listUserAccounts() {
        try {
            List<UserAccountInfo> userAccounts = userRepository.getUserAccounts();
            for (UserAccountInfo user : userAccounts) {
                System.out.println("ID: " + user.id() + ", Username: " + user.userName() +
                                   ", First Name: " + user.firstName() + ", Role: " + user.role());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}