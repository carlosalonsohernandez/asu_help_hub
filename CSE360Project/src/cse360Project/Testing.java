package cse360Project;
import java.util.ArrayList;
import java.util.List;

import cse360Project.Password;

/*******
 * <p> Testing Class </p>
 * 
 * <p> Description: A testing class which will serve to automate some tests to ensure functionality across versions. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */
public class Testing {
	public static void test() throws Exception {
		// Create a Password object and hash the password "Someone1"
		Password pass = new Password("Someone1");

		// Print out the hashed password and salt (these will be in byte array format)
		System.out.println("Hashed Password (Base64 Encoded): " + java.util.Base64.getEncoder().encodeToString(pass.hashedPassword));
		System.out.println("Random Salt (Base64 Encoded): " + java.util.Base64.getEncoder().encodeToString(pass.randSalt));

		// Verify if the password "Someone" is correct (expected to fail)
		boolean isVerifiedIncorrect = pass.verifyPassword("Someone");
		System.out.println("Verification of 'Someone': " + (isVerifiedIncorrect ? "Success" : "Failure"));

		// Verify if the password "Someone1" is correct (expected to pass)
		boolean isVerifiedCorrect = pass.verifyPassword("Someone1");
		System.out.println("Verification of 'Someone1': " + (isVerifiedCorrect ? "Success" : "Failure"));
	}
	

	public static void userTest(String[] args) {
	    try {
	        // Test 1: Create a User with a single role
	        Role role = new Role(); // Create a role
	        role.assignRole("student"); // Assign the "student" role
	        List<Role> roles = new ArrayList<>();
	        roles.add(role); // Add the role to the roles list
	        User user = new User("testEmail@example.com", "testUser", true, "John", null, "Doe", null, roles); // Create a user without a middle or preferred name

	        // Test 2: Role assignment
	        System.out.println("Test 3: Role Assignment");
	        Role newRole = new Role();
	        newRole.assignRole("instructor");
	        user.addRole(newRole); // Add another role to the user
	        System.out.println("Roles assigned: " + user.getRoleNames());  // Expected output: "student, instructor, "

	        // Test 3: Remove role
	        System.out.println("Test 4: Remove Role");
	        user.removeRole(role); // Remove the "student" role
	        System.out.println("Roles after removal: " + user.getRoleNames());  // Expected output: "instructor, "

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
