package com.spring.blog.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String profileImage;

    protected User() {
    }

    public User(String name, String profileImage) {
        this(null, name, profileImage);
    }

    public User(Long id, String name, String profileImage) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
