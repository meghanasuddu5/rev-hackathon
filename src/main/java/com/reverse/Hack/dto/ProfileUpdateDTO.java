package com.reverse.Hack.dto;

import com.reverse.Hack.enums.InterestCategory;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class ProfileUpdateDTO {
    @Size(min = 2, max = 100) private String displayName;
    @Size(max = 300) private String bio;
    private boolean profilePublic = true;
    private Set<InterestCategory> interests;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public boolean isProfilePublic() { return profilePublic; }
    public void setProfilePublic(boolean profilePublic) { this.profilePublic = profilePublic; }
    public Set<InterestCategory> getInterests() { return interests; }
    public void setInterests(Set<InterestCategory> interests) { this.interests = interests; }
}
