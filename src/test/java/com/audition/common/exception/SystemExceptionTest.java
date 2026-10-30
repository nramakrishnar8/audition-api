package com.audition.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class SystemExceptionTest {

    private static final String CAUSE = "cause";
    private static final String DETAIL = "detail";
    private static final String TITLE = "title";

    @Test
    void testPreservesStatusTitleAndDetail() {
        final SystemException exception = new SystemException(DETAIL, TITLE, 400);
        assertEquals(400, exception.getStatusCode());
        assertEquals(TITLE, exception.getTitle());
        assertEquals(DETAIL, exception.getDetail());
        assertEquals(DETAIL, exception.getMessage());
    }

    @Test
    void testPreservesCause() {
        final IllegalStateException cause = new IllegalStateException(CAUSE);
        final SystemException exception = new SystemException(DETAIL, TITLE, 502, cause);
        assertEquals(502, exception.getStatusCode());
        assertEquals(TITLE, exception.getTitle());
        assertEquals(DETAIL, exception.getDetail());
        assertSame(cause, exception.getCause());
    }
}
