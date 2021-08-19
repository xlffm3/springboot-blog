package com.spring.blog.post.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.configuration.JpaTestConfiguration;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.infrasructure.CustomPostRepositoryImpl;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
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

@Import(JpaTestConfiguration.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private CustomPostRepository customPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        customPostRepository = new CustomPostRepositoryImpl(new JPAQueryFactory(entityManager));
    }

    @DisplayName("findWithAuthorById 메서드는")
    @Nested
    class Describe_findWithAuthorById {

        @DisplayName("ID 해당 Post가 존재하지 않는 경우 경우")
        @Nested
        class Context_invalid_id {

            @DisplayName("비어있는 Optional을 반환한다.")
            @Test
            void it_returns_empty_Optional() {
                // given, when, then
                assertThat(customPostRepository.findWithAuthorById(3123L)).isEmpty();
            }
        }

        @DisplayName("ID 해당 Post가 존재하는 경우")
        @Nested
        class Context_valid_id {

            @DisplayName("정상적으로 Post를 조회한다.")
            @Test
            void it_returns_post() {
                // given
                User savedUser = userRepository.save(new User("kevin", "image"));
                Post post = new Post(new PostContent("title", "content"), savedUser);
                postRepository.save(post);
                flushAndClear();

                // when
                Post findPost = customPostRepository.findWithAuthorById(post.getId())
                    .orElseThrow(PostNotFoundException::new);

                // then
                assertThat(findPost.getAuthorName()).isEqualTo("kevin");
            }
        }
    }

    void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}