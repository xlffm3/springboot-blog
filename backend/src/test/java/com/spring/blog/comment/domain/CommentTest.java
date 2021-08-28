package com.spring.blog.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.exception.comment.CannotEditCommentException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("Comment 엔티티 단위 테스트")
class CommentTest {

    @DisplayName("updateChildCommentHierarchy 메서드는")
    @Nested
    class Describe_updateChildCommentHierarchy {

        @DisplayName("현재 부모 Comment의 Depth가 1~98인 경우")
        @Nested
        class Context_parent_comment_depth_from_1_to_98 {

            @DisplayName("자식 대댓글을 추가할 수 있으며, 자식 대댓글 계층이 변경된다.")
            @ParameterizedTest
            @ValueSource(longs = {1, 98})
            void it_adds_child_comment(long depth) {
                // given, when
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment parent = new Comment("parent", post, user);
                parent.updateAsRoot();
                for (int i = 0; i < depth; i++) {
                    Comment child = new Comment("child", post, user);
                    parent.updateChildCommentHierarchy(child);
                    parent = child;
                }

                // then
                assertThat(parent)
                    .extracting("hierarchy.depth", "hierarchy.leftNode", "hierarchy.rightNode")
                    .containsExactly(depth + 1, depth + 1, depth + 2);
            }
        }

        @DisplayName("현재 부모 Comment의 Depth가 99인 경우")
        @Nested
        class Context_parent_comment_depth_99 {

            @DisplayName("자식 대댓글을 추가할 수 없다.")
            @Test
            void it_throws_CannotAddChildCommentException() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment parent = new Comment("parent", post, user);
                parent.updateAsRoot();
                for (int i = 0; i < 98; i++) {
                    Comment child = new Comment("child", post, user);
                    parent.updateChildCommentHierarchy(child);
                    parent = child;
                }

                // when, then
                Comment lastParent = parent;
                Comment lastChild = new Comment("child", post, user);
                assertThatCode(() -> lastParent.updateChildCommentHierarchy(lastChild))
                    .isInstanceOf(CannotAddChildCommentException.class)
                    .hasMessage("대댓글을 추가할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("updateAsRoot 메서드는")
    @Nested
    class Describe_updateAsRoot {

        @DisplayName("자기 자신을")
        @Nested
        class Context_comment_itself {

            @DisplayName("Root Comment로 지정한다.")
            @Test
            void it_sets_itself_as_root() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("root", post, user);

                // when
                comment.updateAsRoot();

                // then
                assertThat(comment)
                    .extracting("hierarchy.rootComment")
                    .isEqualTo(comment);
            }
        }
    }

    @DisplayName("editContent 메서드는")
    @Nested
    class Describe_editContent {

        @DisplayName("작성자와 수정자가 다르면")
        @Nested
        class Context_writer_editor_different {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_CannotEditCommentException() {
                // given
                User writer = new User(1L, "kevin", "image.url");
                User editor = new User(2L, "ginger", "image.url");
                Post post = new Post("title", "content", writer);
                Comment comment = new Comment("comment", post, writer);

                // when, then
                assertThatCode(() -> comment.editContent("change", editor))
                    .isInstanceOf(CannotEditCommentException.class)
                    .hasMessage("댓글을 수정할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }

        @DisplayName("작성자와 수정자가 동일하면")
        @Nested
        class Context_writer_editor_same {

            @DisplayName("댓글이 수정된다.")
            @Test
            void it_throws_CannotEditCommentException() {
                // given
                User writer = new User(1L, "kevin", "image.url");
                Post post = new Post("title", "content", writer);
                Comment comment = new Comment("comment", post, writer);

                // when
                comment.editContent("change comment", writer);

                // then
                assertThat(comment)
                    .extracting("commentContent.content")
                    .isEqualTo("change comment");
            }
        }
    }
}
