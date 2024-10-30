package cse360Project.model;

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