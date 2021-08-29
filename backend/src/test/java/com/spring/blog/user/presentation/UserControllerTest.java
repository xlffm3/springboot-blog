package com.spring.blog.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.user.application.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("UserController 슬라이스 테스트")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("비로그인 유저는 회원 탈퇴를 할 수 없다.")
    @Test
    void withdraw_GuestUser_Failure() throws Exception {
        // given
        given(oAuthService.validateToken(any())).willReturn(false);

        // when, then
        mockMvc.perform(delete("/api/users/withdraw"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(oAuthService, times(1)).validateToken(any());
    }


    @DisplayName("로그인 유저는 회원 탈퇴를 할 수 있다.")
    @Test
    void withdraw_LoginUser_Success() throws Exception {
        // given
        given(oAuthService.validateToken("token")).willReturn(true);
        given(oAuthService.findRequestUserByToken("token"))
            .willReturn(new LoginUser(1L, "kevin"));
        Mockito.doNothing().when(userService).withdarw(1L);

        // when, then
        mockMvc.perform(delete("/api/users/withdraw")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isNoContent());

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1)).findRequestUserByToken("token");
        verify(userService, times(1)).withdarw(1L);
    }
}
