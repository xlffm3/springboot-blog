package com.spring.blog.user.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @DeleteMapping("/users/withdraw")
    public ResponseEntity<Void> delete(@Authenticated AppUser appUser) {
        userService.withdraw(appUser.getId());
        return ResponseEntity.noContent()
            .build();
    }
}
