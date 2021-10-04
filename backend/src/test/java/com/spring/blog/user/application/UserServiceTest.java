package com.spring.blog.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.exception.user.ActiveAccountExistingException;
import com.spring.blog.exception.user.NameDuplicationException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.application.dto.UserRegistrationRequestDto;
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

@DisplayName("UserService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("withdraw 메서드는")
    @Nested
    class Describe_withdraw {

        @DisplayName("해당 ID의 User가 존재하지 않으면")
        @Nested
        class Context_user_not_found {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> userService.withdraw(1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }

        @DisplayName("해당 ID의 User가 존재하면")
        @Nested
        class Context_user_found {

            @DisplayName("User는 삭제된다.")
            @Test
            void it_deletes_user() {
                // given
                User user = new User(1L, "kevin", "image");
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));

                // when
                userService.withdraw(1L);

                // then
                assertThat(user)
                    .extracting("isDeleted")
                    .isEqualTo(true);

                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }
    }

    @DisplayName("registerByOauth 메서드는")
    @Nested
    class Describe_registerByOauth {

        @DisplayName("회원가입하려는 이름이 이미 중복되는 경우(삭제된 회원 포함)")
        @Nested
        class Context_name_duplicated_including_deleted_users {

            @DisplayName("NameDuplicationException이 발생한다.")
            @Test
            void it_throws_NameDuplicationException() {
                // given
                UserRegistrationRequestDto userRegistrationRequestDto =
                    UserRegistrationRequestDto.builder()
                        .name("abc")
                        .email("abc@naver.com")
                        .build();
                User user = new User("abc", "kkk@naver.com");
                given(userRepository.findByName("abc")).willReturn(Optional.of(user));

                // when, then
                assertThatCode(() -> userService.registerByOauth(userRegistrationRequestDto))
                    .isInstanceOf(NameDuplicationException.class)
                    .hasMessage("중복된 이름입니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

                verify(userRepository, times(1)).findByName("abc");
            }
        }

        @DisplayName("이름은 중복이 아니지만 동일 이메일로 활성화된 유저 계정이 존재하는 경우")
        @Nested
        class Context_name_unique_but_same_email_account_alive {

            @DisplayName("NameDuplicationException이 발생한다.")
            @Test
            void it_throws_NameDuplicationException() {
                // given
                UserRegistrationRequestDto userRegistrationRequestDto =
                    UserRegistrationRequestDto.builder()
                        .name("abc")
                        .email("abc@naver.com")
                        .build();
                User user = new User("kkk", "kkk@naver.com");
                given(userRepository.findByName("abc")).willReturn(Optional.empty());
                given(userRepository.findActiveUserByEmail("abc@naver.com"))
                    .willReturn(Optional.of(user));

                // when, then
                assertThatCode(() -> userService.registerByOauth(userRegistrationRequestDto))
                    .isInstanceOf(ActiveAccountExistingException.class)
                    .hasMessage("동일한 이메일로 등록된 회원 계정이 존재합니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

                verify(userRepository, times(1)).findByName("abc");
                verify(userRepository, times(1)).findActiveUserByEmail("abc@naver.com");
            }
        }

        @DisplayName("이메일과 이름 모두 중복이 없는 경우")
        @Nested
        class Context_none_duplication {

            @DisplayName("회원을 생성한다.")
            @Test
            void it_throws_NameDuplicationException() {
                // given
                UserRegistrationRequestDto userRegistrationRequestDto =
                    UserRegistrationRequestDto.builder()
                        .name("abc")
                        .email("abc@naver.com")
                        .build();
                given(userRepository.findByName("abc")).willReturn(Optional.empty());
                given(userRepository.findActiveUserByEmail("abc@naver.com"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> userService.registerByOauth(userRegistrationRequestDto))
                    .doesNotThrowAnyException();
            }
        }
    }
}
