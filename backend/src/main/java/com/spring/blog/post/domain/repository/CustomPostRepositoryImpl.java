package com.spring.blog.post.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.QPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Post> findWithAuthorById(Long id) {
        Post post = selectPostInnerFetchJoinUser()
            .where(QPost.post.id.eq(id))
            .fetchFirst();
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findPostsOrderByDateDesc(Pageable pageable) {
        return selectPostInnerFetchJoinUser()
            .orderBy(QPost.post.baseDate.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private JPAQuery<Post> selectPostInnerFetchJoinUser() {
        return jpaQueryFactory.selectFrom(QPost.post)
            .innerJoin(QPost.post.user)
            .fetchJoin();
    }
}
