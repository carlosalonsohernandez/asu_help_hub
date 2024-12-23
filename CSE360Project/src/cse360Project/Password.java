package cse360Project;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/*******
 * <p> Password Class </p>
 * 
 * <p> Description: A password utility class which encapsulates a lot of the functionality of hashing and validating passwords.  </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */
public class Password {

    public byte[] hashedPassword;
    public byte[] randSalt;
    
    public Password(String password) throws Exception {
    	hashPassword(password);
    	
    }

    // hash a string password
    public byte[] hashPassword(String password) throws Exception {
        try {
            // Generate random salt using secure rng
            randSalt = new byte[16]; 
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randSalt); // Fill the randSalt array with random values

            // Concatenate the salt and the password
            byte[] passwordBytes = password.getBytes();
            byte[] saltedPassword = new byte[randSalt.length + passwordBytes.length];

            System.arraycopy(randSalt, 0, saltedPassword, 0, randSalt.length); // Copy salt to the start
            System.arraycopy(passwordBytes, 0, saltedPassword, randSalt.length, passwordBytes.length); // Add password bytes after salt

            // Hash the concatenated salt and password using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hashedPassword = digest.digest(saltedPassword); // Hash salted password

            return hashedPassword;
        } catch (Exception e) {
            throw new Exception("Error during encryption: " + e.getMessage());
        }
    }
    
    // verify the password given the stored salt, the storedHashedPassword, and the new String
    public boolean verifyPassword(String password) throws Exception {
        try {
            // Concatenate the stored salt with the input password
            byte[] passwordBytes = password.getBytes();
            byte[] saltedPassword = new byte[randSalt.length + passwordBytes.length];

            System.arraycopy(randSalt, 0, saltedPassword, 0, randSalt.length); // Copy stored salt
            System.arraycopy(passwordBytes, 0, saltedPassword, randSalt.length, passwordBytes.length); // Add password

            // Hash the salted password
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedInputPassword = digest.digest(saltedPassword); // Hash salted password

            // Compare the hashed input password with the stored hashed password
            return MessageDigest.isEqual(hashedInputPassword, hashedPassword);
        } catch (Exception e) {
            throw new Exception("Error during password verification: " + e.getMessage());
        }
    }
    
    // static version which can provide utility
    public static boolean verifyPassword(String password, byte[] randSalt, byte[] hashedPassword) throws Exception {
        try {
            // Concatenate the stored salt with the input password
            byte[] passwordBytes = password.getBytes();
            byte[] saltedPassword = new byte[randSalt.length + passwordBytes.length];

            System.arraycopy(randSalt, 0, saltedPassword, 0, randSalt.length); // Copy stored salt
            System.arraycopy(passwordBytes, 0, saltedPassword, randSalt.length, passwordBytes.length); // Add password

            // Hash the salted password
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedInputPassword = digest.digest(saltedPassword); // Hash salted password

            // Compare the hashed input password with the stored hashed password
            return MessageDigest.isEqual(hashedInputPassword, hashedPassword);
        } catch (Exception e) {
            throw new Exception("Error during password verification: " + e.getMessage());
        }
    }
    
    // generate a one time password
	public static String generateOneTimePassword() {
	    int length = 6; // Length of the OTP
	    Random random = new Random();
	    StringBuilder otp = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	        otp.append(random.nextInt(10)); // Append random digit
	    }
	    System.out.println("OTP Generated: " + otp.toString());
	    return otp.toString();
	}
	
	// Method to generate a random password
	public static String generateRandomPassword() {
	    byte[] randomBytes = new byte[16];
	    SecureRandom random = new SecureRandom();
	    random.nextBytes(randomBytes);
	    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, 16); // Return a substring to ensure length
	}
    
    
    // getters and setters

    public byte[] getSalt() {
        return this.randSalt;
    }
    
    public byte[] getHashedPass() {
    	return this.hashedPassword;
    }
}