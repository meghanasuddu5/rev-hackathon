package com.reverse.Hack.repository;

import com.reverse.Hack.enums.PostAudience;
import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByAuthorAndDeletedFalseOrderByCreatedAtDesc(User author, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.audience = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findPublicFeed(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND " +
           "(p.audience = 'PUBLIC' OR (p.audience = 'FOLLOWERS' AND p.author IN :following) OR p.author = :user) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findFeedForUser(@Param("user") User user, @Param("following") List<User> following, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.community.id = :communityId AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> findByCommunityId(@Param("communityId") Long communityId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.audience = 'PUBLIC' AND " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchPublicPosts(@Param("q") String q, Pageable pageable);

    long countByAuthorAndDeletedFalse(User author);
}
