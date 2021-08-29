package com.spring.blog.user.integration;


import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.application.UserService;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("UserService 통합 테스트")
@ActiveProfiles("test")
@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class UserServiceIntegration {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @DisplayName("User를 삭제한다.")
    @Test
    void withdraw_ValidRequest_Success() {
        // given
        User user = userRepository.save(new User("kevin", "image"));

        // when
        userService.withdarw(user.getId());
        User findUser = userRepository.findById(user.getId())
            .orElseThrow(UserNotFoundException::new);

        // then
        assertThat(findUser)
            .extracting("isDeleted")
            .isEqualTo(true);
    }
}
