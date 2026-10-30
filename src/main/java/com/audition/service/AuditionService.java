package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private final AuditionIntegrationClient auditionIntegrationClient;

    public AuditionService(final AuditionIntegrationClient auditionIntegrationClient) {
        super();
        this.auditionIntegrationClient = auditionIntegrationClient;
    }

    public List<AuditionPost> getPosts(final String titleFilter) {
        final List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        if (titleFilter == null || titleFilter.isBlank()) {
            return posts;
        }
        final String filter = titleFilter.trim().toLowerCase(Locale.ROOT);
        return posts.stream()
                .filter(post -> post.matchesTitle(filter))
                .toList();
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public AuditionPost getPostWithComments(final String postId) {
        return auditionIntegrationClient.getPostWithComments(postId);
    }

    public List<AuditionComment> getCommentsByPostId(final String postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }
}
