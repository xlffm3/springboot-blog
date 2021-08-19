package com.spring.blog.authentication.domain.user;

public abstract class AppUser {

    private final Long id;
    private final String name;

    protected AppUser(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
