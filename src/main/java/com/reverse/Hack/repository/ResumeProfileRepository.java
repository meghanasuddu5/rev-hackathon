package com.reverse.Hack.repository;

import com.reverse.Hack.model.ResumeProfile;
import com.reverse.Hack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {

    Optional<ResumeProfile> findByUser(User user);
}
