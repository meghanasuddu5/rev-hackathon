package com.reverse.Hack.repository;

import com.reverse.Hack.model.Comment;
import com.reverse.Hack.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostAndDeletedFalseOrderByCreatedAtAsc(Post post);

    long countByPostAndDeletedFalse(Post post);
}
