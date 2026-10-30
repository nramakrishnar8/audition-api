package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.support.TestProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class AuditionPostsControllerTest extends AuditionControllerITSupport {

    @Autowired
    AuditionPostsControllerTest(final MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    void testGetPostsSuccess() {
        when(auditionService.getPosts(null))
                .thenReturn(List.of(TestProperties.post(1, "Spring Boot"), TestProperties.post(2, "Java")));

        assertDoesNotThrow(() -> mockMvc.perform(get(TestProperties.POSTS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)));
    }

    @Test
    void testGetPostsFiltersByTitle() {
        when(auditionService.getPosts(" spring "))
                .thenReturn(List.of(TestProperties.post(1, "Spring Boot")));

        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.POSTS_PATH)
                                .param(TestProperties.TITLE_QUERY_PARAM, " spring "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1)));
    }

    @Test
    void testGetPostsIgnoresBlankTitleFilter() {
        when(auditionService.getPosts("   "))
                .thenReturn(List.of(TestProperties.post(1, "Spring Boot"), TestProperties.post(2, null)));

        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.POSTS_PATH)
                                .param(TestProperties.TITLE_QUERY_PARAM, "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)));
    }

    @Test
    void testGetPostsTitleFilterTooLong() {
        final String tooLongTitle = "a".repeat(TestProperties.TITLE_FILTER_MAX_LENGTH + 1);

        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.POSTS_PATH)
                                .param(TestProperties.TITLE_QUERY_PARAM, tooLongTitle))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(TestProperties.JSON_TITLE).value(TestProperties.INVALID_REQUEST_TITLE)));
    }
}
