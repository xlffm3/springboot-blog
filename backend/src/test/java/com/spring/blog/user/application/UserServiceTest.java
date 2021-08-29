package com.spring.blog.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
                User user = new User(1L,"kevin", "image");
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
}
