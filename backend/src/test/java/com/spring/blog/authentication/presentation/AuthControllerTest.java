package com.spring.blog.authentication.presentation;

import static com.spring.blog.common.ApiDocumentUtils.getDocumentRequest;
import static com.spring.blog.common.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("AuthController 슬라이스 테스트")
@AutoConfigureRestDocs
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @DisplayName("Github Login URL을 요청한다.")
    @Test
    void getGithubAuthorizationUrl_Valid_Success() throws Exception {
        // given
        given(authService.getOAuthLoginUrl("github")).willReturn("https://github.com/authorize");

        // when, then
        ResultActions resultActions = mockMvc.perform(get("/api/authorization/github")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://github.com/authorize"));

        verify(authService, times(1)).getOAuthLoginUrl("github");

        // restDocs
        resultActions.andDo(document("github-login-url-request",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("url").description("Github 로그인 URL")))
        );
    }

    @DisplayName("Github Login 인증 후 Token을 반환한다.")
    @Test
    void getLoginToken_Valid_Success() throws Exception {
        // given
        String code = "code.123.for.github";
        TokenResponseDto tokenResponseDto = new TokenResponseDto("user jwt token", "kevin");
        given(authService.loginByOauth("github", code)).willReturn(tokenResponseDto);

        // when, then
        ResultActions resultActions = mockMvc.perform(get("/api/github/login?code={code}", code)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("user jwt token"))
            .andExpect(jsonPath("$.userName").value("kevin"));

        verify(authService, times(1)).loginByOauth("github", code);

        // restDocs
        resultActions.andDo(document("github-afterlogin",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("token").description("JWT 인증 토큰"),
                fieldWithPath("userName").description("유저 이름")))
        );
    }
}
