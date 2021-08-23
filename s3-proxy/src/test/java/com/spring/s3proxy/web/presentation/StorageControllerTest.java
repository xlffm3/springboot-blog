package com.spring.s3proxy.web.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.s3proxy.common.FileFactory;
import com.spring.s3proxy.web.application.StorageService;
import com.spring.s3proxy.web.application.dto.FilesRequestDto;
import com.spring.s3proxy.web.application.dto.FilesResponseDto;
import com.spring.s3proxy.web.presentation.dto.FilesResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("StorageController 슬라이스 테스트")
@WebMvcTest(StorageController.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("이미지 파일을 정상적으로 업로드한다.")
    @Test
    void store_Image_Success() throws Exception {
        // given
        List<MultipartFile> images = Arrays.asList(
            FileFactory.getTestSuccessImage1(), FileFactory.getTestSuccessImage2()
        );
        FilesResponseDto filesResponseDto = new FilesResponseDto(Arrays.asList("url1", "url2"));
        FilesResponse expected = new FilesResponse(Arrays.asList("url1", "url2"));

        given(storageService.store(any(FilesRequestDto.class))).willReturn(filesResponseDto);

        // when, then
        mockMvc.perform(multipart("/api/storage")
            .file((MockMultipartFile) images.get(0))
            .file((MockMultipartFile) images.get(1))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @DisplayName("이미지 파일이 아닌 파일은 업로드에 실패한다.")
    @Test
    void store_NotImage_Success() throws Exception {
        // given
        List<MultipartFile> images = Arrays.asList(
            FileFactory.getTestSuccessImage1(), FileFactory.getTestFailData()
        );

        // when, then
        mockMvc.perform(multipart("/api/storage")
            .file((MockMultipartFile) images.get(0))
            .file((MockMultipartFile) images.get(1))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("I0002"));
    }
}
