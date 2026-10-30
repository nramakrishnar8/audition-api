package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.support.TestProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {

    private final AuditionIntegrationClient integrationClient;
    private final AuditionService service;

    AuditionServiceTest(@Mock final AuditionIntegrationClient integrationClient) {
        super();
        this.integrationClient = integrationClient;
        this.service = new AuditionService(integrationClient);
    }

    @Test
    void testGetPostsSuccess() {
        final List<AuditionPost> expected = List.of(new AuditionPost());
        when(integrationClient.getPosts()).thenReturn(expected);
        assertSame(expected, service.getPosts(null));
        verify(integrationClient).getPosts();
    }

    @Test
    void testGetPostsFiltersByTitleIgnoringCaseAndBlanks() {
        when(integrationClient.getPosts()).thenReturn(List.of(
                TestProperties.post(1, "Spring Boot"),
                TestProperties.post(2, "Java"),
                TestProperties.post(3, null)));

        final List<AuditionPost> filtered = service.getPosts(" spring ");

        assertEquals(1, filtered.size());
        assertEquals(1, filtered.get(0).getId());
        assertEquals(3, service.getPosts("   ").size());
    }

    @Test
    void testGetPostByIdSuccess() {
        final AuditionPost expected = new AuditionPost();
        when(integrationClient.getPostById("4")).thenReturn(expected);
        assertSame(expected, service.getPostById("4"));
        verify(integrationClient).getPostById("4");
    }

    @Test
    void testGetPostWithCommentsSuccess() {
        final AuditionPost expected = new AuditionPost();
        when(integrationClient.getPostWithComments("4")).thenReturn(expected);
        assertSame(expected, service.getPostWithComments("4"));
        verify(integrationClient).getPostWithComments("4");
    }

    @Test
    void testGetCommentsByPostIdSuccess() {
        final List<AuditionComment> expected = List.of(new AuditionComment());
        when(integrationClient.getCommentsByPostId("4")).thenReturn(expected);
        assertSame(expected, service.getCommentsByPostId("4"));
        verify(integrationClient).getCommentsByPostId("4");
    }
}
