package com.spring.blog.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.post.CannotDeletePostException;
import com.spring.blog.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Post 엔티티 단위 테스트")
class PostTest {

    @DisplayName("delete 메서드는")
    @Nested
    class Describe_delete {

        @DisplayName("게시물 작성자와 삭제 시도자가 다르면")
        @Nested
        class Context_writer_deleter_different {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_CannotDeletePostException() {
                // given
                User user = new User(1L, "kevin", "pg");
                User another = new User(2L, "ama", "kk");
                Post post = new Post("title", "content", user);

                // when, then
                assertThatCode(() -> post.delete(another))
                    .isInstanceOf(CannotDeletePostException.class)
                    .hasMessage("게시물을 삭제할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0005")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("게시물 작성자와 삭제 시도자가 동일하면")
        @Nested
        class Context_writer_deleter_same {

            @Test
            void it_removes_post() {
                // given
                User user = new User(1L, "kevin", "pg");
                Post post = new Post("title", "content", user);

                // when
                post.delete(user);

                // then
                assertThat(post)
                    .extracting("isDeleted")
                    .isEqualTo(true);
            }
        }
    }
}
