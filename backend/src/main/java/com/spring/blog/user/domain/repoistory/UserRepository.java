package com.spring.blog.user.domain.repoistory;

import com.spring.blog.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByName(String name);
}
