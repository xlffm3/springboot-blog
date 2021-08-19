package com.spring.blog.post.domain.content;

import com.spring.blog.exception.post.PostContentException;
import com.spring.blog.exception.post.PostTitleException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PostContent {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 10000)
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
            throw new PostTitleException();
        }
        String trimTitle = title.trim();
        if (trimTitle.isEmpty() || trimTitle.length() > MAX_TITLE_LENGTH) {
            throw new PostTitleException();
        }
    }

    private void validateContent(String content) {
        if (Objects.isNull(content)) {
            throw new PostContentException();
        }
        String trimContent = content.trim();
        if (trimContent.isEmpty() || trimContent.length() > MAX_CONTENT_LENGTH) {
            throw new PostContentException();
        }
    }
}
