package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.PostDTO;
import com.reverse.Hack.model.*;
import com.reverse.Hack.repository.*;
import com.reverse.Hack.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CircleRepository circleRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,
                           ReactionRepository reactionRepository,
                           BookmarkRepository bookmarkRepository,
                           CircleRepository circleRepository,
                           CommunityRepository communityRepository,
                           UserRepository userRepository) {
        this.postRepository = postRepository;
        this.reactionRepository = reactionRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.circleRepository = circleRepository;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post createPost(User author, PostDTO dto, MultipartFile image) {
        Post post = Post.builder()
                .author(author)
                .content(dto.getContent())
                .audience(dto.getAudience())
                .build();

        if (dto.getCircleId() != null) {
            circleRepository.findById(dto.getCircleId()).ifPresent(post::setCircle);
        }
        if (dto.getCommunityId() != null) {
            communityRepository.findById(dto.getCommunityId()).ifPresent(post::setCommunity);
        }
        if (image != null && !image.isEmpty()) {
            String url = saveImage(image);
            post.setImageUrl(url);
        }
        return postRepository.save(post);
    }

    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = "uploads/posts/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/" + uploadDir + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public Post updatePost(User user, Long postId, PostDTO dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized");
        }
        post.setContent(dto.getContent());
        post.setAudience(dto.getAudience());
        return postRepository.save(post);
    }

    @Override
    public void deletePost(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().getId().equals(user.getId()) && user.getRole() != com.reverse.Hack.enums.Role.ADMIN) {
            throw new SecurityException("Not authorized");
        }
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id).filter(p -> !p.isDeleted());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPublicFeed(Pageable pageable) {
        return postRepository.findPublicFeed(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getFeedForUser(User user, Pageable pageable) {
        User fresh = userRepository.findById(user.getId()).orElse(user);
        List<User> following = fresh.getFollowing().stream().toList();
        return postRepository.findFeedForUser(fresh, following, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPostsByUser(User user, Pageable pageable) {
        return postRepository.findByAuthorAndDeletedFalseOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getCommunityPosts(Long communityId, Pageable pageable) {
        return postRepository.findByCommunityId(communityId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.searchPublicPosts(query, pageable);
    }

    @Override
    public void toggleLike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (reactionRepository.existsByPostAndUser(post, user)) {
            reactionRepository.deleteByPostAndUser(post, user);
        } else {
            reactionRepository.save(Reaction.builder().post(post).user(user).build());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLiked(User user, Post post) {
        return reactionRepository.existsByPostAndUser(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Post post) {
        return reactionRepository.countByPost(post);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBookmarked(User user, Post post) {
        return bookmarkRepository.existsByPostAndUser(post, user);
    }

    @Override
    public void toggleBookmark(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (bookmarkRepository.existsByPostAndUser(post, user)) {
            bookmarkRepository.deleteByPostAndUser(post, user);
        } else {
            bookmarkRepository.save(Bookmark.builder().post(post).user(user).build());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getBookmarks(User user, Pageable pageable) {
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(Bookmark::getPost);
    }
}
