package com.spring.blog.user.application;

import com.spring.blog.exception.user.ActiveAccountExistingException;
import com.spring.blog.exception.user.NameDuplicationException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.application.dto.UserRegistrationRequestDto;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Optional;
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
    public void registerByOauth(UserRegistrationRequestDto userRegistrationRequestDto) {
        String name = userRegistrationRequestDto.getName();
        String email = userRegistrationRequestDto.getEmail();
        validateDuplication(name, email);
        userRepository.save(new User(name, email));
    }

    private void validateDuplication(String name, String email) {
        Optional<User> user = userRepository.findByName(name);
        if (user.isPresent()) {
            throw new NameDuplicationException();
        }
        Optional<User> activeUser = userRepository.findActiveUserByEmail(email);
        if (activeUser.isPresent()) {
            throw new ActiveAccountExistingException();
        }
    }
}
