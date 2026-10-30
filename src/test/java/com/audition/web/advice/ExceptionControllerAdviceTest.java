package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.MessageProperties;
import com.audition.support.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

class ExceptionControllerAdviceTest {

    private final ExceptionControllerAdvice advice;
    private final MessageProperties messageProperties;

    ExceptionControllerAdviceTest() {
        super();
        this.messageProperties = TestProperties.messageProperties();
        this.advice = new ExceptionControllerAdvice(
                new AuditionLogger(messageProperties), messageProperties);
    }

    @Test
    void testHandleSystemExceptionSuccess() {
        final ProblemDetail detail = advice.handleSystemException(
                new SystemException("Invalid input", TestProperties.INVALID_REQUEST_TITLE, 400));

        assertEquals(400, detail.getStatus());
        assertEquals(TestProperties.INVALID_REQUEST_TITLE, detail.getTitle());
        assertEquals("Invalid input", detail.getDetail());
    }

    @Test
    void testHandleSystemExceptionMissingStatus() {
        final ProblemDetail detail =
                advice.handleSystemException(new SystemException("Failure", "API Error Occurred", null));

        assertEquals(500, detail.getStatus());
        assertEquals(messageProperties.getDefaultDetail(), detail.getDetail());
        assertEquals(messageProperties.getDefaultTitle(), detail.getTitle());
    }

    @Test
    void testHandleSystemExceptionInvalidStatus() {
        final ProblemDetail detail =
                advice.handleSystemException(new SystemException("Failure", "API Error Occurred", 99));

        assertEquals(500, detail.getStatus());
        assertEquals(messageProperties.getDefaultDetail(), detail.getDetail());
    }

    @Test
    void testHandleHttpClientException() {
        final ProblemDetail detail = advice.handleHttpClientException(
                new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not found"));

        assertEquals(404, detail.getStatus());
        assertEquals(messageProperties.getDefaultDetail(), detail.getDetail());
    }

    @Test
    void testHandleMethodNotSupported() {
        final ProblemDetail detail = advice.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("POST"));

        assertEquals(405, detail.getStatus());
        assertNotNull(detail.getDetail());
    }

    @Test
    void testHandleMainExceptionMethodNotAllowed() {
        final ProblemDetail detail = advice.handleMainException(
                new HttpRequestMethodNotSupportedException("POST"));

        assertEquals(405, detail.getStatus());
        assertNotNull(detail.getDetail());
    }

    @Test
    void testHandleMainExceptionError() {
        final ProblemDetail detail = advice.handleMainException(
                new IllegalStateException("Internal database details"));

        assertEquals(500, detail.getStatus());
        assertEquals(messageProperties.getDefaultDetail(), detail.getDetail());
    }

    @Test
    void testHandleSystemExceptionHidesServerErrorDetails() {
        final ProblemDetail detail = advice.handleSystemException(
                new SystemException("Internal upstream details", "Upstream Service Error", 502));

        assertEquals(502, detail.getStatus());
        assertEquals(messageProperties.getDefaultDetail(), detail.getDetail());
    }
}
