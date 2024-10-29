package cse360Project.model;

public class Invitation {
    private String inviteCode;
    private boolean used;

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
