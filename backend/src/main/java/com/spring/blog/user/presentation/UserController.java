package com.spring.blog.user.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.user.application.UserService;
import com.spring.blog.user.application.dto.UserRegistrationRequestDto;
import com.spring.blog.user.presentation.dto.UserRegistrationRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/users/oauth")
    public ResponseEntity<Void> registerByOauth(
        @RequestBody UserRegistrationRequest userRegistrationRequest
    ) {
        UserRegistrationRequestDto userRegistrationRequestDto = UserRegistrationRequestDto.builder()
            .email(userRegistrationRequest.getEmail())
            .name(userRegistrationRequest.getName())
            .build();
        userService.register(userRegistrationRequestDto);
        return ResponseEntity.created(URI.create("/")).build();
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> delete(@Authenticated AppUser appUser) {
        userService.withdraw(appUser.getId());
        return ResponseEntity.noContent()
            .build();
    }
}
