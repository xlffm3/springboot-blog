package com.spring.blog.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.common.IntegrationTest;
import com.spring.blog.exception.authentication.InvalidTokenException;
import com.spring.blog.exception.authentication.RegistrationRequiredException;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("AuthService 통합 테스트")
class AuthServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Github Login Url을 요청한다.")
    @Test
    void getOauthLoginUrl_Mock_Success() {
        // given, when
        String githubAuthorizationUrl = authService.getOAuthLoginUrl("github");

        // then
        assertThat(githubAuthorizationUrl).isEqualTo("https://api.github.com/authorize?");
    }

    @DisplayName("외부 플랫폼에서 조회한 유저가 신규 유저면 예외가 발생한다.")
    @Test
    void loginByOauth_NewUser_ExceptionThrown() {
        // given
        String code = "kevin";

        // when, then
        assertThatCode(() -> authService.loginByOauth("github", code))
            .isInstanceOf(RegistrationRequiredException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @DisplayName("외부 플랫폼에서 조회한 유저가 db 존재하면 로그인 성공한다.")
    @Test
    void loginByOauth_ExistingUser_Success() {
        // given
        userRepository.save(new User("kevin", "kevin@naver.com"));
        String code = "kevin";

        // when
        TokenResponseDto tokenResponseDto = authService.loginByOauth("github", code);

        // then
        assertThat(tokenResponseDto.getUserName()).isEqualTo("kevin");
    }

    @DisplayName("token 유효성 검사 : 유효하면 true를 반환한다.")
    @Test
    void validateToken_Valid_True() {
        // given
        userRepository.save(new User("123", "123@naver.com"));
        TokenResponseDto tokenResponseDto = authService.loginByOauth("github", "123");
        String token = tokenResponseDto.getToken();

        // when
        boolean isValid = authService.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @DisplayName("token 유효성 검사 : 유효하지 않으면 false를 반환한다.")
    @Test
    void validateToken_Valid_False() {
        // given, when, then
        assertThat(authService.validateToken("no")).isFalse();
    }

    @DisplayName("token이 유효하면 부합하는 AppUser를 반환한다.")
    @Test
    void findRequestUserByToken_Valid_AppUser() {
        // given
        userRepository.save(new User("123", "123@naver.com"));
        TokenResponseDto tokenResponseDto = authService.loginByOauth("github", "123");
        String token = tokenResponseDto.getToken();

        // when
        AppUser appUser = authService.findRequestUserByToken(token);

        // then
        assertThat(appUser)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(new LoginUser(null, "123"));
    }

    @DisplayName("token이 유효하지 않으면 AppUser를 조회하지 못하고 예외가 발생한다.")
    @Test
    void findRequestUserByToken_Invalid_ExceptionThrown() {
        // given, when, then
        assertThatCode(() -> authService.findRequestUserByToken("invalid"))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다.")
            .hasFieldOrPropertyWithValue("errorCode", "A0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED);
    }
}
