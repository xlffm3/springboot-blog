package com.spring.blog.comment.domain.hierarchy;

import com.spring.blog.comment.domain.Comment;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class ChildComments {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hierarchy.parentComment", cascade = CascadeType.PERSIST)
    private List<Comment> childComments;

    protected ChildComments() {
    }

    public ChildComments(List<Comment> childComments) {
        this.childComments = childComments;
    }

    public void add(Comment childComment) {
        childComments.add(childComment);
    }
}
