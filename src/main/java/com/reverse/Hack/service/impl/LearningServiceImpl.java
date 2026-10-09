package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.RecommendationDTO;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.LearningResource;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.LearningResourceRepository;
import com.reverse.Hack.service.LearningService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class LearningServiceImpl implements LearningService {

    private final LearningResourceRepository resourceRepo;

    public LearningServiceImpl(LearningResourceRepository resourceRepo) {
        this.resourceRepo = resourceRepo;
    }

    @Override
    public Page<LearningResource> getAllResources(Pageable pageable) {
        return resourceRepo.findAll(pageable);
    }

    @Override
    public Page<LearningResource> search(String query, Pageable pageable) {
        return resourceRepo.search(query, pageable);
    }

    /**
     * Explainable recommendation engine for learning resources.
     * Generates a reason string alongside each recommendation.
     */
    @Override
    public List<RecommendationDTO<LearningResource>> getRecommendedResources(User user) {
        Set<InterestCategory> interests = user.getInterests();
        List<RecommendationDTO<LearningResource>> results = new ArrayList<>();

        if (interests != null && !interests.isEmpty()) {
            List<InterestCategory> interestList = new ArrayList<>(interests);
            List<LearningResource> matched = resourceRepo.findByCategoryIn(interestList);

            for (LearningResource r : matched) {
                int score = 60;
                String reason;

                // Build explainable reason
                if (r.getRating() >= 4.5) {
                    score += 20;
                    reason = "Matches your interest in " + r.getCategory().name().toLowerCase().replace("_", " ")
                            + " and is highly rated (" + r.getRating() + "★)";
                } else if (r.isFree()) {
                    score += 10;
                    reason = "Free resource that aligns with your " + r.getCategory().name().toLowerCase()
                            + " interest";
                } else {
                    reason = "Recommended based on your interest in "
                            + r.getCategory().name().toLowerCase().replace("_", " ");
                }

                results.add(new RecommendationDTO<>(r, reason, score, "INTEREST_MATCH"));
            }
        }

        // Fill with top-rated if fewer than 6
        if (results.size() < 6) {
            List<LearningResource> topRated = resourceRepo.findTop10ByOrderByRatingDesc();
            Set<Long> alreadyAdded = new HashSet<>();
            results.forEach(r -> alreadyAdded.add(r.getItem().getId()));

            for (LearningResource r : topRated) {
                if (!alreadyAdded.contains(r.getId())) {
                    results.add(new RecommendationDTO<>(r,
                            "Top-rated resource popular across the platform (" + r.getRating() + "★)",
                            40, "POPULAR"));
                    if (results.size() >= 9) break;
                }
            }
        }

        // Sort by score descending, cap at 9
        results.sort(Comparator.<RecommendationDTO<LearningResource>>comparingInt(RecommendationDTO::getScore).reversed());
        return results.size() > 9 ? results.subList(0, 9) : results;
    }
}
