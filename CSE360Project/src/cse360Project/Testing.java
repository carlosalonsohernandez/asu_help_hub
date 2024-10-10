package cse360Project;
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
}
