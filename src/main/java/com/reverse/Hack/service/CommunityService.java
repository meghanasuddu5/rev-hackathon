package com.reverse.Hack.service;

import com.reverse.Hack.dto.CommunityDTO;
import com.reverse.Hack.enums.InterestCategory;
import com.reverse.Hack.model.Community;
import com.reverse.Hack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommunityService {

    Community createCommunity(User creator, CommunityDTO dto);

    Optional<Community> findById(Long id);

    Page<Community> getPublicCommunities(Pageable pageable);

    Page<Community> searchCommunities(String query, Pageable pageable);

    void joinCommunity(User user, Long communityId);

    void leaveCommunity(User user, Long communityId);

    boolean isMember(User user, Community community);

    List<Community> getCommunitiesForUser(User user);

    long getMemberCount(Community community);
}
