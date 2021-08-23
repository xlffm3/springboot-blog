package com.spring.blog.authentication.domain.user;

public class AnonymousUser extends AppUser {

    private static final Long ANONYMOUS_ID = -1L;
    private static final String ANONYMOUS_USER_NAME = "ANONYMOUS";

    public AnonymousUser() {
        this(1L, ANONYMOUS_USER_NAME);
    }

    public AnonymousUser(Long id, String name) {
        super(id, name);
    }

    @Override
    public Long getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
}
