package com.spring.blog.post.domain.content;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.post.InvalidContentException;
import com.spring.blog.exception.post.InvalidTitleException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;

@DisplayName("PostContent 도메인 단위 테스트")
class PostContentTest {

    @DisplayName("PostContent 생성자는")
    @Nested
    class Describe_newPostContent {

        @DisplayName("게시물의 제목이 null 혹은 공백이면")
        @Nested
        class Context_title_null_or_empty {

            @DisplayName("게시물 제목 생성 예외가 발생한다.")
            @ParameterizedTest
            @NullAndEmptySource
            void it_throws_InvalidTitleException(String title) {
                // given, when, then
                assertThatCode(() -> new PostContent(title, "content"))
                    .isInstanceOf(InvalidTitleException.class)
                    .hasMessage("게시글 제목은 공백이거나 100자를 초과할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 제목이 1~100자라면")
        @Nested
        class Context_title_one_to_hundred {

            @DisplayName("게시물이 정상 생성된다.")
            @Test
            void it_returns_valid_PostContent_instance() {
                // given
                String longTitle = String.join("", Collections.nCopies(100, "a"));
                String shortTitle = "a";

                // when, then
                assertThatCode(() -> {
                    new PostContent(longTitle, "content");
                    new PostContent(shortTitle, "content");
                }).doesNotThrowAnyException();
            }
        }

        @DisplayName("게시물의 제목이 100자를 초과하")
        @Nested
        class Context_title_over_hundred {

            @DisplayName("게시물이 정상 생성된다.")
            @Test
            void it_returns_valid_PostContent_instance() {
                // given
                String title = String.join("", Collections.nCopies(101, "a"));

                // when, then
                assertThatCode(() -> new PostContent(title, "content"))
                    .isInstanceOf(InvalidTitleException.class)
                    .hasMessage("게시글 제목은 공백이거나 100자를 초과할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 내용이 null 혹은 공백이면")
        @Nested
        class Context_content_null_or_empty {

            @DisplayName("게시물 내용 생성 예외가 발생한다.")
            @ParameterizedTest
            @NullAndEmptySource
            void it_throws_InvalidContentException(String content) {
                // given, when, then
                assertThatCode(() -> new PostContent("title", content))
                    .isInstanceOf(InvalidContentException.class)
                    .hasMessage("게시글 내용은 공백일 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 내용 길이가 정상이라면")
        @Nested
        class Context_valid_content {

            @DisplayName("게시물이 정상 생성된다.")
            @Test
            void it_returns_valid_PostContent_instance() {
                // given
                String content = "valid ipsem abc blog test spring java";

                // when, then
                assertThatCode(() -> new PostContent("title", content))
                    .doesNotThrowAnyException();
            }
        }
    }
}
