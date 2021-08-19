package com.spring.blog.comment.domain.content;

import com.spring.blog.exception.comment.CommentContentException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommentContent {

    private static final int MAX_COMMENT_LENGTH = 140;

    @Column(nullable = false, length = 140)
    private String content;

    protected CommentContent() {
    }

    public CommentContent(String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(String content) {
        if (Objects.isNull(content)) {
            throw new CommentContentException();
        }
        String trimContent = content.trim();
        if (trimContent.isEmpty() || trimContent.length() > MAX_COMMENT_LENGTH) {
            throw new CommentContentException();
        }
    }
}
