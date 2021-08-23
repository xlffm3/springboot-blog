package com.spring.s3proxy.common;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.s3proxy.exception.format.FileExtensionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("ImageFileValidator 단위 테스트")
class ImageFileValidatorTest {

    private final FileValidator fileValidator = new ImageFileValidator();

    @DisplayName("validate 메서드는")
    @Nested
    class Describe_validate {

        @DisplayName("주어진 파일이 이미지가 아닌 경우")
        @Nested
        class Context_not_image {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_FileExtensionException() {
                // given
                MultipartFile testFailData = FileFactory.getTestFailImage1();

                // when, then
                assertThatCode(() -> fileValidator.validate(testFailData))
                    .isInstanceOf(FileExtensionException.class);
            }
        }

        @DisplayName("주어진 파일이 이미지인 경우")
        @Nested
        class Context_image {

            @DisplayName("예외를 발생시키지 않는다.")
            @Test
            void it_throws_FileExtensionException() {
                // given
                MultipartFile testSuccessData = FileFactory.getTestSuccessImage1();

                // when, then
                assertThatCode(() -> fileValidator.validate(testSuccessData))
                    .doesNotThrowAnyException();
            }
        }
    }
}
