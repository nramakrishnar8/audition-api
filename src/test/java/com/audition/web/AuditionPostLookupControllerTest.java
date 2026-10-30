package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.model.AuditionComment;
import com.audition.support.TestProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class AuditionPostLookupControllerTest extends AuditionControllerITSupport {

    @Autowired
    AuditionPostLookupControllerTest(final MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    void testGetPostByIdSuccess() {
        when(auditionService.getPostById("3"))
                .thenReturn(TestProperties.post(3, "Example"));

        assertDoesNotThrow(() -> mockMvc.perform(
                get(TestProperties.expandId(TestProperties.POST_BY_ID_PATH, "3")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3)));
    }

    @Test
    void testGetPostByIdNonNumericError() {
        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.expandId(TestProperties.POST_BY_ID_PATH, "not-a-number")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(TestProperties.JSON_TITLE).value(TestProperties.INVALID_REQUEST_TITLE)));
    }

    @Test
    void testGetPostByIdZeroError() {
        assertDoesNotThrow(() -> mockMvc.perform(
                get(TestProperties.expandId(TestProperties.POST_BY_ID_PATH, "0")))
                .andExpect(status().isBadRequest()));
    }

    @Test
    void testGetPostByIdNegativeError() {
        assertDoesNotThrow(() -> mockMvc.perform(
                get(TestProperties.expandId(TestProperties.POST_BY_ID_PATH, "-1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(TestProperties.JSON_TITLE).value(TestProperties.INVALID_REQUEST_TITLE)));
    }

    @Test
    void testGetPostWithCommentsSuccess() {
        when(auditionService.getPostWithComments("3"))
                .thenReturn(TestProperties.post(3, "Example"));

        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.expandId(TestProperties.POST_WITH_COMMENTS_PATH, "3")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3)));
    }

    @Test
    void testGetCommentsSuccess() {
        final AuditionComment comment = new AuditionComment();
        comment.setId(11);
        comment.setPostId(1);

        when(auditionService.getCommentsByPostId("1"))
                .thenReturn(List.of(comment));

        assertDoesNotThrow(() -> mockMvc.perform(
                        get(TestProperties.expandId(TestProperties.POST_COMMENTS_PATH, "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11)));
    }
}
