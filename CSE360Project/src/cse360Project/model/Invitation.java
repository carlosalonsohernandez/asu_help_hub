package cse360Project.model;

/*******
 * <p> Invitation Class </p>
 * 
 * <p> Description: Object data container class which encapsulates an Invitation. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */

public class Invitation {
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
    private String inviteCode;
    private boolean used;
    
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

    // Constructor, getters, and setters
    public Invitation(String inviteCode, boolean used)
    {
    	this.inviteCode = inviteCode;
    	this.used = used;
    }
    public Invitation(String inviteCode)
    {
    	this.inviteCode = inviteCode;
    	this.used = false;
    }
    
    public Invitation() {}
    
    public void use()
    {
    	this.used = true;
    }
    
	/**********************************************************************************************

	Getters and Setters
	
	**********************************************************************************************/
    
    public String getInviteCode()
    {
    	return this.inviteCode;
    }
    
    public boolean isUsed()
    {
    	return this.used;
    }

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public void setUsed(boolean used) {
		// TODO Auto-generated method stub
		
	}
}
