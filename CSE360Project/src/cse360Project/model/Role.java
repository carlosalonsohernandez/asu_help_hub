package cse360Project.model;

/*******
 * <p> Role Class </p>
 * 
 * <p> Description: Object data container class which encapsulates a Role. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */

public class Role {
    private int id;
    private String roleName;

    // Constructor, getters, and setters
    public Role(int id, String roleName)
    {
    	this.id = id;
    	this.roleName = roleName;
    }
    

	public int getID()
    {
    	return this.id;
    }
    
    public void setID(int id)
    {
    	this.id = id;
    }
    
    public String getRoleName()
    {
    	return this.roleName;
    }
    
    public void setRoleName(String roleName)
    {
    	this.roleName = roleName;
    }
}