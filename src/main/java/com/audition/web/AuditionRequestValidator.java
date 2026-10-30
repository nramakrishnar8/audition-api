package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.configuration.ApiProperties;
import com.audition.configuration.MessageProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuditionRequestValidator {

    private final ApiProperties apiProperties;
    private final MessageProperties messageProperties;

    public AuditionRequestValidator(
            final ApiProperties apiProperties,
            final MessageProperties messageProperties) {
        super();
        this.apiProperties = apiProperties;
        this.messageProperties = messageProperties;
    }

    public void validateTitleFilter(final String titleFilter) {
        if (apiProperties.isTitleFilterTooLong(titleFilter)) {
            throw new SystemException(
                    messageProperties.formatTitleFilterTooLong(apiProperties.maxTitleFilterLength()),
                    messageProperties.getInvalidRequestTitle(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    public String requirePostId(final String postId) {
        final String normalized = normalizePostId(postId);
        try {
            if (Integer.parseInt(normalized) <= 0) {
                throw invalidPostId(null);
            }
        } catch (final NumberFormatException exception) {
            throw invalidPostId(exception);
        }
        return normalized;
    }

    private String normalizePostId(final String postId) {
        if (postId == null || postId.isBlank()) {
            throw invalidPostId(null);
        }
        final String normalized = postId.trim();
        if (!normalized.chars().allMatch(Character::isDigit)) {
            throw invalidPostId(null);
        }
        return normalized;
    }

    private SystemException invalidPostId(final NumberFormatException exception) {
        if (exception == null) {
            return new SystemException(
                    messageProperties.getPostIdPositive(),
                    messageProperties.getInvalidRequestTitle(),
                    HttpStatus.BAD_REQUEST.value());
        }
        return new SystemException(
                messageProperties.getPostIdPositive(),
                messageProperties.getInvalidRequestTitle(),
                HttpStatus.BAD_REQUEST.value(),
                exception);
    }
}
