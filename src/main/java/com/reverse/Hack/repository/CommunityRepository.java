package com.reverse.Hack.repository;

import com.reverse.Hack.enums.CommunityVisibility;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    Page<Community> findByVisibilityOrderByCreatedAtDesc(CommunityVisibility visibility, Pageable pageable);

    @Query("SELECT c FROM Community c WHERE c.visibility = 'PUBLIC' AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Community> searchPublicCommunities(@Param("q") String q, Pageable pageable);

    List<Community> findByCategoryAndVisibility(InterestCategory category, CommunityVisibility visibility);
}
