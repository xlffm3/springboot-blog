package com.spring.blog.authentication.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OAuthController.class)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("Github Login URL을 요청한다.")
    @Test
    void getGithubAuthorizationUrl_Valid_Success() throws Exception {
        // given
        given(oAuthService.getGithubAuthorizationUrl()).willReturn("https://github.com/authorize");

        // when, then
        mockMvc.perform(get("/api/authorization/github")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://github.com/authorize"));

        verify(oAuthService, times(1)).getGithubAuthorizationUrl();
    }

    @DisplayName("Github Login 인증 후 Token을 반환한다.")
    @Test
    void getLoginToken_Valid_Success() throws Exception {
        // given
        String code = "code.123.for.github";
        TokenResponseDto tokenResponseDto = new TokenResponseDto("user jwt token", "kevin");
        given(oAuthService.createToken(code)).willReturn(tokenResponseDto);

        // when, then
        mockMvc.perform(get("/api/afterlogin?code={code}", code)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("user jwt token"))
            .andExpect(jsonPath("$.userName").value("kevin"));

        verify(oAuthService, times(1)).createToken(code);
    }
}
