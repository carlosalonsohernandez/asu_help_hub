package cse360Project.model;

import java.sql.Timestamp;

/*******
 * <p> User Class </p>
 * 
 * <p> Description: A user class which will serve to abstract a lot of the raw data away in future versions/refactors. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */

public class User {
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the User class in order to function
	// The names of the variables specify their function and all are set to null until initialized 
    private int id;
    private String email;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String hashedPassword;
    private String randSalt;
    private String otp;
    private Timestamp otpExpiration;
    
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/
   // All required values 
	public User(int id, String email, String username, String firstName, String middleName, String lastName, String preferredName, String hashedPassword, String randSalt, String otp, Timestamp otpExpiration)
	{
		this.id = id;
		this.email = email;
		this.username = username;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.preferredName = preferredName;
		this.hashedPassword = hashedPassword;
		this.randSalt = randSalt;
		this.otp = otp;
		this.otpExpiration = otpExpiration;
		
	}
	
	public User(int id, String username, String hashedPassword, String randSalt)
	{
		this(id, null, username, null, null, null, null, hashedPassword, randSalt, null, null);
	}

	// Only required info
	public User(String username, String hashedPassword, String randSalt)
	{
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.randSalt = randSalt;
	}

	
	/**********************************************************************************************

	Getters and Setters
	
	**********************************************************************************************/
    
    
    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for firstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter and Setter for middleName
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    // Getter and Setter for lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getter and Setter for preferredName
    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    // Getter and Setter for hashedPassword
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    // Getter and Setter for randSalt
    public String getRandSalt() {
        return randSalt;
    }

    public void setRandSalt(String randSalt) {
        this.randSalt = randSalt;
    }

    // Getter and Setter for otp
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Getter and Setter for otpExpiration
    public Timestamp getOtpExpiration() {
        return otpExpiration;
    }

    public void setOtpExpiration(Timestamp otpExpiration) {
        this.otpExpiration = otpExpiration;
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", username=" + username + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName + ", preferredName=" + preferredName
				+ ", hashedPassword=" + hashedPassword + ", randSalt=" + randSalt + ", otp=" + otp + ", otpExpiration="
				+ otpExpiration + "]";
	}



}