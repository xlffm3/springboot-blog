package com.spring.blog.comment.domain.hierarchy;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.exception.comment.CommentDepthException;
import java.util.ArrayList;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Hierarchy {

    private static final int DEFAULT_DEPTH = 1;
    private static final int MAXIMUM_DEPTH = 99;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id")
    private Comment rootComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Embedded
    private ChildComments childComments;

    private Integer depth;

    public Hierarchy() {
        this(null, null, new ChildComments(new ArrayList<>()), DEFAULT_DEPTH);
    }

    public Hierarchy(
        Comment rootComment,
        Comment parentComment,
        ChildComments childComments,
        Integer depth
    ) {
        validateDepth(depth);
        this.rootComment = rootComment;
        this.parentComment = parentComment;
        this.childComments = childComments;
        this.depth = depth;
    }

    private void validateDepth(Integer depth) {
        if (depth <= 0 || depth > MAXIMUM_DEPTH) {
            throw new CommentDepthException();
        }
    }

    public void addChildComment(Comment parentComment, Comment childComment) {
        if (depth >= MAXIMUM_DEPTH) {
            throw new CannotAddChildCommentException();
        }
        childComments.add(childComment);
        childComment.updateHierarchy(rootComment, parentComment, depth + 1);
    }

    public void update(Comment rootComment, Comment parentComment, int depth) {
        validateDepth(depth);
        this.rootComment = rootComment;
        this.parentComment = parentComment;
        this.depth = depth;
    }

    public void updateRoot(Comment comment) {
        this.rootComment = comment;
    }

    public Integer getDepth() {
        return depth;
    }
}
