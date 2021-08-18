package com.spring.blog.post.domain.hierarchy;

import com.spring.blog.exception.post.CannotAddChildPostException;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class ChildPosts {

    private static final int MAXIMUM_PARENT_POST_DEPTH = 98;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentPost")
    private List<Post> childPosts;

    protected ChildPosts() {
    }

    public ChildPosts(List<Post> childPosts) {
        this.childPosts = childPosts;
    }

    public void addChildPost(Post parentPost, Post childPost) {
        validateAddableCondition(parentPost);
        childPosts.add(childPost);
    }

    private void validateAddableCondition(Post parentPost) {
        int depth = 0;
        while (Objects.nonNull(parentPost) && depth <= MAXIMUM_PARENT_POST_DEPTH) {
            parentPost = parentPost.getParentPost();
            depth++;
        }
        if (depth == 0 || depth > MAXIMUM_PARENT_POST_DEPTH) {
            throw new CannotAddChildPostException();
        }
    }
}
