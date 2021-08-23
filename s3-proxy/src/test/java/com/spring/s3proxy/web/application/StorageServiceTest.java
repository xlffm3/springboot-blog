package com.spring.s3proxy.web.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.s3proxy.common.FileFactory;
import com.spring.s3proxy.exception.upload.UploadFailureException;
import com.spring.s3proxy.web.application.dto.FilesRequestDto;
import com.spring.s3proxy.web.application.dto.FilesResponseDto;
import com.spring.s3proxy.web.domain.FileStorage;
import com.spring.s3proxy.web.domain.StoreResult;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@DisplayName("StorageService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @InjectMocks
    private StorageService storageService;

    @Mock
    private FileStorage fileStorage;

    @DisplayName("store 메서드는")
    @Nested
    class Describe_store {

        @DisplayName("복수개의 이미지를 담은 파일 DTO가 주어지면")
        @Nested
        class Context_multiple_images {

            @DisplayName("업로드한 뒤 결과 URL을 반환한다.")
            @Test
            void it_returns_image_urls() {
                // given
                FilesRequestDto filesRequestDto =
                    new FilesRequestDto("kevin",
                        Arrays.asList(FileFactory.getTestSuccessImage1(),
                            FileFactory.getTestSuccessImage2()
                        )
                    );
                List<StoreResult> storeResults = Arrays.asList(
                    new StoreResult("file1", "aws/file1"),
                    new StoreResult("file2", "aws/file2")
                );
                FilesResponseDto expected = new FilesResponseDto(storeResults.stream()
                    .map(StoreResult::getUrl)
                    .collect(Collectors.toList())
                );
                given(fileStorage.store(filesRequestDto.getFiles(), filesRequestDto.getUserName()))
                    .willReturn(storeResults);

                // when
                FilesResponseDto filesResponseDto = storageService.store(filesRequestDto);

                // then
                assertThat(filesResponseDto)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);

                verify(fileStorage, times(1))
                    .store(filesRequestDto.getFiles(), filesRequestDto.getUserName());
            }
        }
    }

    @DisplayName("업로드에 실패하면")
    @Nested
    class Context_upload_failure {

        @DisplayName("예외가 발생한다.")
        @Test
        void it_throws_exception() {
            // given
            FilesRequestDto filesRequestDto =
                new FilesRequestDto("kevin",
                    Arrays.asList(FileFactory.getTestSuccessImage1(),
                        FileFactory.getTestSuccessImage2()
                    )
                );
            given(fileStorage.store(filesRequestDto.getFiles(), filesRequestDto.getUserName()))
                .willThrow(new UploadFailureException());

            // when, then
            assertThatCode(() -> storageService.store(filesRequestDto))
                .isInstanceOf(UploadFailureException.class)
                .hasMessage("업로드에 실패했습니다.")
                .hasFieldOrPropertyWithValue("errorCode", "I0001")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

            verify(fileStorage, times(1))
                .store(filesRequestDto.getFiles(), filesRequestDto.getUserName());
        }
    }
}
