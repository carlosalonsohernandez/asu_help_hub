package cse360Project;

import cse360Project.User;

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
