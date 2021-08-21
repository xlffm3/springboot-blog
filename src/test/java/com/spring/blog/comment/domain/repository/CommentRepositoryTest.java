package com.spring.blog.comment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.configuration.JpaTestConfiguration;
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

@DisplayName("CommentRepository 단위 테스트")
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

    @DisplayName("Comment 자식을 추가만 하더라도 자식이 함께 영속화된다.")
    @Test
    void save_ChildTogether_Persistence() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        Comment comment1 = new Comment("1", post, user);
        Comment comment2 = new Comment("2", post, user);
        comment1.updateAsRoot();
        comment1.addChildComment(comment2);

        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment1);
        flushAndClear();

        // when, then
        assertThat(commentRepository.findById(comment2.getId())).isNotEmpty();
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
        userRepository.save(user);
        postRepository.saveAll(Arrays.asList(post, post2));
        commentRepository.saveAll(Arrays.asList(comment1, comment2, comment3, comment4));
        flushAndClear();

        // when
        Long counts = commentRepository.countCommentByPost(post);

        // then
        assertThat(counts).isEqualTo(2);
    }

    @DisplayName("findCommentsOrderByHierarchyAndDateDesc 메서드는")
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
                    .findCommentsOrderByHierarchyAndDateDesc(pageable, post);

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
            Comment1
              ㄴ child1
                ㄴ child3
                ㄴ child4
              ㄴ child2
            Comment2
            Comment3
              ㄴ child5
             */
            @DisplayName("조상(그룹) 댓글, 계층, 날짜 순으로 오름차순 정렬한다.")
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
                Comment child1 = new Comment("c1", post, user);
                Comment child2 = new Comment("c2", post, user);
                Comment child3 = new Comment("c3", post, user);
                Comment child4 = new Comment("c4", post, user);
                Comment child5 = new Comment("c5", post, user);
                comment1.addChildComment(child1);
                comment1.addChildComment(child2);
                child1.addChildComment(child3);
                child1.addChildComment(child4);
                comment3.addChildComment(child5);

                commentRepository.saveAll(
                    Arrays.asList(
                        comment1,
                        comment2,
                        comment3,
                        child1,
                        child2,
                        child3,
                        child4,
                        child5
                    )
                );
                flushAndClear();

                // when
                Pageable pageable = PageRequest.of(0, 10);
                List<Comment> comments = customCommentRepository
                    .findCommentsOrderByHierarchyAndDateDesc(pageable, post);

                // then
                assertThat(comments)
                    .extracting("commentContent.content")
                    .containsExactly("1", "c1", "c3", "c4", "c2", "2", "3", "c5");
            }
        }
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
