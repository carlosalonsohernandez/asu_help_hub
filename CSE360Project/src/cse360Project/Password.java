package cse360Project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Password {
	private byte[] hashedPassword;
	private int randSalt;
	
	
	public byte[] hashPassword(String password) throws Exception {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			
			byte[] hashed = digest.digest(password.getBytes());
			
			return hashed;
		} catch (Exception e) {
			throw new Exception("Error during encryption");
		}
	}
	
	// find way to verify a password given both its hashedvalue vs its string value
}
