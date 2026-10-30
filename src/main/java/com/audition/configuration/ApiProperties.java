package com.audition.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "api")
@Validated
public class ApiProperties {

    private final String baseUrl;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final int titleFilterMaxLength;
    private final QueryParam queryParam;
    private final Endpoints endpoints;

    public ApiProperties(
            @NotBlank final String baseUrl,
            @NotNull final Duration connectTimeout,
            @NotNull final Duration readTimeout,
            @Positive final int titleFilterMaxLength,
            @Valid @NotNull final QueryParam queryParam,
            @Valid @NotNull final Endpoints endpoints) {
        super();
        this.baseUrl = requireText(baseUrl, "api.base-url");
        this.connectTimeout = requireValue(connectTimeout, "api.connect-timeout");
        this.readTimeout = requireValue(readTimeout, "api.read-timeout");
        if (titleFilterMaxLength <= 0) {
            throw new IllegalArgumentException("api.title-filter-max-length must be positive");
        }
        this.titleFilterMaxLength = titleFilterMaxLength;
        this.queryParam = requireValue(queryParam, "api.query-param");
        this.endpoints = requireValue(endpoints, "api.endpoints");
    }

    public int connectTimeoutMillis() {
        return Math.toIntExact(connectTimeout.toMillis());
    }

    public int readTimeoutMillis() {
        return Math.toIntExact(readTimeout.toMillis());
    }

    public boolean isTitleFilterTooLong(final String titleFilter) {
        return titleFilter != null && titleFilter.length() > titleFilterMaxLength;
    }

    public int maxTitleFilterLength() {
        return titleFilterMaxLength;
    }

    public String postsUrl() {
        return baseUrl + endpoints.posts;
    }

    public String postByIdUrl() {
        return baseUrl + endpoints.postById;
    }

    public String commentsUrl(final String postId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(endpoints.comments)
                .queryParam(queryParam.postId, postId)
                .toUriString();
    }

    @Validated
    public static final class QueryParam {

        private final String postId;

        public QueryParam(@NotBlank final String postId) {
            super();
            this.postId = requireText(postId, "api.query-param.post-id");
        }
    }

    @Validated
    public static final class Endpoints {

        private final String posts;
        private final String postById;
        private final String comments;

        public Endpoints(
                @NotBlank final String posts,
                @NotBlank final String postById,
                @NotBlank final String comments) {
            super();
            this.posts = requireText(posts, "api.endpoints.posts");
            this.postById = requireText(postById, "api.endpoints.post-by-id");
            this.comments = requireText(comments, "api.endpoints.comments");
        }
    }

    static String requireText(final String value, final String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(propertyName + " must be provided");
        }
        return value;
    }

    static <T> T requireValue(final T value, final String propertyName) {
        if (value == null) {
            throw new IllegalArgumentException(propertyName + " must be provided");
        }
        return value;
    }
}
