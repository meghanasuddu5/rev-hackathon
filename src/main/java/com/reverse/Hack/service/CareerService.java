package com.reverse.Hack.service;

import com.reverse.Hack.dto.RecommendationDTO;
import com.reverse.Hack.dto.ResumeProfileDTO;
import com.reverse.Hack.model.JobListing;
import com.reverse.Hack.model.ResumeProfile;
import com.reverse.Hack.model.User;
import com.reverse.Hack.model.UserSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CareerService {

    // Jobs
    Page<JobListing> getAllJobs(Pageable pageable);
    Page<JobListing> searchJobs(String query, Pageable pageable);
    List<RecommendationDTO<JobListing>> getRecommendedJobs(User user);

    // Resume
    Optional<ResumeProfile> getResumeProfile(User user);
    ResumeProfile saveResumeProfile(User user, ResumeProfileDTO dto);
    ResumeProfile analyzeResume(User user);

    // Skills
    List<UserSkill> getSkillsForUser(User user);
    UserSkill addOrUpdateSkill(User user, String skillName, String level, int progress);
    void deleteSkill(User user, Long skillId);
    void endorseSkill(Long skillId);
}
