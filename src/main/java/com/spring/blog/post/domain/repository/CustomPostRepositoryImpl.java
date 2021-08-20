package com.spring.blog.post.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.QPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

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
            .where(QPost.post.id.eq(id))
            .fetchFirst();
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findLatestPostsWithAuthorPagination(Pageable pageable) {
        return jpaQueryFactory.selectFrom(QPost.post)
            .innerJoin(QPost.post.user)
            .fetchJoin()
            .orderBy(QPost.post.baseDate.createdDate.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
    }
}
