package cse360Project;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class Testing {
    private Password password;

    @BeforeEach
    public void setUp() throws Exception {
        password = new Password("Someone1");
    }

    @Test
    public void testHashedPasswordAndSalt() {
        assertNotNull(password.getHashedPass(), "Hashed password should not be null");
        assertNotNull(password.getSalt(), "Salt should not be null");
        assertTrue(password.getHashedPass().length > 0, "Hashed password should have length greater than 0");
        assertTrue(password.getSalt().length > 0, "Salt should have length greater than 0");
    }

    @Test
    public void testVerifyPasswordCorrect() throws Exception {
        assertTrue(password.verifyPassword("Someone1"), "Password verification should succeed for correct password");
    }

    @Test
    public void testVerifyPasswordIncorrect() throws Exception {
        assertFalse(password.verifyPassword("Someone"), "Password verification should fail for incorrect password");
    }

    @Test
    public void testDifferentSaltsProduceDifferentHashes() throws Exception {
        Password anotherPassword = new Password("Someone1");
        assertFalse(Arrays.equals(password.getHashedPass(), anotherPassword.getHashedPass()),
                   "Different salts should produce different hashed passwords");
    }
}