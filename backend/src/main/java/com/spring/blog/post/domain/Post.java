package com.spring.blog.post.domain;

import com.spring.blog.exception.post.CannotDeletePostException;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.date.BaseDate;
import com.spring.blog.post.domain.image.Images;
import com.spring.blog.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
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

    @Embedded
    private Images images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long viewCounts;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Embedded
    private BaseDate baseDate;

    protected Post() {
    }

    public Post(String title, String content, User user) {
        this(null, title, content, user);
    }

    public Post(Long id, String title, String content, User user) {
        this(id, new PostContent(title, content), new Images(new ArrayList<>()), user);
    }

    public Post(Long id, PostContent postContent, Images images, User user) {
        this.id = id;
        this.postContent = postContent;
        this.images = images;
        this.user = user;
        this.viewCounts = DEFAULT_VIEW_COUNTS;
        this.baseDate = new BaseDate();
        this.isDeleted = false;
    }

    public void addImages(List<String> imageUrls) {
        images.add(imageUrls, this);
    }

    public void delete(User user) {
        if (!this.user.equals(user)) {
            throw new CannotDeletePostException();
        }
        this.isDeleted = true;
    }

    public void updateViewCounts() {
        this.viewCounts++;
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

    public String getAuthorName() {
        return user.getName();
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

    public List<String> getImageUrls() {
        return images.getUrls();
    }
}
