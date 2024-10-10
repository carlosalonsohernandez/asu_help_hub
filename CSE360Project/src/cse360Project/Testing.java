package cse360Project;
import cse360Project.Password;

/*******
 * <p> Testing Class </p>
 * 
 * <p> Description: A testing class which will serve to automate some tests to ensure functionality across versions. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0	2024-10-09 Updated for Phase 1
 * 
 */
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
