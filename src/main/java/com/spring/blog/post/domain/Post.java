package com.spring.blog.post.domain;

import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.hierarchy.ChildPosts;
import java.util.ArrayList;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PostContent postContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Post parentPost;

    @Embedded
    private ChildPosts childPosts;

    protected Post() {
    }

    public Post(PostContent postContent) {
        this(null, postContent);
    }

    public Post(Long id, PostContent postContent) {
        this.id = id;
        this.postContent = postContent;
        this.parentPost = null;
        this.childPosts = new ChildPosts(new ArrayList<>());
    }

    public void addChildPost(Post childPost) {
        childPosts.addChildPost(this, childPost);
        childPost.toParentPost(this);
    }

    public void toParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }

    public Post getParentPost() {
        return parentPost;
    }
}
