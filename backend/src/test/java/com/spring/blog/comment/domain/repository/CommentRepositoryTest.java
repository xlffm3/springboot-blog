package com.spring.blog.comment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.configuration.JpaTestConfiguration;
import com.spring.blog.exception.comment.CommentNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("CommentRepository 단위 테스트")
@ActiveProfiles("test")
@Import(JpaTestConfiguration.class)
@DataJpaTest
class CommentRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private CustomCommentRepository customCommentRepository;

    @BeforeEach
    void setUp() {
        customCommentRepository =
            new CustomCommentRepositoryImpl(new JPAQueryFactory(entityManager));
    }

    @DisplayName("특정 Post에 포함되어 있는 Comment 개수를 조회한다.")
    @Test
    void countCommentByPost_True() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        Comment comment1 = new Comment("1", post, user);
        Comment comment2 = new Comment("2", post, user);

        Post post2 = new Post("hi", "there", user);
        Comment comment3 = new Comment("1", post2, user);
        Comment comment4 = new Comment("2", post2, user);

        List<Comment> comments = Arrays.asList(comment1, comment2, comment3, comment4);
        comments.forEach(Comment::updateAsRoot);

        userRepository.save(user);
        postRepository.saveAll(Arrays.asList(post, post2));
        commentRepository.saveAll(comments);
        flushAndClear();

        // when
        Long counts = customCommentRepository.countCommentsByPost(post);

        // then
        assertThat(counts).isEqualTo(2);
    }

    @DisplayName("adjustHierarchyOrders 메서드는")
    @Nested
    class Describe_adjustHierarchyOrders {

        @DisplayName("같은 그룹 내에서")
        @Nested
        class Context_same_root {

            @DisplayName("주어진 Comment 제외, Comment Right Node 이상인 Left Node와, "
                + "Comment Left Node 이상인 Right Node 들의 값을 +2씩 상승시킨다.")
            @Test
            void it_adds_two_under_condition() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("hi", "there", user);
                userRepository.save(user);
                postRepository.save(post);

                Comment root = new Comment("1", post, user);
                root.updateAsRoot();
                commentRepository.save(root);

                Comment child1 = new Comment("c1", post, user);
                Comment child2 = new Comment("c2", post, user);
                Comment child3 = new Comment("c3", post, user);
                Comment child4 = new Comment("c4", post, user);
                Comment child5 = new Comment("c5", post, user);
                addChild(root.getId(), child1);
                addChild(root.getId(), child2);
                addChild(child1.getId(), child3);
                addChild(child2.getId(), child4);

                Comment target = commentRepository.findById(child2.getId())
                    .orElseThrow(IllegalAccessError::new);
                root = commentRepository.findById(root.getId())
                    .orElseThrow(IllegalAccessError::new);
                Long targetLeft = target.getLeftNode();
                Long targetRight = target.getRightNode();
                Long rootLeft = root.getLeftNode();
                Long rootRight = root.getRightNode();

                commentRepository.findById(child1.getId())
                    .orElseThrow(IllegalAccessError::new)
                    .updateChildCommentHierarchy(child5);
                commentRepository.save(child5);

                // when
                customCommentRepository.adjustHierarchyOrders(child5);
                flushAndClear();

                // then
                Comment updatedTarget = commentRepository.findById(child2.getId())
                    .orElseThrow(IllegalAccessError::new);
                Comment updatedRoot = commentRepository.findById(root.getId())
                    .orElseThrow(IllegalAccessError::new);

                assertThat(updatedTarget.getLeftNode()).isEqualTo(targetLeft + 2);
                assertThat(updatedTarget.getRightNode()).isEqualTo(targetRight + 2);
                assertThat(updatedRoot.getLeftNode()).isEqualTo(rootLeft);
                assertThat(updatedRoot.getRightNode()).isEqualTo(rootRight + 2);
            }
        }
    }

    @DisplayName("findCommentsOrderByHierarchy 메서드는")
    @Nested
    class Describe_findCommentsOrderByHierarchyAndDateDesc {

        @DisplayName("댓글들이 동일한 계층이라면")
        @Nested
        class Context_same_hierarchy {

            @DisplayName("조상(그룹) 댓글을 오름차순(오래된 순)으로 정렬해 조회한다.")
            @Test
            void it_returns_comments_order_by_group_id_desc() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("hi", "there", user);
                userRepository.save(user);
                postRepository.save(post);

                Comment comment1 = new Comment("1", post, user);
                comment1.updateAsRoot();
                Comment comment2 = new Comment("2", post, user);
                comment2.updateAsRoot();
                Comment comment3 = new Comment("3", post, user);
                comment3.updateAsRoot();
                commentRepository.saveAll(Arrays.asList(comment1, comment2, comment3));

                flushAndClear();

                // when
                Pageable pageable = PageRequest.of(0, 5);
                List<Comment> comments = customCommentRepository
                    .findCommentsOrderByHierarchy(pageable, post);

                // then
                assertThat(comments)
                    .extracting("commentContent.content")
                    .containsExactly("1", "2", "3");
            }
        }

        @DisplayName("댓글들이 서로 다른 계층이라면")
        @Nested
        class Context_different_hierarchy {

            /*
            최종적으로 다음과 같은 구조의 댓글이 조회된다.
            Comment1 [1, 10]
              ㄴ child1 [2, 7]
                ㄴ child3 [3, 6]
                ㄴ child4 [4, 5]
              ㄴ child2 [8, 9]
            Comment2 [1, 2]
            Comment3 [1, 4]
              ㄴ child5 [2, 3]
             */
            @DisplayName("조상(그룹) 및 그룹 내 순서 순으로 오름차순 정렬한다.")
            @Test
            void it_returns_comments_order_by_group_id_and_left_asc() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("hi", "there", user);
                userRepository.save(user);
                postRepository.save(post);

                Comment comment1 = new Comment("1", post, user);
                comment1.updateAsRoot();
                Comment comment2 = new Comment("2", post, user);
                comment2.updateAsRoot();
                Comment comment3 = new Comment("3", post, user);
                comment3.updateAsRoot();
                commentRepository.saveAll(Arrays.asList(comment1, comment2, comment3));
                flushAndClear();

                Comment child1 = new Comment("c1", post, user);
                Comment child2 = new Comment("c2", post, user);
                Comment child3 = new Comment("c3", post, user);
                Comment child4 = new Comment("c4", post, user);
                Comment child5 = new Comment("c5", post, user);

                addChild(comment1.getId(), child1);
                addChild(comment1.getId(), child2);
                addChild(child1.getId(), child3);
                addChild(child1.getId(), child4);
                addChild(comment3.getId(), child5);

                // when
                Pageable pageable = PageRequest.of(0, 10);
                List<Comment> comments = customCommentRepository
                    .findCommentsOrderByHierarchy(pageable, post);

                // then
                assertThat(comments)
                    .extracting("commentContent.content")
                    .containsExactly("1", "c1", "c3", "c4", "c2", "2", "3", "c5");
            }
        }
    }

    @DisplayName("deleteChildComments 메서드는")
    @Nested
    class Describe_deleteChildComments {

        @DisplayName("주어진 부모 댓글에 대해")
        @Nested
        class Context_parent_comment {

            @DisplayName("부모 댓글의 하위 댓글들을 전부 삭제한다.")
            @Test
            void it_removes_all_child_comments() {
                // given
                User user = new User("kevin", "image");
                Post post = new Post("hi", "there", user);
                userRepository.save(user);
                postRepository.save(post);

                Comment comment1 = new Comment("1", post, user);
                comment1.updateAsRoot();
                commentRepository.save(comment1);
                flushAndClear();

                Comment child1 = new Comment("c1", post, user);
                Comment child2 = new Comment("c2", post, user);

                addChild(comment1.getId(), child1);
                addChild(comment1.getId(), child2);

                Comment parentComment = commentRepository
                    .findByIdWithRootCommentAndAuthor(comment1.getId())
                    .orElseThrow(CommentNotFoundException::new);

                // when
                customCommentRepository.deleteChildComments(parentComment);

                // then
                assertThat(customCommentRepository.countCommentsByPost(post)).isEqualTo(1);
            }
        }
    }

    private void addChild(Long parentId, Comment child) {
        Comment parent = commentRepository.findByIdWithRootComment(parentId)
            .orElseThrow(IllegalAccessError::new);
        parent.updateChildCommentHierarchy(child);
        commentRepository.save(child);
        customCommentRepository.adjustHierarchyOrders(child);
        flushAndClear();
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
