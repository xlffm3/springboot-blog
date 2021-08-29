package com.spring.blog.user.application;

import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void withdarw(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        user.withdraw();
    }
}
