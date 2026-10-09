package com.reverse.Hack.dto;

import com.reverse.Hack.enums.CommunityVisibility;
import com.reverse.Hack.enums.InterestCategory;
import jakarta.validation.constraints.*;

public class CommunityDTO {
    @NotBlank @Size(min = 3, max = 100) private String name;
    @Size(max = 500) private String description;
    private CommunityVisibility visibility = CommunityVisibility.PUBLIC;
    private InterestCategory category;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CommunityVisibility getVisibility() { return visibility; }
    public void setVisibility(CommunityVisibility visibility) { this.visibility = visibility; }
    public InterestCategory getCategory() { return category; }
    public void setCategory(InterestCategory category) { this.category = category; }
}
