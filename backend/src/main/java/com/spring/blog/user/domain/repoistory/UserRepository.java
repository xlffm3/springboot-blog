package com.spring.blog.user.domain.repoistory;

import com.spring.blog.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
}
