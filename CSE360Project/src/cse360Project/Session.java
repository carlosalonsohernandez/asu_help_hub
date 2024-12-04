package cse360Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cse360Project.model.User;

/*******
 * <p> Session Class </p>
 * 
 * <p> Description: A session class which tracks the current logged in user and other functionalities like OTP usage and role invitations. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * @version 2.0.0   2024-10-30 Updated for Phase 2
 */

public class Session {
    /**********************************************************************************************
     * Attributes
     **********************************************************************************************/
    private static Session instance;
    private User currentUser; // Store the current user object
    private boolean OTPUsed;
    private List<String> invitedRoles;
    private List<String > activeGroups;
    private List<String> searchRequests = new ArrayList<>(Arrays.asList("H", "H2"));

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
	
	public List<String> getActiveGroups()
	{
		return this.activeGroups;
	}
	
	public void setActiveGroups(List<String> activeGroups)
	{
		this.activeGroups = activeGroups;
	}
	
	public List<String> getSearchReqs() {
		return this.searchRequests;
	}
	
	public void setSearchReqs(List<String> searchReqs) {
		this.searchRequests = searchReqs;
	}
	
	public void addSearchReq(String req) {
		if(!this.searchRequests.contains(req)) {
			this.searchRequests.add(req);
            System.out.println("-------------- SEARCH REQUESTS: " + String.join(", ", Session.getInstance().getSearchReqs()));
		}
	}
	
	public String toString() {
		String userDetails = currentUser != null ? currentUser.toString() : "NO SESSION USER SET";
		return "Session [currentUser=" + userDetails + ", OTPUsed=" + OTPUsed + ", invitedRoles=" + invitedRoles + "]";
	}
}