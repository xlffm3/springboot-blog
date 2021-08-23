package com.spring.s3proxy.config.resolver;

import com.spring.s3proxy.common.FileValidator;
import com.spring.s3proxy.web.presentation.dto.FilesRequest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class ValidExtensionArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String FILE_NAME = "files";
    private static final String USER_NAME = "userName";

    private final List<FileValidator> fileValidators;

    public ValidExtensionArgumentResolver(List<FileValidator> fileValidators) {
        this.fileValidators = fileValidators;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ValidExtension.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        MultipartHttpServletRequest multipartHttpServletRequest =
            (MultipartHttpServletRequest) nativeRequest;
        List<MultipartFile> files = multipartHttpServletRequest.getFiles(FILE_NAME);
        files.forEach(this::validate);
        String userName = (String) multipartHttpServletRequest.getAttribute(USER_NAME);
        return new FilesRequest(userName, files);
    }

    private void validate(MultipartFile multipartFile) {
        fileValidators.forEach(fileValidator -> fileValidator.validate(multipartFile));
    }
}
