package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.support.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

class AuditionIntegrationClientTest {

    private final RestTemplate restTemplate;
    private final AuditionIntegrationClient client;

    AuditionIntegrationClientTest() {
        super();
        this.restTemplate = mock(RestTemplate.class);
        this.client = new AuditionIntegrationClient(
                restTemplate,
                TestProperties.apiProperties(),
                TestProperties.messageProperties(),
                new AuditionLogger(TestProperties.messageProperties()));
    }

    @Test
    void testGetPostsNullBody() {
        when(restTemplate.getForObject(
                TestProperties.POSTS_URL, AuditionPost[].class))
                .thenReturn(null);

        assertEquals(0, client.getPosts().size());
    }

    @Test
    void testGetPostByIdSuccess() {
        final AuditionPost post = new AuditionPost();
        post.setId(7);

        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "7"))
                .thenReturn(post);

        assertEquals(7, client.getPostById("7").getId());
    }

    @Test
    void testGetPostByIdNotFound() {
        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "404"))
                .thenThrow(
                        new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final SystemException exception = assertThrows(
                SystemException.class,
                () -> client.getPostById("404"));

        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetPostByIdUpstreamError() {
        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "1"))
                .thenThrow(
                        new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        final SystemException exception = assertThrows(
                SystemException.class,
                () -> client.getPostById("1"));

        assertEquals(502, exception.getStatusCode());
    }

    @Test
    void testGetCommentsNullBody() {
        when(restTemplate.getForObject(
                any(String.class), eq(AuditionComment[].class)))
                .thenReturn(null);

        assertEquals(0, client.getCommentsByPostId("1").size());
    }

    @Test
    void testGetPostsError() {
        when(restTemplate.getForObject(
                TestProperties.POSTS_URL, AuditionPost[].class))
                .thenThrow(new ResourceAccessException("offline"));

        final SystemException exception = assertThrows(
                SystemException.class,
                client::getPosts);

        assertEquals(502, exception.getStatusCode());
    }

    @Test
    void testGetPostByIdNullBody() {
        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "88"))
                .thenReturn(null);

        final SystemException exception = assertThrows(
                SystemException.class,
                () -> client.getPostById("88"));

        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testGetPostByIdRestClientError() {
        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "1"))
                .thenThrow(new ResourceAccessException("offline"));

        final SystemException exception = assertThrows(
                SystemException.class,
                () -> client.getPostById("1"));

        assertEquals(502, exception.getStatusCode());
    }

    @Test
    void testGetCommentsError() {
        when(restTemplate.getForObject(
                any(String.class), eq(AuditionComment[].class)))
                .thenThrow(new ResourceAccessException("offline"));

        final SystemException exception = assertThrows(
                SystemException.class,
                () -> client.getCommentsByPostId("1"));

        assertEquals(502, exception.getStatusCode());
    }

    @Test
    void testGetPostWithCommentsSuccess() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);

        when(restTemplate.getForObject(
                TestProperties.POST_BY_ID_URL, AuditionPost.class, "1"))
                .thenReturn(post);

        when(restTemplate.getForObject(
                any(String.class), eq(AuditionComment[].class)))
                .thenReturn(new AuditionComment[] {new AuditionComment()});

        assertEquals(
                1,
                client.getPostWithComments("1").getComments().size());
    }
}
