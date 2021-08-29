package com.spring.blog.user.domain.repoistory;

import com.spring.blog.user.domain.User;
import java.util.Optional;

public interface CustomUserRepository {

    Optional<User> findActiveUserById(Long id);
}
