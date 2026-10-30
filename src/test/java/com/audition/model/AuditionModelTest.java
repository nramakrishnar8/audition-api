package com.audition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class AuditionModelTest {

    @Test
    void testPostSupportsAllProperties() {
        final AuditionPost post = new AuditionPost();
        final AuditionComment comment = new AuditionComment();
        comment.setId(9);
        post.setUserId(3);
        post.setId(7);
        post.setTitle("Title");
        post.setBody("Body");
        post.setComments(List.of(comment));
        assertEquals(3, post.getUserId());
        assertEquals(7, post.getId());
        assertEquals("Title", post.getTitle());
        assertEquals("Body", post.getBody());
        assertEquals(9, post.getComments().get(0).getId());
    }

    @Test
    void testPostMatchesTitleAndAttachesComments() {
        final AuditionPost post = new AuditionPost();
        post.setTitle("Title");
        post.attachComments(List.of());
        assertEquals(0, post.getComments().size());
        assertTrue(post.matchesTitle("title"));
        assertFalse(post.matchesTitle("missing"));
    }

    @Test
    void testCommentSupportsAllProperties() {
        final AuditionComment comment = new AuditionComment();
        comment.setPostId(1);
        comment.setId(2);
        comment.setName("Name");
        comment.setEmail("person@example.com");
        comment.setBody("Comment");
        assertEquals(1, comment.getPostId());
        assertEquals(2, comment.getId());
        assertEquals("Name", comment.getName());
        assertEquals("person@example.com", comment.getEmail());
        assertEquals("Comment", comment.getBody());
    }
}
