package com.reverse.Hack.dto;

import com.reverse.Hack.enums.PostAudience;
import jakarta.validation.constraints.*;

public class PostDTO {

    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 2000)
    private String content;

    private PostAudience audience = PostAudience.PUBLIC;
    private Long circleId;
    private Long communityId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public PostAudience getAudience() { return audience; }
    public void setAudience(PostAudience audience) { this.audience = audience; }
    public Long getCircleId() { return circleId; }
    public void setCircleId(Long circleId) { this.circleId = circleId; }
    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }
}
