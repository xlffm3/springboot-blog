package com.spring.blog.authentication.domain.repository;

import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.exception.authentication.InvalidOauthProviderException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuthClientRepositoryImpl implements OAuthClientRepository {

    private final List<OAuthClient> oAuthClients;

    public OAuthClient findByName(String name) {
        return oAuthClients.stream()
            .filter(oAuthClient -> oAuthClient.matches(name))
            .findAny()
            .orElseThrow(InvalidOauthProviderException::new);
    }
}
