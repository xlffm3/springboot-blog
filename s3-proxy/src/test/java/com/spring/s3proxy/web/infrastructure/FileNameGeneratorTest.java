package com.spring.s3proxy.web.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.s3proxy.common.FileFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("FileNameGenerator 단위 테스트")
class FileNameGeneratorTest {

    private final FileNameGenerator fileNameGenerator = new FileNameGenerator();

    @DisplayName("generateFileName 메서드는")
    @Nested
    class Describe_generateFileName {

        @DisplayName("MultipartFile과 UserName이 정상적으로 주어지면")
        @Nested
        class Context_valid_multipartFile_userName {

            @DisplayName("이름을 해싱하되, 확장자는 동일하게 반환한다.")
            @ParameterizedTest
            @MethodSource("getMockData")
            @Test
            void it_returns_hashed_file_with_extension() {
                // given
                MultipartFile image = FileFactory.getTestSuccessImage1();
                String userName = "kevin";

                // when
                String fileName = fileNameGenerator.generateFileName(image, userName);

                // then
                assertThat(fileName).endsWith(".png");
            }

            @DisplayName("파일 이름은 해싱된 32자의 문자열이다.")
            @Test
            void it_returns_hashed_with_16_length() {
                // given
                MultipartFile image = FileFactory.getTestSuccessImage1();
                String userName = "kevin";

                // when
                String fileName = fileNameGenerator.generateFileName(image, userName);
                int lastIndex = fileName.lastIndexOf(".");
                String extracted = fileName.substring(0, lastIndex);

                // then
                assertThat(extracted).hasSize(32);
            }
        }
    }
}
