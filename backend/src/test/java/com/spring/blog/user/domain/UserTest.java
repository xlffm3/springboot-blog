package com.spring.blog.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User 엔티티 단위 테스트")
class UserTest {

    @DisplayName("회원탈퇴시 삭제 상태 정보가 True로 변경된다.")
    @Test
    void withdraw_isDeleted_True() {
        // given
        User user = new User("kevin", "image");
        user.activate();

        // when
        user.withdraw();

        // then
        assertThat(user)
            .extracting("isDeleted")
            .isEqualTo(true);
    }

    @DisplayName("활성화시 삭제 상태 정보가 False로 변경된다.")
    @Test
    void activate_isDeleted_False() {
        // given
        User user = new User("kevin", "image");
        user.withdraw();

        // when
        user.activate();

        // then
        assertThat(user)
            .extracting("isDeleted")
            .isEqualTo(false);
    }
}
