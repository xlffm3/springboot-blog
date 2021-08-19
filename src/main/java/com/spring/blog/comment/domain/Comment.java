package com.spring.blog.comment.domain;

import com.spring.blog.comment.domain.content.CommentContent;
import com.spring.blog.comment.domain.date.BaseDate;
import com.spring.blog.comment.domain.hierarchy.Hierarchy;
import com.spring.blog.post.domain.Post;
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
    @JoinColumn(name = "post_id")
    private Post post;

    @Embedded
    private BaseDate baseDate;

    protected Comment() {
    }

    public Comment(CommentContent commentContent) {
        this(null, commentContent);
    }

    public Comment(Long id, CommentContent commentContent) {
        this(id, commentContent, new Hierarchy(), null);
    }

    public Comment(
        Long id,
        CommentContent commentContent,
        Hierarchy hierarchy,
        Post post
    ) {
        this.id = id;
        this.commentContent = commentContent;
        this.hierarchy = hierarchy;
        this.post = post;
    }

    public void addChildComment(Comment childComment) {
        hierarchy.addChildComment(this, childComment);
    }

    public void updateAsRoot() {
        hierarchy.updateRoot(this);
    }

    public void updateHierarchy(Comment rootComment, Comment parentComment, int depth) {
        hierarchy.update(rootComment, parentComment, depth);
    }
}
