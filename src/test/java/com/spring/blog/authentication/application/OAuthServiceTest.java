package com.spring.blog.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.authentication.application.dto.TokenDto;
import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.exception.platform.PlatformHttpErrorException;
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

@DisplayName("OAuthService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @InjectMocks
    private OAuthService oAuthService;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("getGithubAuthorizationUrl 메서드는")
    @Nested
    class Describe_getGithubAuthorizationUrl {

        @DisplayName("요청이 들어올 때")
        @Nested
        class Context_given_request {

            @DisplayName("Authorization URL을 반환한다.")
            @Test
            void it_returns_authorization_url() {
                // given
                String url = "https://github.com/authorize";
                given(oAuthClient.getLoginUrl()).willReturn(url);

                // when
                String githubAuthorizationUrl = oAuthService.getGithubAuthorizationUrl();

                // then
                assertThat(githubAuthorizationUrl).isEqualTo(url);

                verify(oAuthClient, times(1)).getLoginUrl();
            }
        }
    }

    @DisplayName("createToken 메서드는")
    @Nested
    class Describe_createToken {

        @DisplayName("유효한 Code값이 아니라면")
        @Nested
        class Context_invalid_code {

            @DisplayName("Github Access Token을 가져오지 못하고 예외가 발생한다.")
            @Test
            void it_throws_PlatformHttpErrorException() {
                // given
                String invalidCode = "abc";
                given(oAuthClient.getAccessToken(invalidCode))
                    .willThrow(new PlatformHttpErrorException());

                // when, then
                assertThatCode(() -> oAuthService.createToken(invalidCode))
                    .isInstanceOf(PlatformHttpErrorException.class)
                    .hasMessage("외부 플랫폼 연동에 실패했습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);

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
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken))
                    .willThrow(new PlatformHttpErrorException());

                // when, then
                assertThatCode(() -> oAuthService.createToken(validCode))
                    .isInstanceOf(PlatformHttpErrorException.class)
                    .hasMessage("외부 플랫폼 연동에 실패했습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);

                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
            }
        }

        @DisplayName("외부 플랫폼에서 조회한 회원이 신규 유저라면")
        @Nested
        class Context_valid_code_and_new_user {

            @DisplayName("신규 유저를 DB에 저장한다.")
            @Test
            void it_saves_new_user() {
                // given
                String validCode = "valid code";
                String validAccessToken = "valid access token";
                String userName = "kevin";
                String jwtToken = "jwt.token";
                UserProfile userProfile = new UserProfile(userName, "image");
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken)).willReturn(userProfile);
                given(userRepository.findByName(userName)).willReturn(Optional.empty());
                given(jwtTokenProvider.createToken(userName)).willReturn(jwtToken);

                // when
                TokenDto tokenDto = oAuthService.createToken(validCode);

                // then
                assertThat(tokenDto)
                    .usingRecursiveComparison()
                    .isEqualTo(new TokenDto(jwtToken, userName));

                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
                verify(userRepository, times(1)).findByName(userName);
                verify(userRepository, times(1)).save(any(User.class));
                verify(jwtTokenProvider, times(1)).createToken(userName);
            }
        }

        @DisplayName("외부 플랫폼에서 조회한 회원이 이미 등록된 유저라면")
        @Nested
        class Context_valid_code_and_already_existing_user {

            @DisplayName("DB에 저장하지 않는다.")
            @Test
            void it_does_not_save_new_user() {
                // given
                String validCode = "valid code";
                String validAccessToken = "valid access token";
                String userName = "kevin";
                String jwtToken = "jwt.token";
                UserProfile userProfile = new UserProfile(userName, "image");
                User user = new User(userName, "image");
                given(oAuthClient.getAccessToken(validCode)).willReturn(validAccessToken);
                given(oAuthClient.getUserProfile(validAccessToken)).willReturn(userProfile);
                given(userRepository.findByName(userName)).willReturn(Optional.of(user));
                given(jwtTokenProvider.createToken(userName)).willReturn(jwtToken);

                // when
                TokenDto tokenDto = oAuthService.createToken(validCode);

                // then
                assertThat(tokenDto)
                    .usingRecursiveComparison()
                    .isEqualTo(new TokenDto(jwtToken, userName));

                verify(oAuthClient, times(1)).getAccessToken(validCode);
                verify(oAuthClient, times(1)).getUserProfile(validAccessToken);
                verify(userRepository, times(1)).findByName(userName);
                verify(userRepository, times(0)).save(any(User.class));
                verify(jwtTokenProvider, times(1)).createToken(userName);
            }
        }
    }
}