package com.spring.blog.post.domain;

import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.date.BaseDate;
import com.spring.blog.user.domain.User;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Post {

    private static final Long DEFAULT_VIEW_COUNTS = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PostContent postContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long viewCounts;

    @Embedded
    private BaseDate baseDate;

    protected Post() {
    }

    public Post(PostContent postContent, User user) {
        this(null, postContent, user);
    }

    public Post(Long id, PostContent postContent, User user) {
        this.id = id;
        this.postContent = postContent;
        this.user = user;
        this.viewCounts = DEFAULT_VIEW_COUNTS;
        this.baseDate = new BaseDate();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return postContent.getTitle();
    }

    public String getContent() {
        return postContent.getContent();
    }

    public Long getViewCounts() {
        return viewCounts;
    }

    public LocalDateTime getCreatedDate() {
        return baseDate.getCreatedDate();
    }

    public LocalDateTime getModifiedDate() {
        return baseDate.getModifiedDate();
    }
}
