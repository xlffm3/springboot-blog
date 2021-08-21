package com.spring.blog.authentication.presentation.interceptor;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.presentation.util.AuthorizationExtractor;
import com.spring.blog.exception.authentication.InvalidTokenException;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Pattern COMMENT_IGNORE_PATTERN = Pattern.compile("/api/posts/.*/comments");

    private final OAuthService oAuthService;

    public AuthenticationInterceptor(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        if (isPreflightRequest(request)) {
            return true;
        }
        if (isAbleToIgnoreAuthorization(request)) {
            return true;
        }
        String token = AuthorizationExtractor.extract(request);
        if (!oAuthService.validateToken(token)) {
            throw new InvalidTokenException();
        }
        request.setAttribute(HttpHeaders.AUTHORIZATION, token);
        return true;
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptions(request)
            && hasAccessControlRequestHeaders(request)
            && hasAccessControlRequestMethod(request)
            && hasOrigin(request);
    }

    private boolean isOptions(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    private boolean hasAccessControlRequestHeaders(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));
    }

    private boolean hasAccessControlRequestMethod(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
    }

    private boolean hasOrigin(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ORIGIN));
    }

    private boolean isAbleToIgnoreAuthorization(HttpServletRequest request) {
        boolean isReadingPosts = request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString())
            && request.getRequestURI().equals("/api/posts");
        boolean isReadingComments = request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString())
            && COMMENT_IGNORE_PATTERN.matcher(request.getRequestURI()).matches();
        return isReadingPosts || isReadingComments;
    }

    private boolean isPostListReadRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString())
            && request.getRequestURI().equals("/api/posts");
    }
}
