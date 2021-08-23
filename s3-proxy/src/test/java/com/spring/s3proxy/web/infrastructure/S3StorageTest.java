package com.spring.s3proxy.web.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.spring.s3proxy.common.FileFactory;
import com.spring.s3proxy.exception.upload.UploadFailureException;
import com.spring.s3proxy.web.domain.StoreResult;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("S3Storage 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class S3StorageTest {

    @InjectMocks
    private S3Storage s3Storage;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private FileNameGenerator fileNameGenerator;

    private String fileUrlFormat = "https://d2lkb1z978lt7d.cloudfront.net/images/%s";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Storage, "fileUrlFormat", fileUrlFormat);
    }

    @DisplayName("store 메서드는")
    @Nested
    class Describe_store {

        @DisplayName("이미지와 유저 이름이 주어지면")
        @Nested
        class Context_images_and_userName {

            @DisplayName("이미지를 저장하고 결과를 반환한다.")
            @Test
            void it_saves_images_and_returns_result() {
                // given
                List<MultipartFile> images = Arrays.asList(
                    FileFactory.getTestSuccessImage1(),
                    FileFactory.getTestSuccessImage2()
                );
                String userName = "kevin";
                given(fileNameGenerator.generateFileName(any(MultipartFile.class), eq(userName)))
                    .willCallRealMethod();
                given(amazonS3.putObject(any(), any(), any(), any()))
                    .willReturn(new PutObjectResult());

                // when
                List<StoreResult> storeResults = s3Storage.store(images, userName);
                List<StoreResult> expected = generateExpectedResults(images, userName);

                // then
                assertThat(storeResults)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);

                verify(fileNameGenerator, times(2))
                    .generateFileName(any(MultipartFile.class), eq(userName));
                verify(amazonS3, times(2)).putObject(any(), any(), any(), any());
            }

            private List<StoreResult> generateExpectedResults(
                List<MultipartFile> images,
                String userName
            ) {
                FileNameGenerator fileNameGenerator = new FileNameGenerator();
                String first = fileNameGenerator.generateFileName(images.get(0), userName);
                String second = fileNameGenerator.generateFileName(images.get(1), userName);
                return Arrays.asList(
                    new StoreResult(first, String.format(fileUrlFormat, first)),
                    new StoreResult(second, String.format(fileUrlFormat, second))
                );
            }
        }

        @DisplayName("업로드에 실패하면")
        @Nested
        class Context_upload_fail {

            @DisplayName("예외를 반환한다.")
            @Test
            void it_throws_UploadFailureException() {
                // given
                List<MultipartFile> images = Arrays.asList(
                    FileFactory.getTestSuccessImage1(),
                    FileFactory.getTestSuccessImage2()
                );
                String userName = "kevin";
                given(fileNameGenerator.generateFileName(any(MultipartFile.class), eq(userName)))
                    .willCallRealMethod();
                given(amazonS3.putObject(any(), any(), any(), any()))
                    .willThrow(RuntimeException.class);

                // when, then
                assertThatCode(() -> s3Storage.store(images, userName))
                    .isInstanceOf(UploadFailureException.class)
                    .hasMessage("업로드에 실패했습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "I0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

                verify(fileNameGenerator, times(1))
                    .generateFileName(any(MultipartFile.class), eq(userName));
                verify(amazonS3, times(1)).putObject(any(), any(), any(), any());
            }
        }
    }
}
