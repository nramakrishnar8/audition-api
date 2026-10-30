package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.SystemException;
import com.audition.support.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AuditionRequestValidatorTest {

    private final AuditionRequestValidator validator;

    AuditionRequestValidatorTest() {
        super();
        this.validator = new AuditionRequestValidator(
                TestProperties.apiProperties(),
                TestProperties.messageProperties());
    }

    @Test
    void testRequirePostIdAcceptsPositiveInteger() {
        assertEquals("12", validator.requirePostId(" 12 "));
    }

    @Test
    void testRequirePostIdRejectsBlankZeroNegativeAndNonNumeric() {
        assertInvalidPostId(" ");
        assertInvalidPostId("0");
        assertInvalidPostId("-3");
        assertInvalidPostId("abc");
        assertInvalidPostId("+1");
        assertInvalidPostId(null);
    }

    @Test
    void testValidateTitleFilterRejectsOversizedValue() {
        final String tooLong = "a".repeat(TestProperties.TITLE_FILTER_MAX_LENGTH + 1);
        final SystemException exception = assertThrows(
                SystemException.class,
                () -> validator.validateTitleFilter(tooLong));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }

    @Test
    void testValidateTitleFilterAllowsNullAndMaxLength() {
        assertDoesNotThrow(() -> validator.validateTitleFilter(null));
        assertDoesNotThrow(() -> validator.validateTitleFilter(
                "a".repeat(TestProperties.TITLE_FILTER_MAX_LENGTH)));
    }

    private void assertInvalidPostId(final String postId) {
        final SystemException exception = assertThrows(
                SystemException.class,
                () -> validator.requirePostId(postId));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }
}
