package com.reverse.Hack.repository;

import com.reverse.Hack.model.Bookmark;
import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    Page<Bookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    void deleteByPostAndUser(Post post, User user);
}
