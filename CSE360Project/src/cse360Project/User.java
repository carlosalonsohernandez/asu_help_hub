package cse360Project;

import java.util.ArrayList;
import java.util.List;

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
    public String email;
    public String username;
    public Boolean oneTimeFlag;
    public String firstName;
    public String middleName;
    public String lastName;
    public String preferredName;
    public List<Role> roles; 
    public Password password;

	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/
    // constructor with all values
    public User(String email, String username, Boolean oneTimeFlag, String firstName, String middleName, String lastName, String preferredName, List<Role> roles) {
        this.email = email;
        this.username = username;
        this.oneTimeFlag = oneTimeFlag;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.roles = roles != null ? roles : new ArrayList<>(); // Initialize to empty list if null
        this.preferredName = preferredName;
    }
    
    // constructor in case student with no middle name and no preferred name
    public User(String email, String username, Boolean oneTimeFlag, String firstName, String lastName, List<Role> roles) {
        this(email, username, oneTimeFlag, firstName, null, lastName, null, roles);
    }
    
    // constructor with no middle name but preferred name
    public User(String email, String username, Boolean oneTimeFlag, String firstName, String lastName, String preferredName, List<Role> roles) {
        this(email, username, oneTimeFlag, firstName, null, lastName, preferredName, roles);
    }
    
	public User(String string, String string2, boolean b, String string3, String string4, Role role, String string5) {
		// TODO Auto-generated constructor stub
	}

	/**********************************************************************************************

	Methods
	
	**********************************************************************************************/
    // Method to get a formatted string of role names
    public String getRoleNames() {
    	String roleNames = "";
    	for(var role : roles)
    	{
    		roleNames += role + ", ";
    	}
    	return roleNames;
    }
    
	/**********************************************************************************************

	Getters and Setters
	
	**********************************************************************************************/
    // add role if not null
    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            this.roles.add(role);
        } else {
            throw new IllegalArgumentException("Role must be defined and not already assigned.");
        }
    }
    
    // remove role
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

}