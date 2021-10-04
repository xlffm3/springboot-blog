package com.spring.blog.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.authentication.RegistrationRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("UserProfile 단위 테스트")
class UserProfileTest {

    @DisplayName("email이 null이면 예외가 발생한다.")
    @Test
    void newUserProfile_emailNull_ExceptionThrown() {
        // given, when, then
        assertThatCode(() -> new UserProfile("abc", null))
            .isInstanceOf(RegistrationRequiredException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
