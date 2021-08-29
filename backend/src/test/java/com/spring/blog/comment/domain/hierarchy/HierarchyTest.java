package com.spring.blog.comment.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.exception.comment.CommentDepthException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("Hierarchy 도메인 단위 테스트")
class HierarchyTest {

    @DisplayName("Hierarchy 생성자는")
    @Nested
    class Describe_newHierarchy {

        @DisplayName("Depth가 1~99인 경우")
        @Nested
        class Context_depth_from_1_to_99 {

            @DisplayName("Hierarchy를 정상 생성한다.")
            @ParameterizedTest
            @ValueSource(longs = {1, 99})
            void it_returns_Hierarchy(long depth) {
                // given, when
                Hierarchy hierarchy = new Hierarchy(null, null, null, null, depth);

                // then
                assertThat(hierarchy)
                    .usingRecursiveComparison()
                    .isEqualTo(new Hierarchy(null, null, null, null, depth));
            }
        }

        @DisplayName("Depth가 0이하거나 99를 초과하는 경우")
        @Nested
        class Context_depth_under_1_or_over_99 {

            @DisplayName("Hierarchy 생성 예외가 발생한다.")
            @ParameterizedTest
            @ValueSource(longs = {0, 100})
            void it_throws_CommentDepthException(long depth) {
                // given, when, then
                assertThatCode(() -> new Hierarchy(null, null, null, null, depth))
                    .isInstanceOf(CommentDepthException.class)
                    .hasMessage("유효한 댓글 계층은 1~99 입니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("updateChildCommentHierarchy 메서드는")
    @Nested
    class Describe_updateChildCommentHierarchy {

        @DisplayName("현재 계층이 1~98이면")
        @Nested
        class Context_hierarchy_depth_from_1_to_98 {

            @DisplayName("대댓글이 추가되며, 자식 댓글의 계층이 업데이트된다.")
            @ParameterizedTest
            @ValueSource(longs = {1, 98})
            void it_adds_child_comment_successfully(long depth) {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment parentComment = new Comment("parent", post, user);
                Hierarchy parentHierarchy =
                    new Hierarchy(parentComment, null, 1L, 2L, depth);
                Hierarchy childHierarchy = new Hierarchy();

                // when
                parentHierarchy.updateChildHierarchy(parentComment, childHierarchy);
                Hierarchy expectedChildHierarchy =
                    new Hierarchy(parentComment, parentComment, 2L, 3L, depth + 1);

                // then
                assertThat(childHierarchy)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedChildHierarchy);
            }
        }

        @DisplayName("현재 계층이 99면")
        @Nested
        class Context_hierarchy_depth_99 {

            @DisplayName("대댓글을 추가할 수 없다.")
            @Test
            void it_throws_CannotAddChildCommentException() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment parentComment = new Comment("parent", post, user);
                Comment childComment = new Comment("child", post, user);
                Hierarchy parentHierarchy =
                    new Hierarchy(parentComment, null, 1L, 2L, 99L);
                Hierarchy childHierarchy = new Hierarchy();

                // when, then
                assertThatCode(() -> parentHierarchy.updateChildHierarchy(parentComment, childHierarchy))
                    .isInstanceOf(CannotAddChildCommentException.class)
                    .hasMessage("대댓글을 추가할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0006")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("updateRoot 메서드는")
    @Nested
    class Describe_updateRoot {

        @DisplayName("주어진 Comment를")
        @Nested
        class Context_given_comment {

            @DisplayName("Root Comment로 설정한다.")
            @Test
            void it_sets_given_comment_as_root() {
                // given
                Hierarchy hierarchy = new Hierarchy();
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("root", post, user);

                // when
                hierarchy.updateAsRoot(comment);

                // then
                assertThat(hierarchy)
                    .extracting("rootComment")
                    .isEqualTo(comment);
            }
        }
    }
}
