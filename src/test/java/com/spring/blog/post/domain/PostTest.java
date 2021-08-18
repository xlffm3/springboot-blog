package com.spring.blog.post.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.post.CannotAddChildPostException;
import com.spring.blog.post.domain.content.PostContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Post 엔티티 단위 테스트")
class PostTest {

    @DisplayName("addChildPost 메서드는")
    @Nested
    class Describe_addChildPost {

        @DisplayName("부모 Post의 계층이 98인 경우")
        @Nested
        class Context_parent_hierarchy_one_to_ninety_eight {

            @DisplayName("하위 자식 Post를 추가할 수 있다.")
            @Test
            void it_adds_child_post() {
                // given
                Post parentPost = new Post(new PostContent("title", "content"));
                for (int i = 0; i < 97; i++) {
                    Post childPost = new Post(new PostContent("child title", "child content"));
                    parentPost.addChildPost(childPost);
                    parentPost = childPost;
                }

                // when. then
                Post parentPostWith98Depth = parentPost;
                Post childPost = new Post(new PostContent("child title", "child content"));

                assertThatCode(() -> parentPostWith98Depth.addChildPost(childPost))
                    .doesNotThrowAnyException();
            }
        }

        @DisplayName("부모 Post의 계층이 99인 경우")
        @Nested
        class Context_parent_hierarchy_ninety_nine {

            @DisplayName("하위 자식 Post를 추가할 수 없다.")
            @Test
            void it_throws_CannotAddChildPostException() {
                // given
                Post parentPost = new Post(new PostContent("title", "content"));
                for (int i = 0; i < 98; i++) {
                    Post childPost = new Post(new PostContent("child title", "child content"));
                    parentPost.addChildPost(childPost);
                    parentPost = childPost;
                }

                // when, then
                Post parentPostWith99Depth = parentPost;
                Post childPost = new Post(new PostContent("child title", "child content"));

                assertThatCode(() -> parentPostWith99Depth.addChildPost(childPost))
                    .isInstanceOf(CannotAddChildPostException.class)
                    .hasMessage("답글을 추가할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
