package com.spring.blog.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("OAuthService 통합 테스트")
@ActiveProfiles("test")
@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class OAuthServiceIntegrationTest {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @DisplayName("Github Login Url을 요청한다.")
    @Test
    void getGithubAuthorizationUrl_Mock_Success() {
        // given, when
        String githubAuthorizationUrl = oAuthService.getGithubAuthorizationUrl();

        // then
        assertThat(githubAuthorizationUrl).isEqualTo("https://api.github.com/authorize?");
    }

    @DisplayName("외부 플랫폼에서 조회한 유저가 신규 유저면 DB에 저장한다.")
    @Test
    void createToken_NewUser_Save() {
        // given
        String code = "kevin";
        boolean before = userRepository.findByName(code)
            .isPresent();

        // when
        oAuthService.createToken(code);

        // then
        assertThat(userRepository.findByName(code)).isNotEmpty();
    }
}
