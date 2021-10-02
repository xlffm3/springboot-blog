package com.spring.blog.authentication.domain.user;

import com.spring.blog.exception.authentication.RegistrationRequiredException;
import java.util.Objects;

public class UserProfile {

    private String name;
    private String email;

    private UserProfile() {
    }

    public UserProfile(String name, String email) {
        validateEmail(email);
        this.name = name;
        this.email = email;
    }

    private void validateEmail(String email) {
        if (Objects.isNull(email)) {
            throw new RegistrationRequiredException(email);
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
