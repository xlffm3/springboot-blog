package com.spring.blog.comment.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.content.CommentContent;
import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.exception.comment.CommentDepthException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.user.domain.User;
import java.util.ArrayList;
import java.util.Arrays;
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
            @ValueSource(ints = {1, 99})
            void it_returns_Hierarchy(int depth) {
                // when
                Hierarchy hierarchy = new Hierarchy(null, null, null, depth);

                // then
                assertThat(hierarchy)
                    .usingRecursiveComparison()
                    .isEqualTo(new Hierarchy(null, null, null, depth));
            }
        }

        @DisplayName("Depth가 0이하거나 99를 초과하는 경우")
        @Nested
        class Context_depth_under_1_or_over_99 {

            @DisplayName("Hierarchy 생성 예외가 발생한다.")
            @ParameterizedTest
            @ValueSource(ints = {0, 100})
            void it_throws_CommentDepthException(int depth) {
                // given, when, then
                assertThatCode(() -> new Hierarchy(null, null, null, depth))
                    .isInstanceOf(CommentDepthException.class)
                    .hasMessage("유효한 댓글 계층은 1~99 입니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("addChildComment 메서드는")
    @Nested
    class Describe_addChildComment {

        @DisplayName("현재 계층이 1~98이면")
        @Nested
        class Context_hierarchy_depth_from_1_to_98 {

            @DisplayName("대댓글이 추가되며, 부모와 자식 댓글의 계층이 업데이트된다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 98})
            void it_adds_child_comment_successfully(int depth) {
                // given
                ChildComments childComments = new ChildComments(new ArrayList<>());
                User user = new User("kevin", "image");
                Post post = new Post(new PostContent("title", "content"), user);
                Comment parentComment = new Comment(new CommentContent("parent"), post, user);
                Comment childComment = new Comment(new CommentContent("child"), post, user);
                Hierarchy parentHierarchy =
                    new Hierarchy(parentComment, null, childComments, depth);

                // when
                parentHierarchy.addChildComment(parentComment, childComment);
                ChildComments expectedChildCommentsOfParent =
                    new ChildComments(Arrays.asList(childComment));
                Hierarchy expectedChildHierarchy =
                    new Hierarchy(
                        parentComment,
                        parentComment,
                        new ChildComments(new ArrayList<>()),
                        depth + 1
                    );

                // then
                assertThat(childComment)
                    .extracting("hierarchy")
                    .usingRecursiveComparison()
                    .isEqualTo(expectedChildHierarchy);

                assertThat(parentHierarchy)
                    .extracting("childComments")
                    .usingRecursiveComparison()
                    .isEqualTo(expectedChildCommentsOfParent);
            }
        }

        @DisplayName("현재 계층이 99면")
        @Nested
        class Context_hierarchy_depth_99 {

            @DisplayName("대댓글을 추가할 수 없다.")
            @Test
            void it_throws_CannotAddChildCommentException() {
                // given
                ChildComments childComments = new ChildComments(new ArrayList<>());
                User user = new User("kevin", "image");
                Post post = new Post(new PostContent("title", "content"), user);
                Comment parentComment = new Comment(new CommentContent("parent"), post, user);
                Comment childComment = new Comment(new CommentContent("child"), post, user);
                Hierarchy parentHierarchy =
                    new Hierarchy(parentComment, null, childComments, 99);

                // when, then
                assertThatCode(() -> parentHierarchy.addChildComment(parentComment, childComment))
                    .isInstanceOf(CannotAddChildCommentException.class)
                    .hasMessage("대댓글을 추가할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("update 메서드는")
    @Nested
    class Describe_update {

        @DisplayName("Depth가 1~99인 경우")
        @Nested
        class Context_depth_from_1_to_99 {

            @DisplayName("업데이트에 성공한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 99})
            void it_updates_Hierarchy(int depth) {
                // given
                Hierarchy hierarchy = new Hierarchy(null, null, null, 1);
                User user = new User("kevin", "image");
                Post post = new Post(new PostContent("title", "content"), user);
                Comment comment = new Comment(new CommentContent("hi"), post, user);

                // when
                hierarchy.update(comment, comment, depth);
                Hierarchy expected = new Hierarchy(comment, comment, null, depth);

                // then
                assertThat(hierarchy)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }
        }

        @DisplayName("Depth가 0이하거나 99를 초과하는 경우")
        @Nested
        class Context_depth_under_1_or_over_99 {

            @DisplayName("업데이트 예외가 발생한다.")
            @ParameterizedTest
            @ValueSource(ints = {0, 100})
            void it_throws_CommentDepthException(int depth) {
                // given
                Hierarchy hierarchy = new Hierarchy(null, null, null, 1);

                // when, then
                assertThatCode(() -> hierarchy.update(null, null, depth))
                    .isInstanceOf(CommentDepthException.class)
                    .hasMessage("유효한 댓글 계층은 1~99 입니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0003")
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
                Post post = new Post(new PostContent("title", "content"), user);
                Comment comment = new Comment(new CommentContent("root"), post, user);

                // when
                hierarchy.updateRoot(comment);

                // then
                assertThat(hierarchy)
                    .extracting("rootComment")
                    .isEqualTo(comment);
            }
        }
    }
}
