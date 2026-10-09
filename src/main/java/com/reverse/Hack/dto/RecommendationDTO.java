package com.reverse.Hack.dto;

/**
 * Wraps any recommended item with an explainability reason.
 * The 'item' is the actual object (LearningResource, JobListing, User, etc.)
 * 'reason' is the human-readable explanation of why it was recommended.
 * 'score' is a normalized relevance score 0-100 (used for sorting).
 */
public class RecommendationDTO<T> {

    private final T item;
    private final String reason;
    private final int score;
    private final String matchType; // "INTEREST_MATCH", "SKILL_MATCH", "COMMUNITY_MATCH", "POPULAR"

    public RecommendationDTO(T item, String reason, int score, String matchType) {
        this.item = item;
        this.reason = reason;
        this.score = score;
        this.matchType = matchType;
    }

    public T getItem() { return item; }
    public String getReason() { return reason; }
    public int getScore() { return score; }
    public String getMatchType() { return matchType; }
}
