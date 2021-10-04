package com.spring.blog.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.user.InvalidUserNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("User 엔티티 단위 테스트")
class UserTest {

    @DisplayName("회원탈퇴시 삭제 상태 정보가 True로 변경된다.")
    @Test
    void withdraw_isDeleted_True() {
        // given
        User user = new User("aaaaaaaaaa", "image");

        // when
        user.withdraw();

        // then
        assertThat(user)
            .extracting("isDeleted")
            .isEqualTo(true);
    }

    @DisplayName("이름은 2~10자만 가능하다.")
    @ParameterizedTest
    @ValueSource(strings = {"a", "aaaaaaaaaaa"})
    void newUser_invalidLength_Exception(String name) {
        // given, when, then
        assertThatCode(() -> new User(name, "abc@naver.com"))
            .isInstanceOf(InvalidUserNameException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
            .hasMessage("유저 이름은 2~10자입니다.");
    }
}
