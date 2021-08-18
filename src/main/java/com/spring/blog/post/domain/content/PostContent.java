package com.spring.blog.post.domain.content;

import com.spring.blog.exception.post.InvalidContentException;
import com.spring.blog.exception.post.InvalidTitleException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class PostContent {

    private static final int MAX_TITLE_LENGTH = 100;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    protected PostContent() {
    }

    public PostContent(String title, String content) {
        validateTitle(title);
        validateContent(content);
        this.title = title;
        this.content = content;
    }

    private void validateTitle(String title) {
        if (Objects.isNull(title)) {
            throw new InvalidTitleException();
        }
        String trimTitle = title.trim();
        if (trimTitle.isEmpty() || trimTitle.length() > MAX_TITLE_LENGTH) {
            throw new InvalidTitleException();
        }
    }

    private void validateContent(String content) {
        if (Objects.isNull(content)) {
            throw new InvalidContentException();
        }
        String trimContent = content.trim();
        if (trimContent.isEmpty()) {
            throw new InvalidContentException();
        }
    }
}
