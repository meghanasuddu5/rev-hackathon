package com.reverse.Hack.service;

import com.reverse.Hack.model.Comment;
import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.User;

import java.util.List;

public interface CommentService {

    Comment addComment(User author, Long postId, String content);

    void deleteComment(User user, Long commentId);

    List<Comment> getCommentsForPost(Post post);
}
