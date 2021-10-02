package com.spring.blog.authentication.domain.repository;

import com.spring.blog.authentication.domain.OAuthClient;

public interface OAuthClientRepository {

    OAuthClient findByName(String name);
}
