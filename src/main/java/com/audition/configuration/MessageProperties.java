package com.audition.configuration;

import jakarta.validation.constraints.NotBlank;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "messages")
@Validated
@Getter
@Setter
public class MessageProperties {

    @NotBlank
    private String defaultTitle;
    @NotBlank
    private String defaultDetail;
    @NotBlank
    private String invalidRequestTitle;
    @NotBlank
    private String resourceNotFoundTitle;
    @NotBlank
    private String upstreamServiceTitle;
    @NotBlank
    private String postIdPositive;
    @NotBlank
    private String postNotFound;
    @NotBlank
    private String retrievePostsFailed;
    @NotBlank
    private String retrievePostFailed;
    @NotBlank
    private String retrieveCommentsFailed;
    @NotBlank
    private String upstreamRetrievePostFailed;
    @NotBlank
    private String invalidStatusCode;
    @NotBlank
    private String titleFilterTooLong;
    @NotBlank
    private String problemDetail;

    public String formatPostNotFound(final String postId) {
        return MessageFormat.format(postNotFound, postId);
    }

    public String formatRetrievePostFailed(final String postId) {
        return MessageFormat.format(retrievePostFailed, postId);
    }

    public String formatRetrieveCommentsFailed(final String postId) {
        return MessageFormat.format(retrieveCommentsFailed, postId);
    }

    public String formatUpstreamRetrievePostFailed(final String postId) {
        return MessageFormat.format(upstreamRetrievePostFailed, postId);
    }

    public String formatTitleFilterTooLong(final int maxLength) {
        return MessageFormat.format(titleFilterTooLong, maxLength);
    }
}
