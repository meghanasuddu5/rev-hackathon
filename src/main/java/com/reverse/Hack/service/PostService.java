package com.reverse.Hack.service;

import com.reverse.Hack.dto.PostDTO;
import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PostService {

    Post createPost(User author, PostDTO dto, MultipartFile image);

    Post updatePost(User user, Long postId, PostDTO dto);

    void deletePost(User user, Long postId);

    Optional<Post> findById(Long id);

    Page<Post> getPublicFeed(Pageable pageable);

    Page<Post> getFeedForUser(User user, Pageable pageable);

    Page<Post> getPostsByUser(User user, Pageable pageable);

    Page<Post> getCommunityPosts(Long communityId, Pageable pageable);

    Page<Post> searchPosts(String query, Pageable pageable);

    void toggleLike(User user, Long postId);

    boolean hasLiked(User user, Post post);

    long getLikeCount(Post post);

    boolean hasBookmarked(User user, Post post);

    void toggleBookmark(User user, Long postId);

    Page<Post> getBookmarks(User user, Pageable pageable);
}
