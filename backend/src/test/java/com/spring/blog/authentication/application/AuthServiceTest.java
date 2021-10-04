package com.spring.blog.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.repository.OAuthClientRepository;
import com.spring.blog.authentication.domain.user.AnonymousUser;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.exception.authentication.InvalidTokenException;
import com.spring.blog.exception.authentication.RegistrationRequiredException;
import com.spring.blog.exception.platform.PlatformHttpErrorException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@DisplayName("AuthService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private OAuthClientRepository oAuthClientRepository;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("getLoginUrl 메서드는")
    @Nested
    class Describe_getOAuthLoginUrl {

        @DisplayName("요청이 들어올 때")
        @Nested
        class Context_invalid_request {

            @DisplayName("Authorization URL을 반환한다.")
            @Test
            void it_returns_authorization_url() {
                // given
                String url = "https://github.com/authorize";
                given(oAuthClientRepository.findByName("github")).willReturn(oAuthClient);
                given(oAuthClient.getLoginUrl()).willReturn(url);

                // when
                String githubAuthorizationUrl = authService.getOAuthLoginUrl("github");

                // then
                assertThat(githubAuthorizationUrl).isEqualTo(url);

                verify(oAuthClient, times(1)).getLoginUrl();
                verify(oAuthClientRepository, times(1)).findByName("github");
            }
        }
    }

    @DisplayName("loginByOauth 메서드는")
    @Nested
    class Describe_loginByOauth {

        @DisplayName("유효한 Code값이 아니라면")
        @Nested
        class Context_invalid_code {

            @DisplayName("Github Access Token을 가져오지 못하고 예외가 발생한다.")
            @Test
            void it_throws_PlatformHttpErrorException() {
                // given
                String invalidCode = "abc";
                given(oAuthClientRepository.findByName("github")).willReturn(oAuthClient);
                given(oAuthClient.getAccessToken(invalidCode))
                    .willThrow(new PlatformHttpErrorException());

                // when, then
                assertThatCode(() -> authService.loginByOauth("github", invalidCode))
                    .isInstanceOf(PlatformHttpErrorException.class)
                    .hasMessage("외부 플랫폼 연동에 실패했습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "V0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);

                verify(oAuthClientRepository, times(1)).findByName("github");
                verify(oAuthClient, times(1)).getAccessToken(invalidCode);
            }
        }

        @DisplayName("유효한 Code이더라도 외부 플랫폼에서 해당 회원을 조회하지 못하면")
        @Nested
        class Context_valid_code_but_user_profile_not_found {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_PlatformHttpErrorException() {
                // given
                String validCode = "valid code";
                String validAccessToken = "valid access token";
                given(oAuthClientRepository.findByName("github")).willReturn(oAuthClient);
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken))
                    .willThrow(new PlatformHttpErrorException());

                // when, then
                assertThatCode(() -> authService.loginByOauth("github", validCode))
                    .isInstanceOf(PlatformHttpErrorException.class)
                    .hasMessage("외부 플랫폼 연동에 실패했습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "V0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);

                verify(oAuthClientRepository, times(1)).findByName("github");
                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
            }
        }

        @DisplayName("외부 플랫폼에서 조회한 회원이 신규 유저라면")
        @Nested
        class Context_valid_code_and_new_user {

            @DisplayName("회원 가입 필요 예외가 발생한다.")
            @Test
            void it_throws_RegistrationRequiredException() {
                // given
                String validCode = "valid code";
                String validAccessToken = "valid access token";
                String userName = "kevin";
                String email = "abc@naver.com";
                UserProfile userProfile = new UserProfile(userName, email);
                given(oAuthClientRepository.findByName("github")).willReturn(oAuthClient);
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken)).willReturn(userProfile);
                given(userRepository.findActiveUserByEmail(email)).willReturn(Optional.empty());

                // when
                assertThatCode(() -> authService.loginByOauth("github", validCode))
                    .isInstanceOf(RegistrationRequiredException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "A0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

                verify(oAuthClientRepository, times(1)).findByName("github");
                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
                verify(userRepository, times(1)).findActiveUserByEmail(email);
            }
        }

        @DisplayName("외부 플랫폼에서 조회한 회원이 이미 등록된 유저라면")
        @Nested
        class Context_valid_code_and_already_existing_user {

            @DisplayName("로그인에 성공한다")
            @Test
            void it_logins() {
                // given
                String validCode = "valid code";
                String validAccessToken = "valid access token";
                String userName = "kevin";
                String jwtToken = "jwt.token";
                String email = "abc@naver.com";
                UserProfile userProfile = new UserProfile(userName, email);
                User user = new User(1L, "kevin", email);
                given(oAuthClientRepository.findByName("github")).willReturn(oAuthClient);
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken)).willReturn(userProfile);
                given(userRepository.findActiveUserByEmail(email)).willReturn(Optional.of(user));
                given(jwtTokenProvider.createToken(userName)).willReturn(jwtToken);

                // when
                TokenResponseDto tokenResponseDto = authService.loginByOauth("github", validCode);
                TokenResponseDto expected = TokenResponseDto.builder()
                    .token(jwtToken)
                    .userName(userName)
                    .build();

                // then
                assertThat(tokenResponseDto)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);

                verify(oAuthClientRepository, times(1)).findByName("github");
                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
                verify(userRepository, times(1)).findActiveUserByEmail(email);
                verify(jwtTokenProvider, times(1)).createToken(userName);
            }
        }
    }

    @DisplayName("validateToken 메서드는")
    @Nested
    class Describe_validateToken {

        @DisplayName("토큰이 정상인 경우")
        @Nested
        class Context_valid_token {

            @DisplayName("true를 반환한다.")
            @Test
            void it_returns_true() {
                // given
                String token = "token";
                given(jwtTokenProvider.validateToken(token)).willReturn(true);

                // when
                boolean isValid = authService.validateToken(token);

                // then
                assertThat(isValid).isTrue();

                verify(jwtTokenProvider, times(1)).validateToken(token);
            }
        }

        @DisplayName("토큰이 비정상인 경우")
        @Nested
        class Context_invalid_token {

            @DisplayName("false를 반환한다.")
            @Test
            void it_returns_false() {
                // given
                String token = "token";
                given(jwtTokenProvider.validateToken(token)).willReturn(false);

                // when
                boolean isValid = authService.validateToken(token);

                // then
                assertThat(isValid).isFalse();

                verify(jwtTokenProvider, times(1)).validateToken(token);
            }
        }
    }

    @DisplayName("findRequestUserByToken 메서드는")
    @Nested
    class Describe_findRequestUserByToken {

        @DisplayName("token이 null인경우")
        @Nested
        class Context_null_token {

            @DisplayName("익명 유저를 반환한다.")
            @Test
            void it_returns_anonymous_user() {
                // given, when
                AppUser appUser = authService.findRequestUserByToken(null);

                // then
                assertThat(appUser).isInstanceOf(AnonymousUser.class);
            }
        }

        @DisplayName("token이 유효하지 않은 경우")
        @Nested
        class Context_invalid_token {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_InvalidTokenException() {
                // given
                String token = "invalid token";
                given(jwtTokenProvider.getPayloadByKey(token, "userName"))
                    .willThrow(new InvalidTokenException());

                // when, then
                assertThatCode(() -> authService.findRequestUserByToken(token))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "A0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED);

                verify(jwtTokenProvider, times(1)).getPayloadByKey(token, "userName");
            }
        }

        @DisplayName("token이 유효한 경우")
        @Nested
        class Context_valid_token {

            @DisplayName("회원을 조회하여 반환한다.")
            @Test
            void it_returns_appUser() {
                // given
                String token = "token";
                User user = new User(1L, "kevin", "image");
                given(jwtTokenProvider.getPayloadByKey(token, "userName"))
                    .willReturn("kevin");
                given(userRepository.findActiveUserByName("kevin")).willReturn(Optional.of(user));

                // when
                AppUser appUser = authService.findRequestUserByToken(token);

                // then
                assertThat(appUser)
                    .usingRecursiveComparison()
                    .isEqualTo(new LoginUser(1L, "kevin"))
                    .isInstanceOf(LoginUser.class);

                verify(jwtTokenProvider, times(1)).getPayloadByKey(token, "userName");
                verify(userRepository, times(1)).findActiveUserByName("kevin");
            }
        }

        @DisplayName("token이 유효하지만 해당 이름의 회원이 없는 경우")
        @Nested
        class Context_valid_token_but_user_not_found {

            @DisplayName("회원 조회 예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                String token = "token";
                given(jwtTokenProvider.getPayloadByKey(token, "userName"))
                    .willReturn("kevin");
                given(userRepository.findActiveUserByName("kevin")).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> authService.findRequestUserByToken(token))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(jwtTokenProvider, times(1)).getPayloadByKey(token, "userName");
                verify(userRepository, times(1)).findActiveUserByName("kevin");
            }
        }
    }
}
