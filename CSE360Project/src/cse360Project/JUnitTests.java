package cse360Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cse360Project.model.User;
import cse360Project.repository.RoleRepository;
import cse360Project.repository.UserRepository;
import cse360Project.service.UserService;

import java.sql.Timestamp;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JUnitTests {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserService userService;
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private Session session;

    @BeforeEach
    void setUp() throws Exception {
    	
        // Initialize dependencies
    	databaseHelper.connectToDatabase();
        userRepository = new UserRepository(databaseHelper.getConnection()); 
        roleRepository = new RoleRepository(databaseHelper.getConnection());
        userService = new UserService(userRepository, roleRepository);
        session = Session.getInstance();

        userRepository.clearAllUsers(); // Assuming this method exists to clear the USERS table

        // Populate the repository with a test user
        String username = "testUser";
        String password = "SecurePassword123";

        // Create a Password object for hashing
        Password passwordUtil = new Password(password);
        byte[] hashedPassword = passwordUtil.getHashedPass();
        byte[] salt = passwordUtil.getSalt();

        // Create a User object with hashed password and salt
        User testUser = new User(
            1, "test@example.com", username, "John", null, "Doe", null,
            Base64.getEncoder().encodeToString(hashedPassword),
            Base64.getEncoder().encodeToString(salt), null, null
        );

        userRepository.insertUser(testUser);
    }

    @Test
    void testLogin() throws Exception {
        String username = "testUser";
        String password = "SecurePassword123";

        // Test valid login
        boolean loginSuccess = userService.login(username, password);
        assertTrue(loginSuccess, "Login should succeed with correct credentials.");

        // Test invalid login
        boolean loginFail = userService.login(username, "WrongPassword");
        assertFalse(loginFail, "Login should fail with incorrect credentials.");

        // Verify the session is updated
        User loggedInUser = session.getCurrentUser();
        assertNotNull(loggedInUser, "Session should store the logged-in user.");
        assertEquals(username, loggedInUser.getUsername(), "The logged-in user's username should match.");
    }

    @Test
    void testPasswordStorageAndValidation() throws Exception {
        String rawPassword = "SecurePassword123";
        Password passwordUtil = new Password(rawPassword);

        // Verify the password is hashed
        assertNotNull(passwordUtil.getHashedPass(), "Hashed password should not be null.");
        assertNotNull(passwordUtil.getSalt(), "Salt should not be null.");

        // Verify hashed password is not the same as the plaintext password
        String hashedPasswordBase64 = Base64.getEncoder().encodeToString(passwordUtil.getHashedPass());
        assertNotEquals(rawPassword, hashedPasswordBase64, "Hashed password should not be retrievable as plaintext.");

        // Validate password using verifyPassword method
        boolean isPasswordValid = Password.verifyPassword(
            rawPassword,
            passwordUtil.getSalt(),
            passwordUtil.getHashedPass()
        );
        assertTrue(isPasswordValid, "Password validation should succeed with correct credentials.");

        // Validate incorrect password
        boolean isPasswordInvalid = Password.verifyPassword(
            "WrongPassword",
            passwordUtil.getSalt(),
            passwordUtil.getHashedPass()
        );
        assertFalse(isPasswordInvalid, "Password validation should fail with incorrect credentials.");
    }
}