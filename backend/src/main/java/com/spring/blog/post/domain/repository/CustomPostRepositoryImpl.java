package com.spring.blog.post.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.QPost;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private static final QPost QPOST = QPost.post;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Post> findActivePostById(Long id) {
        Post post = jpaQueryFactory.selectFrom(QPOST)
            .where(isActivePost(id))
            .fetchFirst();
        return Optional.ofNullable(post);
    }

    @Override
    public Optional<Post> findByIdWithAuthor(Long id) {
        Post post = selectPostInnerFetchJoinUser()
            .where(isActivePost(id))
            .fetchFirst();
        return Optional.ofNullable(post);
    }

    @Override
    public Optional<Post> findByIdWithAuthorAndImages(Long id) {
        Post post = selectPostInnerFetchJoinUser()
            .distinct()
            .leftJoin(QPOST.images.images)
            .fetchJoin()
            .where(isActivePost(id))
            .fetchFirst();
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findPostsOrderByDateDesc(Pageable pageable) {
        return selectPostInnerFetchJoinUser()
            .where(isActivePost())
            .orderBy(QPOST.baseDate.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private JPAQuery<Post> selectPostInnerFetchJoinUser() {
        return jpaQueryFactory.selectFrom(QPOST)
            .innerJoin(QPOST.user)
            .fetchJoin();
    }

    private BooleanExpression isActivePost(Long id) {
        return QPOST.id.eq(id).and(isActivePost());
    }

    private BooleanExpression isActivePost() {
        return QPOST.isDeleted.eq(false);
    }
}
