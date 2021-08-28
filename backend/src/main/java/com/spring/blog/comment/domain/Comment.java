package com.spring.blog.comment.domain;

import com.spring.blog.comment.domain.content.CommentContent;
import com.spring.blog.comment.domain.date.BaseDate;
import com.spring.blog.comment.domain.hierarchy.Hierarchy;
import com.spring.blog.exception.comment.CannotDeleteCommentException;
import com.spring.blog.exception.comment.CannotEditCommentException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.user.domain.User;
import java.time.LocalDateTime;
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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CommentContent commentContent;

    @Embedded
    private Hierarchy hierarchy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Embedded
    private BaseDate baseDate;

    protected Comment() {
    }

    public Comment(String content, Post post, User user) {
        this(null, content, post, user);
    }

    public Comment(Long id, String content, Post post, User user) {
        this(id, new CommentContent(content), new Hierarchy(), post, user);
    }

    public Comment(
        Long id,
        CommentContent commentContent,
        Hierarchy hierarchy,
        Post post,
        User user
    ) {
        this.id = id;
        this.commentContent = commentContent;
        this.hierarchy = hierarchy;
        this.post = post;
        this.user = user;
        this.isDeleted = false;
        this.baseDate = new BaseDate();
    }

    public void updateChildCommentHierarchy(Comment childComment) {
        hierarchy.updateChildHierarchy(this, childComment.hierarchy);
    }

    public void updateAsRoot() {
        hierarchy.updateAsRoot(this);
    }

    public void editContent(String content, User user) {
        if (!this.user.equals(user)) {
            throw new CannotEditCommentException();
        }
        this.commentContent = new CommentContent(content);
    }

    public void delete(User user) {
        if (!this.user.equals(user)) {
            throw new CannotDeleteCommentException();
        }
        this.isDeleted = true;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorName() {
        return user.getName();
    }

    public String getContent() {
        return commentContent.getContent();
    }

    public Long getDepth() {
        return hierarchy.getDepth();
    }

    public LocalDateTime getCreatedDate() {
        return baseDate.getCreatedDate();
    }

    public Long getLeftNode() {
        return hierarchy.getLeftNode();
    }

    public Long getRightNode() {
        return hierarchy.getRightNode();
    }

    public Comment getRootComment() {
        return hierarchy.getRootComment();
    }
}
