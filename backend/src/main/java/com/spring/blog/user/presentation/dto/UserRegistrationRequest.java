package com.spring.blog.user.presentation.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRegistrationRequest {

    @NotBlank
    @Pattern(regexp = "[a-zA-Z가-힣]{2,10}")
    private String name;

    @NotBlank
    @Email
    private String email;
}
