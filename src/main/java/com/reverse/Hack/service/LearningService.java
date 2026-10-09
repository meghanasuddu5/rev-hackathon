package com.reverse.Hack.service;

import com.reverse.Hack.dto.RecommendationDTO;
import com.reverse.Hack.model.LearningResource;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LearningService {

    Page<LearningResource> getAllResources(Pageable pageable);

    Page<LearningResource> search(String query, Pageable pageable);

    List<RecommendationDTO<LearningResource>> getRecommendedResources(User user);
}
