package com.spring.blog.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.authentication.InvalidTokenException;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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

    @DisplayName("token 유효성 검사 : 유효하면 true를 반환한다.")
    @Test
    void validateToken_Valid_True() {
        // given
        TokenResponseDto tokenResponseDto = oAuthService.createToken("kevin");
        String token = tokenResponseDto.getToken();

        // when
        boolean isValid = oAuthService.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @DisplayName("token 유효성 검사 : 유효하지 않으면 false를 반환한다.")
    @Test
    void validateToken_Valid_False() {
        // given, when, then
        assertThat(oAuthService.validateToken("no")).isFalse();
    }

    @DisplayName("token이 유효하면 부합하는 AppUser를 반환한다.")
    @Test
    void findRequestUserByToken_Valid_AppUser() {
        // given
        TokenResponseDto tokenResponseDto = oAuthService.createToken("kevin");
        String token = tokenResponseDto.getToken();

        // when
        AppUser appUser = oAuthService.findRequestUserByToken(token);

        // then
        assertThat(appUser)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new LoginUser(null, "kevin"));
    }

    @DisplayName("token이 유효하지 않으면 AppUser를 조회하지 못하고 예외가 발생한다.")
    @Test
    void findRequestUserByToken_Invalid_ExceptionThrown() {
        // given, when, then
        assertThatCode(() -> oAuthService.findRequestUserByToken("invalid"))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.")
            .hasFieldOrPropertyWithValue("errorCode", "A0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED);
    }
}
