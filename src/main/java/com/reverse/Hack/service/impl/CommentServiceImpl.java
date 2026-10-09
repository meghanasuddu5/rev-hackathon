package com.reverse.Hack.service.impl;

import com.reverse.Hack.model.Comment;
import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.CommentRepository;
import com.reverse.Hack.repository.PostRepository;
import com.reverse.Hack.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment addComment(User author, Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Comment comment = Comment.builder().author(author).post(post).content(content).build();
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getAuthor().getId().equals(user.getId()) && user.getRole() != com.reverse.Hack.enums.Role.ADMIN) {
            throw new SecurityException("Not authorized");
        }
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsForPost(Post post) {
        return commentRepository.findByPostAndDeletedFalseOrderByCreatedAtAsc(post);
    }
}
