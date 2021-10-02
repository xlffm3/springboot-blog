package com.spring.blog.user.application;

import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.application.dto.UserRegistrationRequestDto;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findActiveUserById(id)
            .orElseThrow(UserNotFoundException::new);
        user.withdraw();
    }

    @Transactional
    public void register(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = new User(
            userRegistrationRequestDto.getName(),
            userRegistrationRequestDto.getEmail()
        );
        userRepository.save(user);
    }
}
