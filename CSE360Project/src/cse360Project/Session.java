package cse360Project;

public class Session {
	private static Session instance;
	private int userId;
	private String email;
	private String username;
	private String firstName;
	private String middleName;
	private String lastName;
	private String preferredName;
	private String role;
	
	// empty constructor
	private Session() {}
	
	private Session(int userId, String username) 
	{
		this.userId = userId;
		this.username = username;
	}
	
	public static Session getInstance()
	{
		if(instance == null)
		{
			instance = new Session();
		}
		return instance;
	}

	
	// method to clear session
	public void clear()
	{
		this.userId = -1;
		this.username = null;
	}
	
	// getters and setters

	public void setUser(int userId, String email)
	{
		this.userId = userId;
		this.email = email;
	}
	
	public void setUser(int userId, String email, String firstName, String lastName, String preferredName, String role) {
	    this.userId = userId;
	    this.email = email;
	    this.firstName = firstName != null ? firstName : "";
	    this.lastName = lastName != null ? lastName : "";
	    this.preferredName = preferredName != null ? preferredName : "";
	    this.role = role != null ? role : "";
	}
	
	public int getUserId()
	{
		return this.userId;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
}
