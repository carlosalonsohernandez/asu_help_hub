package cse360Project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cse360Project.model.HelpArticle;
import cse360Project.repository.HelpArticleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class Testing {
    private Password password;
    private HelpArticle article;
    private Set<String> keywords;
    private Set<String> groupIdentifiers;
    private List<String> links;
    private static Connection connection;
    private HelpArticleRepository repository;
    
    @BeforeAll
    public static void setUpDatabase() throws SQLException {
        // Set up an in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE help_articles (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "header VARCHAR(255), level VARCHAR(50), title VARCHAR(255)," +
                "short_description TEXT, keywords TEXT, body TEXT," +
                "links TEXT, group_identifiers TEXT, is_sensitive BOOLEAN," +
                "safe_title VARCHAR(255), safe_description TEXT" +
                ")");
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        password = new Password("Someone1");

        keywords = new HashSet<>(Set.of("keyword1", "keyword2"));
        groupIdentifiers = new HashSet<>(Set.of("group1", "group2"));
        links = new ArrayList<>(List.of("https://link1.com", "https://link2.com"));
        
        article = new HelpArticle(
            1L,
            "Header Info",
            "Beginner",
            "Title",
            "Short description",
            keywords,
            "Main body content",
            links,
            groupIdentifiers,
            true,
            "Safe Title",
            "Safe Description"
        );
        
        repository = new HelpArticleRepository(connection);
    }
    
    @AfterEach
    public void cleanUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM help_articles");
        }
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
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

    @Test
    public void testHelpArticleConstructorAndGetters() {
        assertEquals(1L, article.getId());
        assertEquals("Header Info", article.getHeader());
        assertEquals("Beginner", article.getLevel());
        assertEquals("Title", article.getTitle());
        assertEquals("Short description", article.getShortDescription());
        assertEquals("Main body content", article.getBody());
        assertEquals(keywords, article.getKeywords());
        assertEquals(links, article.getLinks());
        assertEquals(groupIdentifiers, article.getGroupIdentifiers());
        assertTrue(article.isSensitive());
        assertEquals("Safe Title", article.getSafeTitle());
        assertEquals("Safe Description", article.getSafeDescription());
    }

    @Test
    public void testHelpArticleSetters() {
        article.setId(2L);
        article.setHeader("New Header");
        article.setLevel("Intermediate");
        article.setTitle("New Title");
        article.setShortDescription("New Short Description");
        article.setBody("New Body Content");
        article.setSensitive(false);
        article.setSafeTitle("New Safe Title");
        article.setSafeDescription("New Safe Description");

        assertEquals(2L, article.getId());
        assertEquals("New Header", article.getHeader());
        assertEquals("Intermediate", article.getLevel());
        assertEquals("New Title", article.getTitle());
        assertEquals("New Short Description", article.getShortDescription());
        assertEquals("New Body Content", article.getBody());
        assertFalse(article.isSensitive());
        assertEquals("New Safe Title", article.getSafeTitle());
        assertEquals("New Safe Description", article.getSafeDescription());
    }

    @Test
    public void testAddAndRemoveKeyword() {
        article.addKeyword("keyword3");
        assertTrue(article.getKeywords().contains("keyword3"));

        article.removeKeyword("keyword1");
        assertFalse(article.getKeywords().contains("keyword1"));
    }

    @Test
    public void testAddAndRemoveGroupIdentifier() {
        article.addGroupIdentifier("group3");
        assertTrue(article.getGroupIdentifiers().contains("group3"));

        article.removeGroupIdentifier("group1");
        assertFalse(article.getGroupIdentifiers().contains("group1"));
    }

    @Test
    public void testDisplayTitleAndDescriptionForSensitiveArticle() {
        assertEquals("Safe Title", article.getDisplayTitle());
        assertEquals("Safe Description", article.getDisplayDescription());
    }

    @Test
    public void testDisplayTitleAndDescriptionForNonSensitiveArticle() {
        article.setSensitive(false);
        assertEquals("Title", article.getDisplayTitle());
        assertEquals("Short description", article.getDisplayDescription());
    }
    
    @Test
    public void testCreateArticle() throws SQLException {
        HelpArticle article = createSampleArticle();
        repository.createArticle(article);

        List<HelpArticle> articles = repository.getAllArticles();
        assertEquals(1, articles.size(), "There should be one article in the database");
        assertEquals(article.getHeader(), articles.get(0).getHeader(), "Headers should match");
    }

    @Test
    public void testGetAllArticles() throws SQLException {
        repository.createArticle(createSampleArticle());
        repository.createArticle(createSampleArticle("Header2", "Another Article"));

        List<HelpArticle> articles = repository.getAllArticles();
        assertEquals(2, articles.size(), "There should be two articles in the database");
    }


    @Test
    public void testSearchArticles() throws SQLException {
        repository.createArticle(createSampleArticle("Header1", "Test Title"));
        repository.createArticle(createSampleArticle("Header2", "Another Test Title"));

        List<HelpArticle> results = repository.searchArticles("Test");
        assertEquals(2, results.size(), "Both articles should be found with 'Test' in the title");
    }

    @Test
    public void testBackupAndRestoreArticles() throws Exception {
        HelpArticle article1 = createSampleArticle("Header1", "First Article");
        HelpArticle article2 = createSampleArticle("Header2", "Second Article");

        List<HelpArticle> articles = List.of(article1, article2);
        String backupFilePath = "articles_backup.json";
        repository.backupArticles(backupFilePath, articles);

        // Clear articles from the database
        repository.deleteArticle(1);
        repository.deleteArticle(2);

        repository.restoreArticles(backupFilePath, true);

        List<HelpArticle> restoredArticles = repository.getAllArticles();
        assertEquals(2, restoredArticles.size(), "Both articles should be restored");
    }

    private HelpArticle createSampleArticle() {
        return createSampleArticle("SampleHeader", "Sample Article Title");
    }

    private HelpArticle createSampleArticle(String header, String title) {
        return new HelpArticle(
            0L, header, "Intro", title, "A short description",
            new HashSet<>(Arrays.asList("Java", "Help")),
            "This is the body of the help article.",
            Arrays.asList("https://example.com"),
            new HashSet<>(Arrays.asList("Group1", "Group2")),
            false, "Safe Title", "Safe Description"
        );
    }
}