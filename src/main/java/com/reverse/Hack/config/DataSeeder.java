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

import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final JobListingRepository jobListingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PostRepository postRepository,
                      CommunityRepository communityRepository,
                      CommunityMembershipRepository membershipRepository,
                      LearningResourceRepository learningResourceRepository,
                      JobListingRepository jobListingRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.jobListingRepository = jobListingRepository;
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

        // ── Learning Resources ────────────────────────────────
        learningResourceRepository.save(LearningResource.builder()
                .title("The Complete Web Developer Bootcamp").provider("Udemy")
                .description("Learn HTML, CSS, JavaScript, React, Node.js and more in one complete course.")
                .url("https://www.udemy.com/course/the-complete-web-development-bootcamp/")
                .resourceType("COURSE").difficulty("BEGINNER").category(InterestCategory.TECHNOLOGY)
                .skills(List.of("HTML", "CSS", "JavaScript", "React", "Node.js"))
                .durationMinutes(4320).rating(4.7).free(false).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Machine Learning by Andrew Ng").provider("Coursera")
                .description("A broad introduction to machine learning, data mining, and statistical pattern recognition.")
                .url("https://www.coursera.org/learn/machine-learning").resourceType("COURSE")
                .difficulty("INTERMEDIATE").category(InterestCategory.TECHNOLOGY)
                .skills(List.of("Python", "Machine Learning", "Linear Algebra", "Statistics"))
                .durationMinutes(3360).rating(4.9).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Introduction to Data Science with Python").provider("edX")
                .description("Learn data science fundamentals, exploratory data analysis, and visualization.")
                .url("https://www.edx.org/course/introduction-to-data-science-in-python")
                .resourceType("COURSE").difficulty("BEGINNER").category(InterestCategory.SCIENCE)
                .skills(List.of("Python", "Pandas", "NumPy", "Data Visualization"))
                .durationMinutes(1800).rating(4.6).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Design Thinking Fundamentals").provider("IDEO U")
                .description("Learn how to apply human-centered design thinking to solve complex problems.")
                .url("https://www.ideou.com/courses/design-thinking-certificate")
                .resourceType("COURSE").difficulty("BEGINNER").category(InterestCategory.ARTS)
                .skills(List.of("Design Thinking", "Problem Solving", "Prototyping", "User Research"))
                .durationMinutes(720).rating(4.5).free(false).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Photography Masterclass").provider("Udemy")
                .description("A complete guide to photography — from basics to professional techniques.")
                .url("https://www.udemy.com/course/photography-masterclass-complete-guide-to-photography/")
                .resourceType("COURSE").difficulty("BEGINNER").category(InterestCategory.ARTS)
                .skills(List.of("Photography", "Lightroom", "Composition", "Portrait Photography"))
                .durationMinutes(2100).rating(4.6).free(false).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Digital Marketing Fundamentals").provider("Google")
                .description("Get started with digital marketing from Google's official free certification course.")
                .url("https://learndigital.withgoogle.com/digitalgarage").resourceType("COURSE")
                .difficulty("BEGINNER").category(InterestCategory.BUSINESS)
                .skills(List.of("SEO", "Social Media Marketing", "Analytics", "Content Marketing"))
                .durationMinutes(1440).rating(4.4).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Spring Boot in Practice").provider("Manning")
                .description("Build enterprise-grade Spring Boot applications from scratch with best practices.")
                .url("https://www.manning.com/books/spring-boot-in-practice")
                .resourceType("BOOK").difficulty("INTERMEDIATE").category(InterestCategory.TECHNOLOGY)
                .skills(List.of("Java", "Spring Boot", "REST API", "Microservices"))
                .durationMinutes(600).rating(4.8).free(false).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("The Science of Well-Being").provider("Coursera / Yale")
                .description("Yale's most popular course ever — learn psychology of happiness and how to change habits.")
                .url("https://www.coursera.org/learn/the-science-of-well-being")
                .resourceType("COURSE").difficulty("BEGINNER").category(InterestCategory.HEALTH)
                .skills(List.of("Positive Psychology", "Habit Formation", "Mindfulness", "Well-Being"))
                .durationMinutes(1200).rating(4.9).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Game Development with Unity").provider("Unity Learn")
                .description("Official Unity tutorials — create 2D and 3D games from beginner to advanced.")
                .url("https://learn.unity.com").resourceType("COURSE")
                .difficulty("BEGINNER").category(InterestCategory.GAMING)
                .skills(List.of("Unity", "C#", "Game Design", "3D Modeling"))
                .durationMinutes(2880).rating(4.7).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Financial Planning and Analysis").provider("CFI")
                .description("Master financial modeling, budgeting, and business analysis for corporate finance.")
                .url("https://corporatefinanceinstitute.com/courses/")
                .resourceType("COURSE").difficulty("INTERMEDIATE").category(InterestCategory.BUSINESS)
                .skills(List.of("Financial Modeling", "Excel", "Budgeting", "Forecasting"))
                .durationMinutes(1560).rating(4.5).free(false).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Sustainable Travel Guide 2024").provider("Article / Green Traveler")
                .description("How to travel responsibly — tips for eco-friendly journeys around the world.")
                .url("https://www.sustainabletravel.org").resourceType("ARTICLE")
                .difficulty("BEGINNER").category(InterestCategory.TRAVEL)
                .skills(List.of("Sustainable Tourism", "Trip Planning", "Carbon Offset"))
                .durationMinutes(30).rating(4.3).free(true).build());

        learningResourceRepository.save(LearningResource.builder()
                .title("Advanced React Patterns").provider("Kent C. Dodds / Epic React")
                .description("Master advanced React patterns: compound components, hooks, context, and performance.")
                .url("https://epicreact.dev").resourceType("COURSE")
                .difficulty("ADVANCED").category(InterestCategory.TECHNOLOGY)
                .skills(List.of("React", "JavaScript", "Hooks", "Performance Optimization"))
                .durationMinutes(1800).rating(4.9).free(false).build());

        // ── Job Listings ──────────────────────────────────────
        jobListingRepository.save(JobListing.builder()
                .title("Software Engineer — Full Stack").company("Google")
                .location("Bangalore, India").jobType("FULL_TIME")
                .description("Build scalable web applications using Java, Python and React. Work on products used by billions.")
                .category(InterestCategory.TECHNOLOGY)
                .requiredSkills(List.of("Java", "React", "Python", "SQL", "System Design"))
                .experienceLevel("MID").salaryRange("₹25L – ₹45L/year")
                .applyUrl("https://careers.google.com").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Data Scientist").company("Amazon")
                .location("Hyderabad, India").jobType("FULL_TIME")
                .description("Apply ML models to optimize supply chain, pricing, and customer experience at scale.")
                .category(InterestCategory.TECHNOLOGY)
                .requiredSkills(List.of("Python", "Machine Learning", "Statistics", "SQL", "TensorFlow"))
                .experienceLevel("MID").salaryRange("₹20L – ₹38L/year")
                .applyUrl("https://www.amazon.jobs").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("UI/UX Designer").company("Flipkart")
                .location("Bangalore, India").jobType("FULL_TIME")
                .description("Design intuitive user interfaces for India's largest e-commerce platform.")
                .category(InterestCategory.ARTS)
                .requiredSkills(List.of("Figma", "User Research", "Prototyping", "Design Thinking", "CSS"))
                .experienceLevel("MID").salaryRange("₹15L – ₹28L/year")
                .applyUrl("https://www.flipkart.com/careers").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Backend Developer — Spring Boot").company("Infosys")
                .location("Pune, India (Hybrid)").jobType("FULL_TIME")
                .description("Develop enterprise microservices using Spring Boot, Kafka, and AWS.")
                .category(InterestCategory.TECHNOLOGY)
                .requiredSkills(List.of("Java", "Spring Boot", "Microservices", "REST API", "AWS"))
                .experienceLevel("ENTRY").salaryRange("₹8L – ₹16L/year")
                .applyUrl("https://career.infosys.com").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Digital Marketing Manager").company("Zomato")
                .location("Gurgaon, India").jobType("FULL_TIME")
                .description("Lead digital campaigns across social media, SEO, and paid channels to grow brand.")
                .category(InterestCategory.BUSINESS)
                .requiredSkills(List.of("SEO", "Social Media Marketing", "Google Ads", "Analytics", "Content Strategy"))
                .experienceLevel("MID").salaryRange("₹12L – ₹22L/year")
                .applyUrl("https://www.zomato.com/careers").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Game Developer").company("Nazara Technologies")
                .location("Mumbai, India").jobType("FULL_TIME")
                .description("Create mobile and PC games using Unity — from concept to launch.")
                .category(InterestCategory.GAMING)
                .requiredSkills(List.of("Unity", "C#", "Game Design", "3D Modeling", "Physics"))
                .experienceLevel("ENTRY").salaryRange("₹6L – ₹14L/year")
                .applyUrl("https://www.nazara.com/careers").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("ML Engineer — Computer Vision").company("Wipro AI")
                .location("Chennai, India (Remote OK)").jobType("FULL_TIME")
                .description("Build and deploy computer vision models for industrial automation solutions.")
                .category(InterestCategory.TECHNOLOGY)
                .requiredSkills(List.of("Python", "OpenCV", "TensorFlow", "PyTorch", "Docker"))
                .experienceLevel("SENIOR").salaryRange("₹30L – ₹55L/year")
                .applyUrl("https://careers.wipro.com").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Content Writer — Tech & Science").company("Times Internet")
                .location("New Delhi, India (Remote)").jobType("FULL_TIME")
                .description("Write engaging articles on technology and science for 50M+ monthly readers.")
                .category(InterestCategory.SCIENCE)
                .requiredSkills(List.of("Technical Writing", "SEO", "Research", "Editing"))
                .experienceLevel("ENTRY").salaryRange("₹5L – ₹10L/year")
                .applyUrl("https://timesinternet.in/careers").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("Travel Experience Designer").company("MakeMyTrip")
                .location("Gurgaon, India").jobType("FULL_TIME")
                .description("Design curated travel itineraries and experiences for premium customers.")
                .category(InterestCategory.TRAVEL)
                .requiredSkills(List.of("Trip Planning", "Customer Experience", "Communication", "Geography"))
                .experienceLevel("ENTRY").salaryRange("₹4L – ₹8L/year")
                .applyUrl("https://careers.makemytrip.com").active(true).build());

        jobListingRepository.save(JobListing.builder()
                .title("DevOps Engineer").company("Razorpay")
                .location("Bangalore, India").jobType("FULL_TIME")
                .description("Manage CI/CD pipelines, Kubernetes clusters, and cloud infrastructure for fintech at scale.")
                .category(InterestCategory.TECHNOLOGY)
                .requiredSkills(List.of("Kubernetes", "Docker", "AWS", "Terraform", "CI/CD"))
                .experienceLevel("MID").salaryRange("₹18L – ₹35L/year")
                .applyUrl("https://razorpay.com/jobs").active(true).build());

        log.info("Learning resources and job listings seeded.");
    }
}
