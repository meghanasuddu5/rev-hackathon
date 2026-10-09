package com.reverse.Hack.repository;

import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobListingRepository extends JpaRepository<JobListing, Long> {

    Page<JobListing> findByActiveTrue(Pageable pageable);

    List<JobListing> findByCategoryInAndActiveTrue(List<InterestCategory> categories);

    @Query("SELECT j FROM JobListing j WHERE j.active = true AND (" +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<JobListing> search(@Param("q") String q, Pageable pageable);

    Page<JobListing> findByCategoryAndActiveTrue(InterestCategory category, Pageable pageable);
}
