package com.reverse.Hack.repository;

import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.LearningResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    Page<LearningResource> findAll(Pageable pageable);

    List<LearningResource> findByCategoryIn(List<InterestCategory> categories);

    Page<LearningResource> findByCategory(InterestCategory category, Pageable pageable);

    @Query("SELECT r FROM LearningResource r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(r.provider) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<LearningResource> search(@Param("q") String q, Pageable pageable);

    List<LearningResource> findTop10ByOrderByRatingDesc();

    List<LearningResource> findByCategoryAndDifficulty(InterestCategory category, String difficulty);
}
