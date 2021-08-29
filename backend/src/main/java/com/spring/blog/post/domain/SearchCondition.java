package com.spring.blog.post.domain;

import java.util.Objects;

public class SearchCondition {

    private final String searchType;
    private final String keyword;

    public SearchCondition(String searchType, String keyword) {
        this.searchType = searchType;
        this.keyword = keyword;
    }

    public boolean isCustomSearchCondition() {
        return Objects.nonNull(searchType) && Objects.nonNull(keyword);
    }

    public boolean isForTitle() {
        return "title".equals(searchType);
    }

    public boolean isForName() {
        return "name".equals(searchType);
    }

    public boolean isForContent() {
        return "content".endsWith(searchType);
    }

    public String getKeyword() {
        return Objects.requireNonNull(keyword);
    }
}
