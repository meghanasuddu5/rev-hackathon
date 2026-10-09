package com.reverse.Hack.dto;

import jakarta.validation.constraints.*;

public class CommentDTO {
    @NotBlank @Size(max = 1000) private String content;
    private Long postId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
