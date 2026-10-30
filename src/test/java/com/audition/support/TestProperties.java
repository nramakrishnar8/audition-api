package com.audition.support;

import com.audition.configuration.ApiProperties;
import com.audition.configuration.AppProperties;
import com.audition.configuration.MessageProperties;
import com.audition.model.AuditionPost;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

public final class TestProperties {

    private static final Properties APPLICATION = loadApplicationProperties();

    public static final String BASE_URL = required("api.base-url");
    public static final String POSTS_PATH = required("api.endpoints.posts");
    public static final String POST_BY_ID_PATH = required("api.endpoints.post-by-id");
    public static final String COMMENTS_PATH = required("api.endpoints.comments");
    public static final String POST_WITH_COMMENTS_PATH = required("api.endpoints.post-with-comments");
    public static final String POST_COMMENTS_PATH = required("api.endpoints.post-comments");
    public static final String POST_ID_QUERY_PARAM = required("api.query-param.post-id");
    public static final String TITLE_QUERY_PARAM = required("api.query-param.title");
    public static final String PATH_VARIABLE_ID = "id";
    public static final String POSTS_URL = BASE_URL + POSTS_PATH;
    public static final String POST_BY_ID_URL = BASE_URL + POST_BY_ID_PATH;
    public static final String TRACE_ID_HEADER = required("app.headers.trace-id");
    public static final String SPAN_ID_HEADER = required("app.headers.span-id");
    public static final String DATE_FORMAT = required("app.jackson.date-format");
    public static final String INVALID_REQUEST_TITLE = required("messages.invalid-request-title");
    public static final String JSON_TITLE = "$.title";
    public static final String EXAMPLE_POSTS_URI = "https://example.com/posts";
    public static final int TITLE_FILTER_MAX_LENGTH =
            Integer.parseInt(required("api.title-filter-max-length"));

    private TestProperties() {
        super();
    }

    public static String expandId(final String template, final String postId) {
        return template.replace("{" + PATH_VARIABLE_ID + "}", postId);
    }

    public static AuditionPost post(final int id, final String title) {
        final AuditionPost post = new AuditionPost();
        post.setId(id);
        post.setTitle(title);
        return post;
    }

    public static ApiProperties apiProperties() {
        return apiProperties(Duration.ofSeconds(5), Duration.ofSeconds(5));
    }

    public static ApiProperties apiProperties(final Duration connectTimeout, final Duration readTimeout) {
        return new ApiProperties(
                BASE_URL,
                connectTimeout,
                readTimeout,
                TITLE_FILTER_MAX_LENGTH,
                new ApiProperties.QueryParam(POST_ID_QUERY_PARAM),
                new ApiProperties.Endpoints(
                        POSTS_PATH,
                        POST_BY_ID_PATH,
                        COMMENTS_PATH));
    }

    public static AppProperties appProperties() {
        return new AppProperties(
                new AppProperties.Headers(TRACE_ID_HEADER, SPAN_ID_HEADER),
                new AppProperties.Jackson(DATE_FORMAT));
    }

    public static MessageProperties messageProperties() {
        final MessageProperties messages = new MessageProperties();
        messages.setDefaultTitle(required("messages.default-title"));
        messages.setDefaultDetail(required("messages.default-detail"));
        messages.setInvalidRequestTitle(required("messages.invalid-request-title"));
        messages.setResourceNotFoundTitle(required("messages.resource-not-found-title"));
        messages.setUpstreamServiceTitle(required("messages.upstream-service-title"));
        messages.setPostIdPositive(required("messages.post-id-positive"));
        messages.setPostNotFound(required("messages.post-not-found"));
        messages.setRetrievePostsFailed(required("messages.retrieve-posts-failed"));
        messages.setRetrievePostFailed(required("messages.retrieve-post-failed"));
        messages.setRetrieveCommentsFailed(required("messages.retrieve-comments-failed"));
        messages.setUpstreamRetrievePostFailed(required("messages.upstream-retrieve-post-failed"));
        messages.setInvalidStatusCode(required("messages.invalid-status-code"));
        messages.setTitleFilterTooLong(required("messages.title-filter-too-long"));
        messages.setProblemDetail(required("messages.problem-detail"));
        return messages;
    }

    private static String required(final String propertyName) {
        final String value = APPLICATION.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " must be provided");
        }
        return value;
    }

    private static Properties loadApplicationProperties() {
        final Properties properties = new Properties();
        try (InputStream inputStream = TestProperties.class.getResourceAsStream("/application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("application.properties must be on the test classpath");
            }
            properties.load(inputStream);
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return properties;
    }
}
