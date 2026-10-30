package com.audition.common.logging;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.support.TestProperties;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class AuditionLoggerTest {

    private static final String VALUE_TEMPLATE = "Value: {}";
    private static final String WARNING_MESSAGE = "Warning message";
    private static final String OPERATION_FAILED_MESSAGE = "Operation failed";
    private static final String TEST_VALUE = "test-value";
    private static final String TEST_FAILURE = "Test failure";
    private static final String BAD_INPUT = "Bad input";

    private final AuditionLogger auditionLogger;
    private final Logger logger;

    AuditionLoggerTest() {
        super();
        this.auditionLogger = new AuditionLogger(TestProperties.messageProperties());
        this.logger = mock(Logger.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testInfoWithObjectLogsOnlyWhenInfoIsEnabled(final boolean enabled) {
        when(logger.isInfoEnabled()).thenReturn(enabled);

        auditionLogger.info(logger, VALUE_TEMPLATE, TEST_VALUE);

        if (enabled) {
            verify(logger).info(VALUE_TEMPLATE, TEST_VALUE);
        } else {
            verify(logger, never()).info(VALUE_TEMPLATE, TEST_VALUE);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testWarnLogsOnlyWhenWarnIsEnabled(final boolean enabled) {
        when(logger.isWarnEnabled()).thenReturn(enabled);

        auditionLogger.warn(logger, WARNING_MESSAGE);

        if (enabled) {
            verify(logger).warn(WARNING_MESSAGE);
        } else {
            verify(logger, never()).warn(WARNING_MESSAGE);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testLogErrorWithExceptionRespectsErrorLevel(final boolean enabled) {
        final Exception exception = new IllegalStateException(TEST_FAILURE);

        when(logger.isErrorEnabled()).thenReturn(enabled);

        auditionLogger.logErrorWithException(
                logger,
                OPERATION_FAILED_MESSAGE,
                exception);

        if (enabled) {
            verify(logger).error(OPERATION_FAILED_MESSAGE, exception);
        } else {
            verify(logger, never()).error(
                    OPERATION_FAILED_MESSAGE,
                    exception);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testLogStandardProblemDetailRespectsErrorLevel(final boolean enabled) {
        final ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        final Exception exception = new IllegalArgumentException(BAD_INPUT);

        when(logger.isErrorEnabled()).thenReturn(enabled);

        auditionLogger.logStandardProblemDetail(
                logger,
                problemDetail,
                exception);

        if (enabled) {
            verify(logger).error(anyString(), eq(exception));
        } else {
            verify(logger, never()).error(anyString(), eq(exception));
        }
    }
}
