package cse360Project.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*******
 * <p> HelpArticle Class </p>
 * 
 * <p> Description: Object data container class which encapsulates a HelpArticle. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelpArticle {
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
    // Unique ID for each help article
    private Long id;
    private String header;                // Unique header with article level and system info
    private String level;                 // Level: beginner, intermediate, etc.
    private String title;                 // Title of the article
    private String shortDescription;      // Short description/abstract for search and preview
    private Set<String> keywords;         // Set of keywords for easy search
    private String body;                  // Main content of the article
    private List<String> links;           // List of links to reference materials and related articles
    private Set<String> groupIdentifiers; // Set of group names for this article
    private boolean isSensitive;          // Flag for restricted/sensitive access
    private String safeTitle;             // Non-sensitive version of title
    private String safeDescription;       // Non-sensitive version of short description
    
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

    // Constructor
    @JsonCreator
    public HelpArticle(
            @JsonProperty("id") Long id,
            @JsonProperty("header") String header,
            @JsonProperty("level") String level,
            @JsonProperty("title") String title,
            @JsonProperty("shortDescription") String shortDescription,
            @JsonProperty("keywords") Set<String> keywords,
            @JsonProperty("body") String body,
            @JsonProperty("links") List<String> links,
            @JsonProperty("groupIdentifiers") Set<String> groupIdentifiers,
            @JsonProperty("isSensitive") boolean isSensitive,
            @JsonProperty("safeTitle") String safeTitle,
            @JsonProperty("safeDescription") String safeDescription) {
        this.id = id;
        this.header = header;
        this.level = level;
        this.title = title;
        this.shortDescription = shortDescription;
        this.keywords = keywords;
        this.body = body;
        this.links = links;
        this.groupIdentifiers = groupIdentifiers;
        this.isSensitive = isSensitive;
        this.safeTitle = safeTitle;
        this.safeDescription = safeDescription;
    }

	/**********************************************************************************************

	Getters and Setters
	
	**********************************************************************************************/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public Set<String> getGroupIdentifiers() {
        return groupIdentifiers;
    }

    public void setGroupIdentifiers(Set<String> groupIdentifiers) {
        this.groupIdentifiers = groupIdentifiers;
    }

    public boolean isSensitive() {
        return isSensitive;
    }

    public void setSensitive(boolean isSensitive) {
        this.isSensitive = isSensitive;
    }

    public String getSafeTitle() {
        return safeTitle;
    }

    public void setSafeTitle(String safeTitle) {
        this.safeTitle = safeTitle;
    }

    public String getSafeDescription() {
        return safeDescription;
    }

    public void setSafeDescription(String safeDescription) {
        this.safeDescription = safeDescription;
    }

    // Methods for handling keywords and groups
    public void addKeyword(String keyword) {
        this.keywords.add(keyword);
    }

    public void removeKeyword(String keyword) {
        this.keywords.remove(keyword);
    }

    public void addGroupIdentifier(String groupIdentifier) {
        this.groupIdentifiers.add(groupIdentifier);
    }

    public void removeGroupIdentifier(String groupIdentifier) {
        this.groupIdentifiers.remove(groupIdentifier);
    }

    // Display logic for sensitive information
    public String getDisplayTitle() {
        return isSensitive ? safeTitle : title;
    }

    public String getDisplayDescription() {
        return isSensitive ? safeDescription : shortDescription;
    }

    @Override
    public String toString() {
        return "HelpArticle{" +
                "id=" + id +
                ", level='" + level + '\'' +
                ", title='" + getDisplayTitle() + '\'' +
                ", description='" + getDisplayDescription() + '\'' +
                ", keywords=" + keywords +
                '}';
    }
    
	/**********************************************************************************************

	Methods
	
	**********************************************************************************************/
    public List<String> toList() {
        List<String> articleDetails = new ArrayList<>();
        
        // Add fields to the list in the desired order
        articleDetails.add(header);              
        articleDetails.add(title);               
        articleDetails.add(shortDescription);                
        articleDetails.add(level);    
        articleDetails.add(String.join(", ", keywords)); 
        articleDetails.add(body);                
        articleDetails.add(String.join(", ", links)); 
        articleDetails.add(String.join(", ", groupIdentifiers)); 
        articleDetails.add(String.valueOf(isSensitive)); 
        articleDetails.add(safeTitle);           
        articleDetails.add(safeDescription);     

        return articleDetails;
    }


}