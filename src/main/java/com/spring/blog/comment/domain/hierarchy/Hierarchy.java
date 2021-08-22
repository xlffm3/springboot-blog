package com.spring.blog.comment.domain.hierarchy;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.exception.comment.CommentDepthException;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Hierarchy {

    private static final long DEFAULT_LEFT_FOR_ROOT = 1;
    private static final long DEFAULT_RIGHT_FOR_ROOT = 2;
    private static final long DEFAULT_DEPTH = 1;
    private static final long MAXIMUM_DEPTH = 99;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id")
    private Comment rootComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false)
    private Long leftNode;

    @Column(nullable = false)
    private Long rightNode;

    @Column(nullable = false)
    private Long depth;

    public Hierarchy() {
        this(null, null, null, null, DEFAULT_DEPTH);
    }

    public Hierarchy(
        Comment rootComment,
        Comment parentComment,
        Long leftNode,
        Long rightNode,
        Long depth
    ) {
        validateDepth(depth);
        this.rootComment = rootComment;
        this.parentComment = parentComment;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.depth = depth;
    }

    private void validateDepth(long depth) {
        if (depth < DEFAULT_DEPTH || depth > MAXIMUM_DEPTH) {
            throw new CommentDepthException();
        }
    }

    public void updateChildHierarchy(Comment parentComment, Hierarchy childCommentHierarchy) {
        validateAddableCondition(this.depth);
        childCommentHierarchy.rootComment = this.rootComment;
        childCommentHierarchy.parentComment = parentComment;
        childCommentHierarchy.leftNode = this.rightNode;
        childCommentHierarchy.rightNode = this.rightNode + 1L;
        childCommentHierarchy.depth = this.depth + 1L;
    }

    private void validateAddableCondition(long depth) {
        if (depth >= MAXIMUM_DEPTH) {
            throw new CannotAddChildCommentException();
        }
    }

    public void updateAsRoot(Comment comment) {
        this.rootComment = comment;
        this.leftNode = DEFAULT_LEFT_FOR_ROOT;
        this.rightNode = DEFAULT_RIGHT_FOR_ROOT;
        this.depth = DEFAULT_DEPTH;
    }

    public Comment getRootComment() {
        return rootComment;
    }

    public Long getLeftNode() {
        return leftNode;
    }

    public Long getRightNode() {
        return rightNode;
    }

    public Long getDepth() {
        return depth;
    }
}
