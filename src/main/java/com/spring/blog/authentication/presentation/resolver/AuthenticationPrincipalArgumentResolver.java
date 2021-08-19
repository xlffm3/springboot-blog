package com.spring.blog.authentication.presentation.resolver;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.exception.authentication.InvalidTokenException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final OAuthService oAuthService;

    public AuthenticationPrincipalArgumentResolver(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (Objects.isNull(request)) {
            throw new InvalidTokenException();
        }
        String token = (String) request.getAttribute(HttpHeaders.AUTHORIZATION);
        return oAuthService.findRequestUserByToken(token);
    }
}
