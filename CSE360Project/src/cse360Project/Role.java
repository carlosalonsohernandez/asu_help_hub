package cse360Project;

import cse360Project.User;

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

public class Role {
	
	// public static permissionDict
	
	public String roleName;
	
	public void assignRole(String role)
	{
		switch(role.toLowerCase()) {
			case "instructor":
				this.roleName = "Instructor";
			case "professor":
				this.roleName = "Professor";
			case "student":
				this.roleName = "Student";
			default:
				this.roleName = "Undefined";
		}
	}
	
	public void removeRole()
	{
		this.roleName = "Undefined";
	}
	
	// verify permission class after define a permission dictionary
}
