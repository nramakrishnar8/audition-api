package com.audition.common.logging;

import com.audition.configuration.MessageProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    private final MessageProperties messageProperties;

    public AuditionLogger(final MessageProperties messageProperties) {
        super();
        this.messageProperties = messageProperties;
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail,
            final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(createStandardProblemDetailMessage(problemDetail), e);
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail problemDetail) {
        return String.format(messageProperties.getProblemDetail(),
                problemDetail.getStatus(), StringUtils.defaultString(problemDetail.getTitle()),
                StringUtils.defaultString(problemDetail.getDetail()));
    }
}
