package cse360Project;
import cse360Project.Role;

public class User {
	// user attributes
	public String email;
	public String username;
	public Boolean oneTimeFlag;
	public String firstName;
	public String middleName;
	public String lastName;
	public Role role;
	
	// constructor with all values
	public User(String email, String username, Boolean oneTimeFlag, String firstName, String middleName, String lastName, Role role1) {
		this.email = email;
		this.username = username;
		this.oneTimeFlag = oneTimeFlag;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.role = role1;
		// validate password
	}
	
	// constructor in case student with no middle name
	public User(String email, String username, Boolean oneTimeFlag, String firstName,  String lastName, Role role1) {
		this.email = email;
		this.username = username;
		this.oneTimeFlag = oneTimeFlag;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role1;
	}
	
	
	// add role if not null
	public void addRole(Role role1) {
		if (role1 != null) {
			this.role = role1;
		}
		
		throw new IllegalArgumentException("Role must be defined");
	}
	
	public void removeRole(Role role1) {
		this.role = null;
	}
	
	// validate password class
	
	
}