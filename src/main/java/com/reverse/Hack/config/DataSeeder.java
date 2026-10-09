package com.reverse.Hack.config;

import com.reverse.Hack.enums.*;
import com.reverse.Hack.model.*;
import com.reverse.Hack.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PostRepository postRepository,
                      CommunityRepository communityRepository,
                      CommunityMembershipRepository membershipRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        log.info("Seeding demo data...");

        User admin = userRepository.save(User.builder()
                .username("admin").email("admin@googleplus.dev")
                .displayName("Admin").passwordHash(passwordEncoder.encode("Admin@1234"))
                .role(Role.ADMIN).bio("Platform administrator")
                .interests(Set.of(InterestCategory.TECHNOLOGY))
                .onboardingComplete(true).profilePublic(true).build());

        User alice = userRepository.save(User.builder()
                .username("alice").email("alice@example.com")
                .displayName("Alice Johnson").passwordHash(passwordEncoder.encode("Alice@1234"))
                .bio("Software engineer & coffee enthusiast ☕")
                .interests(Set.of(InterestCategory.TECHNOLOGY, InterestCategory.SCIENCE))
                .onboardingComplete(true).profilePublic(true).build());

        User bob = userRepository.save(User.builder()
                .username("bob").email("bob@example.com")
                .displayName("Bob Smith").passwordHash(passwordEncoder.encode("Bob@1234"))
                .bio("Photographer and travel lover 📷")
                .interests(Set.of(InterestCategory.TRAVEL, InterestCategory.ARTS))
                .onboardingComplete(true).profilePublic(true).build());

        User carol = userRepository.save(User.builder()
                .username("carol").email("carol@example.com")
                .displayName("Carol White").passwordHash(passwordEncoder.encode("Carol@1234"))
                .bio("Gamer, streamer, open-source contributor 🎮")
                .interests(Set.of(InterestCategory.GAMING, InterestCategory.TECHNOLOGY))
                .onboardingComplete(true).profilePublic(true).build());

        // Follows
        alice.getFollowing().add(bob);
        alice.getFollowing().add(carol);
        bob.getFollowing().add(alice);
        carol.getFollowing().add(alice);
        userRepository.save(alice);
        userRepository.save(bob);
        userRepository.save(carol);

        // Posts
        postRepository.save(Post.builder().author(alice).audience(PostAudience.PUBLIC)
                .content("Just deployed my first microservice to the cloud! 🚀 #technology #devops").build());
        postRepository.save(Post.builder().author(bob).audience(PostAudience.PUBLIC)
                .content("Captured this stunning sunset at Santorini. Travel truly opens your mind. 🌅 #travel").build());
        postRepository.save(Post.builder().author(carol).audience(PostAudience.PUBLIC)
                .content("Finally beat that impossible boss in Elden Ring after 47 attempts. Never give up! 🎮").build());
        postRepository.save(Post.builder().author(alice).audience(PostAudience.PUBLIC)
                .content("Hot take: Code review is the most underrated skill in software engineering.").build());
        postRepository.save(Post.builder().author(bob).audience(PostAudience.PUBLIC)
                .content("New blog post up: '10 Composition Techniques That Will Transform Your Photography'.").build());

        // Communities
        Community techCommunity = communityRepository.save(Community.builder()
                .name("Tech Enthusiasts").description("For developers, engineers, and tech lovers.")
                .visibility(CommunityVisibility.PUBLIC).category(InterestCategory.TECHNOLOGY).creator(alice).build());
        membershipRepository.save(CommunityMembership.builder().community(techCommunity).user(alice).role(CommunityRole.OWNER).build());
        membershipRepository.save(CommunityMembership.builder().community(techCommunity).user(carol).role(CommunityRole.MEMBER).build());
        membershipRepository.save(CommunityMembership.builder().community(techCommunity).user(admin).role(CommunityRole.MEMBER).build());

        Community travelCommunity = communityRepository.save(Community.builder()
                .name("Wanderlust Travelers").description("Share your travel stories, tips, and photos.")
                .visibility(CommunityVisibility.PUBLIC).category(InterestCategory.TRAVEL).creator(bob).build());
        membershipRepository.save(CommunityMembership.builder().community(travelCommunity).user(bob).role(CommunityRole.OWNER).build());
        membershipRepository.save(CommunityMembership.builder().community(travelCommunity).user(alice).role(CommunityRole.MEMBER).build());

        Community gamingCommunity = communityRepository.save(Community.builder()
                .name("Gamers Unite").description("For passionate gamers — reviews, tips, and everything in between.")
                .visibility(CommunityVisibility.PUBLIC).category(InterestCategory.GAMING).creator(carol).build());
        membershipRepository.save(CommunityMembership.builder().community(gamingCommunity).user(carol).role(CommunityRole.OWNER).build());

        log.info("Demo data seeded. Accounts: alice/Alice@1234, bob/Bob@1234, carol/Carol@1234, admin/Admin@1234");
    }
}
