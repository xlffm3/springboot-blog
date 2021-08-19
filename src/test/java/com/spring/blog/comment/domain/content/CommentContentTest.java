package com.spring.blog.comment.domain.content;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.comment.CommentContentException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("CommentContent 도메인 단위 테스트")
class CommentContentTest {

    @DisplayName("CommentContent 생성자는")
    @Nested
    class Describe_newCommentContent {

        @DisplayName("댓글 내용이 null이거나 공백일 때")
        @Nested
        class Context_comment_null_or_empty {

            @DisplayName("댓글 내용 생성 예외가 발생한다.")
            @ParameterizedTest
            @NullAndEmptySource
            void it_throws_CommentContentException(String content) {
                // given, when, then
                assertThatCode(() -> new CommentContent(content))
                    .isInstanceOf(CommentContentException.class)
                    .hasMessage("댓글은 1자 이상 140자 이하만 가능합니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);

            }
        }

        @DisplayName("댓글 내용이 140자를 초과할 때")
        @Nested
        class Context_comment_over_140 {

            @DisplayName("댓글 내용 생성 예외가 발생한다.")
            @Test
            void it_throws_CommentContentException() {
                // given
                String content = String.join("", Collections.nCopies(141, "a"));

                // when, then
                assertThatCode(() -> new CommentContent(content))
                    .isInstanceOf(CommentContentException.class)
                    .hasMessage("댓글은 1자 이상 140자 이하만 가능합니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("댓글이 1~140자일 때")
        @Nested
        class Context_comment_from_one_to_140 {

            @DisplayName("댓글 내용이 정상 생성된다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 140})
            void it_returns_CommentContent(int length) {
                // given
                String content = String.join("", Collections.nCopies(length, "a"));

                // when, then
                assertThatCode(() -> new CommentContent(content))
                    .doesNotThrowAnyException();
            }
        }
    }
}
