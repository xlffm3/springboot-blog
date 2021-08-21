package com.spring.blog.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.comment.domain.hierarchy.ChildComments;
import com.spring.blog.comment.domain.hierarchy.Hierarchy;
import com.spring.blog.exception.comment.CannotAddChildCommentException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.user.domain.User;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

@DisplayName("Comment 엔티티 단위 테스트")
class CommentTest {

    @DisplayName("addChildComment 메서드는")
    @Nested
    class Describe_addChildComment {

        @DisplayName("현재 부모 Comment의 Depth가 1~98인 경우")
        @Nested
        class Context_parent_comment_depth_from_1_to_98 {

            @DisplayName("자식 대댓글을 추가할 수 있다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 98})
            void it_adds_child_comment(int depth) {
                // given, when
                User user = new User("kevin", "image");
                Post post = new Post(new PostContent("title", "content"), user);
                Comment parent = new Comment("parent", post, user);
                parent.updateAsRoot();
                for (int i = 0; i < depth; i++) {
                    Comment child = new Comment("child", post, user);
                    parent.addChildComment(child);
                    parent = child;
                }

                // then
                assertThat(parent)
                    .extracting("hierarchy.depth")
                    .isEqualTo(depth + 1);
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
                Post post = new Post(new PostContent("title", "content"), user);
                Comment parent = new Comment("parent", post, user);
                parent.updateAsRoot();
                for (int i = 0; i < 98; i++) {
                    Comment child = new Comment("child", post, user);
                    parent.addChildComment(child);
                    parent = child;
                }

                // when, then
                Comment lastParent = parent;
                Comment lastChild = new Comment("child", post, user);
                assertThatCode(() -> lastParent.addChildComment(lastChild))
                    .isInstanceOf(CannotAddChildCommentException.class)
                    .hasMessage("대댓글을 추가할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DisplayName("updateHierarchy 메서드는")
    @Nested
    class Describe_updateHierarchy {

        @DisplayName("주어진 Root, Parent, Depth를")
        @Nested
        class Context_given_root_parent_depth {

            @DisplayName("자신의 계층 정보로 수정한다.")
            @Test
            void it_sets_given_information_as_hierarchy() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post(new PostContent("title", "content"), user);
                Comment target = new Comment("taret", post, user);
                Comment parentAndRoot = new Comment("parent and root", post, user);

                // when
                target.updateHierarchy(parentAndRoot, parentAndRoot, 13);
                Hierarchy expected =
                    new Hierarchy(parentAndRoot,
                        parentAndRoot,
                        new ChildComments(new ArrayList<>()),
                        13
                    );

                // then
                assertThat(target)
                    .extracting("hierarchy")
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
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
                Post post = new Post(new PostContent("title", "content"), user);
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
}
