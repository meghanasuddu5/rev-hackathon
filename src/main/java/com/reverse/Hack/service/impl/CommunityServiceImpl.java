package com.reverse.Hack.service.impl;

import com.reverse.Hack.dto.CommunityDTO;
import com.reverse.Hack.enums.CommunityRole;
import com.reverse.Hack.enums.CommunityVisibility;
import com.reverse.Hack.model.Community;
import com.reverse.Hack.model.CommunityMembership;
import com.reverse.Hack.model.User;
import com.reverse.Hack.repository.CommunityMembershipRepository;
import com.reverse.Hack.repository.CommunityRepository;
import com.reverse.Hack.service.CommunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;

    public CommunityServiceImpl(CommunityRepository communityRepository,
                                CommunityMembershipRepository membershipRepository) {
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Community createCommunity(User creator, CommunityDTO dto) {
        if (communityRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("A community with that name already exists");
        }
        Community community = Community.builder()
                .name(dto.getName()).description(dto.getDescription())
                .visibility(dto.getVisibility() != null ? dto.getVisibility() : CommunityVisibility.PUBLIC)
                .category(dto.getCategory()).creator(creator).build();
        community = communityRepository.save(community);
        membershipRepository.save(CommunityMembership.builder()
                .community(community).user(creator).role(CommunityRole.OWNER).build());
        return community;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Community> findById(Long id) {
        return communityRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Community> getPublicCommunities(Pageable pageable) {
        return communityRepository.findByVisibilityOrderByCreatedAtDesc(CommunityVisibility.PUBLIC, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Community> searchCommunities(String query, Pageable pageable) {
        return communityRepository.searchPublicCommunities(query, pageable);
    }

    @Override
    public void joinCommunity(User user, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        if (membershipRepository.existsByCommunityAndUser(community, user)) {
            throw new IllegalArgumentException("Already a member");
        }
        membershipRepository.save(CommunityMembership.builder()
                .community(community).user(user).role(CommunityRole.MEMBER).build());
    }

    @Override
    public void leaveCommunity(User user, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        CommunityMembership membership = membershipRepository.findByCommunityAndUser(community, user)
                .orElseThrow(() -> new IllegalArgumentException("Not a member"));
        if (membership.getRole() == CommunityRole.OWNER) {
            throw new IllegalArgumentException("Owner cannot leave. Transfer ownership first.");
        }
        membershipRepository.delete(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(User user, Community community) {
        return membershipRepository.existsByCommunityAndUser(community, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Community> getCommunitiesForUser(User user) {
        return membershipRepository.findCommunitiesByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMemberCount(Community community) {
        return membershipRepository.countByCommunity(community);
    }
}
