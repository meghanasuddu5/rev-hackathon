package com.reverse.Hack.repository;

import com.reverse.Hack.model.Post;
import com.reverse.Hack.model.Reaction;
import com.reverse.Hack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    long countByPost(Post post);

    void deleteByPostAndUser(Post post, User user);
}
