package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.support.TestProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ApiPropertiesTest {

    @Test
    void testApiPropertiesRejectsBlankBaseUrl() {
        assertThrows(IllegalArgumentException.class, () -> new ApiProperties(
                " ",
                Duration.ofSeconds(1),
                Duration.ofSeconds(1),
                10,
                new ApiProperties.QueryParam(TestProperties.POST_ID_QUERY_PARAM),
                new ApiProperties.Endpoints(
                        TestProperties.POSTS_PATH,
                        TestProperties.POST_BY_ID_PATH,
                        TestProperties.COMMENTS_PATH)));
    }

    @Test
    void testApiPropertiesRejectsNonPositiveTitleFilterMaxLength() {
        assertThrows(IllegalArgumentException.class, () -> new ApiProperties(
                TestProperties.BASE_URL,
                Duration.ofSeconds(1),
                Duration.ofSeconds(1),
                0,
                new ApiProperties.QueryParam(TestProperties.POST_ID_QUERY_PARAM),
                new ApiProperties.Endpoints(
                        TestProperties.POSTS_PATH,
                        TestProperties.POST_BY_ID_PATH,
                        TestProperties.COMMENTS_PATH)));
    }

    @Test
    void testMessagePropertiesFormatters() {
        final MessageProperties messages = TestProperties.messageProperties();
        assertEquals("Cannot find a Post with id 9", messages.formatPostNotFound("9"));
        assertEquals("Unable to retrieve post 9", messages.formatRetrievePostFailed("9"));
        assertEquals("Unable to retrieve comments for post 9", messages.formatRetrieveCommentsFailed("9"));
        assertEquals("Upstream service failed to retrieve post 9", messages.formatUpstreamRetrievePostFailed("9"));
        assertEquals("Title filter must not exceed 200 characters", messages.formatTitleFilterTooLong(200));
    }

    @Test
    void testTitleFilterLengthAndTimeouts() {
        final ApiProperties apiProperties = TestProperties.apiProperties(
                Duration.ofSeconds(2), Duration.ofMillis(1500));
        assertEquals(2000, apiProperties.connectTimeoutMillis());
        assertEquals(1500, apiProperties.readTimeoutMillis());
        assertFalse(apiProperties.isTitleFilterTooLong(null));
        assertTrue(apiProperties.isTitleFilterTooLong(
                "a".repeat(TestProperties.TITLE_FILTER_MAX_LENGTH + 1)));
    }

    @Test
    void testApiPropertiesBuildsUpstreamUrls() {
        final ApiProperties apiProperties = TestProperties.apiProperties();
        assertEquals(TestProperties.POSTS_URL, apiProperties.postsUrl());
        assertEquals(TestProperties.POST_BY_ID_URL, apiProperties.postByIdUrl());
        assertEquals(
                TestProperties.BASE_URL + TestProperties.COMMENTS_PATH
                        + "?" + TestProperties.POST_ID_QUERY_PARAM + "=4",
                apiProperties.commentsUrl("4"));
    }

    @Test
    void testAppPropertiesStoresConfiguredValues() {
        final AppProperties appProperties = TestProperties.appProperties();
        assertEquals(TestProperties.TRACE_ID_HEADER, appProperties.traceIdHeader());
        assertEquals(TestProperties.SPAN_ID_HEADER, appProperties.spanIdHeader());
        assertEquals(TestProperties.DATE_FORMAT, appProperties.dateFormat());
    }
}
