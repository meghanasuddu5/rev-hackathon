package com.reverse.Hack.repository;

import com.reverse.Hack.model.User;
import com.reverse.Hack.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserOrderByEndorsementsDesc(User user);

    Optional<UserSkill> findByUserAndSkillNameIgnoreCase(User user, String skillName);

    boolean existsByUserAndSkillNameIgnoreCase(User user, String skillName);
}
