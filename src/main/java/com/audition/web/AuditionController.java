package com.audition.web;

import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditionController {

    private final AuditionService auditionService;
    private final AuditionRequestValidator requestValidator;

    public AuditionController(
            final AuditionService auditionService,
            final AuditionRequestValidator requestValidator) {
        super();
        this.auditionService = auditionService;
        this.requestValidator = requestValidator;
    }

    @GetMapping(value = "${api.endpoints.posts}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditionPost> getPosts(
            @RequestParam(name = "${api.query-param.title}", required = false) final String titleFilter) {
        requestValidator.validateTitleFilter(titleFilter);
        return auditionService.getPosts(titleFilter);
    }

    @GetMapping(value = "${api.endpoints.post-by-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuditionPost getPostById(
            @PathVariable("id") final String postId) {
        return auditionService.getPostById(requestValidator.requirePostId(postId));
    }

    @GetMapping(value = "${api.endpoints.post-with-comments}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuditionPost getPostWithComments(
            @PathVariable("id") final String postId) {
        return auditionService.getPostWithComments(requestValidator.requirePostId(postId));
    }

    @GetMapping(value = "${api.endpoints.post-comments}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditionComment> getComments(
            @PathVariable("id") final String postId) {
        return auditionService.getCommentsByPostId(requestValidator.requirePostId(postId));
    }
}
