package com.spring.blog.post.domain.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.post.PostContentException;
import com.spring.blog.exception.post.PostTitleException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("PostContent 도메인 단위 테스트")
class PostContentTest {

    @DisplayName("PostContent 생성자는")
    @Nested
    class Describe_newPostContent {

        @DisplayName("게시물의 제목이 1~100자라면")
        @Nested
        class Context_title_from_1_to_100 {

            @DisplayName("게시물이 정상 생성된다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 100})
            void it_returns_PostContent(int length) {
                // given
                String title = String.join("", Collections.nCopies(length, "a"));

                // when
                PostContent postContent = new PostContent(title, "content");

                // then
                assertThat(postContent)
                    .usingRecursiveComparison()
                    .isEqualTo(new PostContent(title, "content"));
            }
        }

        @DisplayName("게시물의 제목이 null 혹은 공백이면")
        @Nested
        class Context_title_null_or_empty {

            @DisplayName("게시물 제목 생성 예외를 발생시킨다.")
            @ParameterizedTest
            @NullAndEmptySource
            void it_throws_PostTitleException(String title) {
                // given, when, then
                assertThatCode(() -> new PostContent(title, "content"))
                    .isInstanceOf(PostTitleException.class)
                    .hasMessage("게시글 제목은 공백이거나 100자를 초과할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 제목이 100자를 초과하면")
        @Nested
        class Context_title_over_100 {

            @DisplayName("게시물 제목 생성 예외를 발생시킨다.")
            @Test
            void it_throws_PostTitleException() {
                // given
                String title = String.join("", Collections.nCopies(101, "a"));

                // when, then
                assertThatCode(() -> new PostContent(title, "content"))
                    .isInstanceOf(PostTitleException.class)
                    .hasMessage("게시글 제목은 공백이거나 100자를 초과할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 내용 길이가 1~10000자라면")
        @Nested
        class Context_content_from_1_to_10000 {

            @DisplayName("게시물이 정상 생성된다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 10000})
            void it_returns_PostContent(int length) {
                // given
                String content = String.join("", Collections.nCopies(length, "a"));

                // when
                PostContent postContent = new PostContent("title", content);

                // then
                assertThat(postContent)
                    .usingRecursiveComparison()
                    .isEqualTo(new PostContent("title", content));
            }
        }

        @DisplayName("게시물의 내용이 null 혹은 공백이면")
        @Nested
        class Context_content_null_or_empty {

            @DisplayName("게시물 내용 생성 예외가 발생한다.")
            @ParameterizedTest
            @NullAndEmptySource
            void it_throws_PostContentException(String content) {
                // given, when, then
                assertThatCode(() -> new PostContent("title", content))
                    .isInstanceOf(PostContentException.class)
                    .hasMessage("게시글 내용은 1~10000자만 가능합니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물의 내용이 10000자를 초과하면")
        @Nested
        class Context_content_over_10000 {

            @DisplayName("게시물 내용 생성 예외가 발생한다.")
            @Test
            void it_throws_PostContentException() {
                // given
                String content = String.join("", Collections.nCopies(10001, "a"));

                // when, then
                assertThatCode(() -> new PostContent("title", content))
                    .isInstanceOf(PostContentException.class)
                    .hasMessage("게시글 내용은 1~10000자만 가능합니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
