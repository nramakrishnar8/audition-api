package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    private final AuditionLogger logger;
    private final MessageProperties messageProperties;

    public ExceptionControllerAdvice(
            final AuditionLogger logger,
            final MessageProperties messageProperties) {
        super();
        this.logger = logger;
        this.messageProperties = messageProperties;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(
            final HttpClientErrorException exception) {
        final ProblemDetail detail = createProblemDetail(
                messageProperties.getDefaultDetail(),
                exception.getStatusCode(),
                messageProperties.getDefaultTitle());

        logger.logStandardProblemDetail(LOG, detail, exception);
        return detail;
    }

    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException exception) {
        final HttpStatusCode status =
                getHttpStatusCodeFromSystemException(exception);

        final ProblemDetail detail;

        if (status.is5xxServerError()) {
            detail = createProblemDetail(
                    messageProperties.getDefaultDetail(),
                    status,
                    exception.getTitle());
        } else {
            detail = createProblemDetail(
                    exception.getMessage(),
                    status,
                    exception.getTitle());
        }

        logger.logStandardProblemDetail(LOG, detail, exception);
        return detail;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ProblemDetail handleMethodNotSupported(
            final HttpRequestMethodNotSupportedException exception) {
        final ProblemDetail detail =
                createProblemDetail(exception, METHOD_NOT_ALLOWED);

        logger.logStandardProblemDetail(LOG, detail, exception);
        return detail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception exception) {
        final HttpStatusCode status = getHttpStatusCodeFromException(exception);

        final ProblemDetail detail;

        if (status.is5xxServerError()) {
            detail = createProblemDetail(
                    messageProperties.getDefaultDetail(),
                    status,
                    messageProperties.getDefaultTitle());
        } else {
            detail = createProblemDetail(exception, status);
        }

        logger.logStandardProblemDetail(LOG, detail, exception);
        return detail;
    }

    private ProblemDetail createProblemDetail(
            final Exception exception,
            final HttpStatusCode statusCode) {
        return createProblemDetail(
                exception.getMessage(),
                statusCode,
                messageProperties.getDefaultTitle());
    }

    private ProblemDetail createProblemDetail(
            final String message,
            final HttpStatusCode statusCode,
            final String title) {
        final ProblemDetail problemDetail =
                ProblemDetail.forStatus(statusCode);

        problemDetail.setDetail(
                message == null || message.isBlank()
                        ? messageProperties.getDefaultDetail()
                        : message);

        problemDetail.setTitle(
                title == null || title.isBlank()
                        ? messageProperties.getDefaultTitle()
                        : title);

        return problemDetail;
    }

    private HttpStatusCode getHttpStatusCodeFromSystemException(
            final SystemException exception) {
        if (exception.getStatusCode() == null) {
            return INTERNAL_SERVER_ERROR;
        }

        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (final IllegalArgumentException invalidStatus) {
            if (LOG.isWarnEnabled()) {
                logger.warn(LOG, messageProperties.getInvalidStatusCode());
            }
            return INTERNAL_SERVER_ERROR;
        }
    }

    private HttpStatusCode getHttpStatusCodeFromException(
            final Exception exception) {
        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }

        return INTERNAL_SERVER_ERROR;
    }
}
