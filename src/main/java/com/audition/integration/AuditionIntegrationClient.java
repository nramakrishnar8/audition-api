package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.ApiProperties;
import com.audition.configuration.MessageProperties;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionIntegrationClient.class);
    private static final String CALLING_POST = "Calling upstream API for post ID: {}";

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;
    private final MessageProperties messageProperties;
    private final AuditionLogger auditionLogger;

    public AuditionIntegrationClient(
            final RestTemplate restTemplate,
            final ApiProperties apiProperties,
            final MessageProperties messageProperties,
            final AuditionLogger auditionLogger) {
        super();
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
        this.messageProperties = messageProperties;
        this.auditionLogger = auditionLogger;
    }

    public List<AuditionPost> getPosts() {
        try {
            final AuditionPost[] posts = restTemplate.getForObject(
                    apiProperties.postsUrl(), AuditionPost[].class);
            return posts == null ? List.of() : Arrays.asList(posts);
        } catch (final RestClientException exception) {
            auditionLogger.logErrorWithException(
                    LOG, messageProperties.getRetrievePostsFailed(), exception);
            throw new SystemException(
                    messageProperties.getRetrievePostsFailed(),
                    messageProperties.getUpstreamServiceTitle(),
                    HttpStatus.BAD_GATEWAY.value(),
                    exception);
        }
    }

    public AuditionPost getPostById(final String id) {
        try {
            auditionLogger.info(LOG, CALLING_POST, id);

            final AuditionPost post = restTemplate.getForObject(
                    apiProperties.postByIdUrl(), AuditionPost.class, id);
            if (post == null) {
                throw notFound(id, null);
            }
            return post;
        } catch (final HttpStatusCodeException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw notFound(id, exception);
            }
            throw new SystemException(
                    messageProperties.formatUpstreamRetrievePostFailed(id),
                    messageProperties.getUpstreamServiceTitle(),
                    HttpStatus.BAD_GATEWAY.value(),
                    exception);
        } catch (final RestClientException exception) {
            throw new SystemException(
                    messageProperties.formatRetrievePostFailed(id),
                    messageProperties.getUpstreamServiceTitle(),
                    HttpStatus.BAD_GATEWAY.value(),
                    exception);
        }
    }

    public AuditionPost getPostWithComments(final String id) {
        final AuditionPost post = getPostById(id);
        post.attachComments(getCommentsByPostId(id));
        return post;
    }

    public List<AuditionComment> getCommentsByPostId(final String postId) {
        final String uri = apiProperties.commentsUrl(postId);
        try {
            final AuditionComment[] comments =
                    restTemplate.getForObject(uri, AuditionComment[].class);
            return comments == null ? List.of() : Arrays.asList(comments);
        } catch (final RestClientException exception) {
            throw new SystemException(
                    messageProperties.formatRetrieveCommentsFailed(postId),
                    messageProperties.getUpstreamServiceTitle(),
                    HttpStatus.BAD_GATEWAY.value(),
                    exception);
        }
    }

    private SystemException notFound(final String id, final HttpStatusCodeException exception) {
        if (exception == null) {
            return new SystemException(
                    messageProperties.formatPostNotFound(id),
                    messageProperties.getResourceNotFoundTitle(),
                    HttpStatus.NOT_FOUND.value());
        }
        return new SystemException(
                messageProperties.formatPostNotFound(id),
                messageProperties.getResourceNotFoundTitle(),
                HttpStatus.NOT_FOUND.value(),
                exception);
    }
}
