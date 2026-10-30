package com.audition.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuditionPost {
    private int userId;
    private int id;
    private String title;
    private String body;
    private List<AuditionComment> comments;

    public boolean matchesTitle(final String normalizedFilter) {
        return title != null
                && normalizedFilter != null
                && title.toLowerCase(Locale.ROOT).contains(normalizedFilter);
    }

    public void attachComments(final List<AuditionComment> postComments) {
        this.comments = postComments;
    }
}
