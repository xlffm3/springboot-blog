package com.spring.blog.post.domain.image;

import com.spring.blog.post.domain.Post;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    protected Image() {
    }

    public Image(String url, Post post) {
        this(null, url, post);
    }

    public Image(Long id, String url, Post post) {
        this.id = id;
        this.url = url;
        this.post = post;
    }

    public String getUrl() {
        return url;
    }
}
