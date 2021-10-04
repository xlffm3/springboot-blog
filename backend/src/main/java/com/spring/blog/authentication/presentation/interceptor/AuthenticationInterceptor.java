package com.spring.blog.authentication.presentation.interceptor;

import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.presentation.util.AuthorizationExtractor;
import com.spring.blog.exception.authentication.InvalidTokenException;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Pattern COMMENT_IGNORE_PATTERN = Pattern.compile("/api/posts/.*/comments");
    private static final Pattern POST_IGNORE_PATTERN = Pattern.compile("/api/posts/.*");

    private final AuthService authService;

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
        if (!authService.validateToken(token)) {
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
        String requestURI = request.getRequestURI();
        boolean isGetMapping = request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString());
        boolean isReadingPosts = isGetMapping && requestURI.equals("/api/posts");
        boolean isReadingComments = isGetMapping
            && COMMENT_IGNORE_PATTERN.matcher(requestURI).matches();
        boolean isReadingPost = isGetMapping
            && POST_IGNORE_PATTERN.matcher(requestURI).matches();
        return isReadingPosts || isReadingComments || isReadingPost;
    }
}
