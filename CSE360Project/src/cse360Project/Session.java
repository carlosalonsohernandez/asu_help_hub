package cse360Project;

import java.util.List;

import cse360Project.model.User;

public class Session {
    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/
    private static Session instance;
    private User currentUser; // Store the current user object
    private boolean OTPUsed;
    private List<String> invitedRoles;

    /**********************************************************************************************
     * Constructors
     **********************************************************************************************/

    // Empty constructor
    private Session() {
        this.OTPUsed = false;
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**********************************************************************************************
     * Methods
     **********************************************************************************************/
    
    // Method to clear session
    public void clear() {
        this.currentUser = null;
        this.OTPUsed = false;
    }

    // Set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
		System.out.println(Session.getInstance().toString());
    }

    // Get the current user
    public User getCurrentUser() {
        return this.currentUser;
    }

    public boolean isOTPUsed() {
        return OTPUsed;
    }

    public void setOTPUsed(boolean oTPJustUsed) {
        OTPUsed = oTPJustUsed;
    }
    

    public void setInvitedRoles(List<String> roleNames) {
  		this.invitedRoles = roleNames;
	}
	
	public List<String> getInvitedRoles()
	{
		return this.invitedRoles;
	}

	public String toString() {
		String userDetails = currentUser != null ? currentUser.toString() : "NO SESSION USER SET";
		return "Session [currentUser=" + userDetails + ", OTPUsed=" + OTPUsed + ", invitedRoles=" + invitedRoles + "]";
	}
}