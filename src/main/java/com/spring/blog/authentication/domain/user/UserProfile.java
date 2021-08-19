package com.spring.blog.authentication.domain.user;

public class UserProfile {

    private String name;
    private String profileImageUrl;

    private UserProfile() {
    }

    public UserProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
