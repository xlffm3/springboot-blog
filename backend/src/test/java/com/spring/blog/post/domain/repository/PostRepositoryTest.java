package com.spring.blog.post.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.configuration.JpaTestConfiguration;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.SearchCondition;
import com.spring.blog.post.domain.image.Image;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Arrays;
import java.util.Collections;
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

@DisplayName("PostRepository 단위 테스트")
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

    @DisplayName("findByIdWithAuthor 메서드는")
    @Nested
    class Describe_findByIdWithAuthor {

        @DisplayName("ID 해당 Post가 존재하지 않는 경우 경우")
        @Nested
        class Context_invalid_id {

            @DisplayName("비어있는 Optional을 반환한다.")
            @Test
            void it_returns_empty_Optional() {
                // given
                User savedUser = userRepository.save(new User("kevin", "image"));
                Post post = new Post("title", "content", savedUser);
                postRepository.save(post);
                flushAndClear();

                // when, then
                assertThat(customPostRepository.findByIdWithAuthor(3123L)).isEmpty();
            }
        }

        @DisplayName("ID 해당 Post가 존재하는 경우")
        @Nested
        class Context_valid_id {

            @DisplayName("유저를 페치조인하여 정상적으로 Post를 조회한다.")
            @Test
            void it_returns_post() {
                // given
                User savedUser = userRepository.save(new User("kevin", "image"));
                Post post = new Post("title", "content", savedUser);
                postRepository.save(post);
                flushAndClear();

                // when
                Post findPost = customPostRepository.findByIdWithAuthor(post.getId())
                    .orElseThrow(PostNotFoundException::new);

                // then
                assertThat(findPost.getAuthorName()).isEqualTo("kevin");
            }
        }
    }

    @DisplayName("findPostsOrderByDateDesc 메서드는")
    @Nested
    class Describe_findPostsOrderByDateDesc {

        @DisplayName("Pageable이 주어졌지만 검색 조건이 없을 때")
        @Nested
        class Context_given_pageable {

            @DisplayName("유저를 페치조인한 Post를 최신순으로 정렬하여 페이징한다.")
            @Test
            void it_returns_posts_order_by_date_desc_with_user_fetch_join() {
                // given
                List<User> users = Arrays.asList(
                    new User("kevin", "hi"),
                    new User("kevin2", "hi2"),
                    new User("kevin3", "hi3")
                );
                userRepository.saveAll(users);
                List<Post> posts = Arrays.asList(
                    new Post("a", "b", users.get(0)),
                    new Post("a", "b", users.get(1)),
                    new Post("a", "b", users.get(2))
                );
                postRepository.saveAll(posts);
                flushAndClear();

                // when
                Pageable pageable = PageRequest.of(0, 3);
                SearchCondition searchCondition = new SearchCondition(null, null);
                List<Post> findPosts = customPostRepository
                    .findPostsOrderByDateDesc(pageable, searchCondition);
                Collections.reverse(posts);

                // then
                assertThat(findPosts)
                    .usingRecursiveComparison()
                    .isEqualTo(posts);
            }
        }

        @DisplayName("검색 조건이 함께 주어졌을 때")
        @Nested
        class Context_given_search_condition {

            @DisplayName("검색 조건에 따라 게시물을 최신순 조회한다.")
            @Test
            void it_returns_posts_under_search_condition() {
                // given
                List<User> users = Arrays.asList(
                    new User("jipark", "hi"),
                    new User("kevin2", "hi2")
                );
                userRepository.saveAll(users);
                List<Post> posts = Arrays.asList(
                    new Post("a", "b", users.get(0)),
                    new Post("a", "b", users.get(0)),
                    new Post("a", "b", users.get(1))
                );
                postRepository.saveAll(posts);
                flushAndClear();

                // when
                Pageable pageable = PageRequest.of(0, 3);
                SearchCondition searchCondition = new SearchCondition("name", "jipark");
                List<Post> findPosts = customPostRepository
                    .findPostsOrderByDateDesc(pageable, searchCondition);

                // then
                assertThat(findPosts)
                    .usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(posts.get(1), posts.get(0)));
            }
        }
    }

    @DisplayName("save 메서드는")
    @Nested
    class Describe_save {

        @DisplayName("Post를 영속화시킬 때")
        @Nested
        class Context_persist_post {

            @DisplayName("Image도 함께 영속화시킨다.")
            @Test
            void it_saves_images_together() {
                // given
                User savedUser = userRepository.save(new User("kevin", "image"));
                Post post = new Post("title", "content", savedUser);
                List<String> images = Arrays.asList("abc", "def");

                // when
                post.addImages(images);
                postRepository.save(post);
                flushAndClear();

                Post findPost = postRepository.findById(post.getId())
                    .orElseThrow(PostNotFoundException::new);
                List<Image> findImages = entityManager
                    .createQuery("select i from Image i where i.post = :post", Image.class)
                    .setParameter("post", findPost)
                    .getResultList();

                // then
                assertThat(findPost.getImageUrls()).containsExactly("abc", "def");
                assertThat(findImages).hasSize(2);
            }
        }
    }

    @DisplayName("countActivePosts 메서드는")
    @Nested
    class Describe_countActivePosts {

        @DisplayName("검색 조건이 주어지면")
        @Nested
        class Context_given_search_condition {

            @DisplayName("검색 조건에 맞는 게시물의 개수를 검색한다.")
            @Test
            void it_returns_post_counts_by_condition() {
                // given
                List<User> users = Arrays.asList(
                    new User("kevin", "hi"),
                    new User("kevin2", "hi2")
                );
                userRepository.saveAll(users);
                List<Post> posts = Arrays.asList(
                    new Post("a", "bbb", users.get(0)),
                    new Post("a", "bbbbb", users.get(1)),
                    new Post("a", "c", users.get(1))
                );
                postRepository.saveAll(posts);
                flushAndClear();

                // when
                Pageable pageable = PageRequest.of(0, 3);
                SearchCondition searchCondition = new SearchCondition("content", "b");
                List<Post> findPosts = customPostRepository
                    .findPostsOrderByDateDesc(pageable, searchCondition);

                // then
                assertThat(findPosts)
                    .usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(posts.get(1), posts.get(0)));
            }
        }
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
