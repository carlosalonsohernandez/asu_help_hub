package cse360Project;
import cse360Project.Password;

public class Testing {
	public static void main(String[] args) throws Exception
	{
		var pass = new Password("Someone1");
		System.out.println(pass.hashedPassword);
		System.out.println(pass.randSalt);
		
		System.out.println(pass.verifyPassword("Someone"));
		System.out.println(pass.verifyPassword("Someone1"));
	}
}
