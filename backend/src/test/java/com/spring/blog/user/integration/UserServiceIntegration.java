package com.spring.blog.user.integration;


import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.IntegrationTest;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.application.UserService;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UserService 통합 테스트")
class UserServiceIntegration extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("User를 삭제한다.")
    @Test
    void withdraw_ValidRequest_Success() {
        // given
        User user = userRepository.save(new User("kevin", "image"));

        // when
        userService.withdraw(user.getId());
        User findUser = userRepository.findById(user.getId())
            .orElseThrow(UserNotFoundException::new);

        // then
        assertThat(findUser)
            .extracting("isDeleted")
            .isEqualTo(true);
    }
}
