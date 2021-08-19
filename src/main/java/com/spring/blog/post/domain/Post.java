package com.spring.blog.post.domain;

import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.date.BaseDate;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PostContent postContent;

    @Embedded
    private BaseDate baseDate;

    protected Post() {
    }

    public Post(PostContent postContent) {
        this(null, postContent);
    }

    public Post(Long id, PostContent postContent) {
        this.id = id;
        this.postContent = postContent;
    }
}
