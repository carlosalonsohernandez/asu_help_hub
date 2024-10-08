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

	public void setUser(int userId, String username)
	{
		this.userId = userId;
		this.username = username;
	}
	
	public void setUser(int userId, String username, String email, String firstName, String lastName, String preferredName, String role) {
	    this.userId = userId;
	    this.username = username;
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
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getMiddleName()
	{
		return this.middleName;
	}
	
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getPreferredName()
	{
		return this.preferredName;
	}
	
	public void setPreferredName(String preferredName)
	{
		this.preferredName = preferredName;
	}
	
	public String getRoleName()
	{
		return this.role;
	}
	
	public void setRoleName(String role)
	{
		this.role = role;
	}
}
