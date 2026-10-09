package com.reverse.Hack.repository;

import com.reverse.Hack.model.Community;
import com.reverse.Hack.model.CommunityMembership;
import com.reverse.Hack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, Long> {

    Optional<CommunityMembership> findByCommunityAndUser(Community community, User user);

    boolean existsByCommunityAndUser(Community community, User user);

    List<CommunityMembership> findByCommunity(Community community);

    List<CommunityMembership> findByUser(User user);

    void deleteByCommunityAndUser(Community community, User user);

    long countByCommunity(Community community);

    @Query("SELECT cm.community FROM CommunityMembership cm WHERE cm.user = :user")
    List<Community> findCommunitiesByUser(@Param("user") User user);
}
