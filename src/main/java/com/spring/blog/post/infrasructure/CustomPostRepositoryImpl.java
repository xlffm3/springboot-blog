package com.spring.blog.post.infrasructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.QPost;
import com.spring.blog.post.domain.repository.CustomPostRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Post> findWithAuthorById(Long id) {
        Post post = jpaQueryFactory.selectFrom(QPost.post)
            .innerJoin(QPost.post.user)
            .fetchJoin()
            .fetchFirst();
        return Optional.ofNullable(post);
    }
}
