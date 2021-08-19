package com.spring.blog.authentication.domain;

public interface JwtTokenProvider {

    String createToken(String payload);

    boolean validateToken(String token);

    String getPayloadByKey(String token, String key);
}
