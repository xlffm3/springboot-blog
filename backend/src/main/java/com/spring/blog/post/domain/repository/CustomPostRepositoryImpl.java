package com.spring.blog.post.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.QPost;
import com.spring.blog.post.domain.SearchCondition;
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
            .leftJoin(QPOST.images.images)
            .fetchJoin()
            .where(isActivePost(id))
            .distinct()
            .fetchOne();
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findPostsOrderByDateDesc(
        Pageable pageable,
        SearchCondition searchCondition
    ) {
        return selectPostInnerFetchJoinUser()
            .where(isActivePostUnderSearchCondition(searchCondition))
            .orderBy(QPOST.baseDate.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countActivePosts(SearchCondition searchCondition) {
        return jpaQueryFactory.selectFrom(QPOST)
            .where(isActivePostUnderSearchCondition(searchCondition))
            .fetchCount();
    }

    private Predicate isActivePostUnderSearchCondition(SearchCondition searchCondition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(isActivePost());
        if (!searchCondition.isCustomSearchCondition()) {
            return booleanBuilder;
        }
        String keyword = searchCondition.getKeyword();
        if (searchCondition.isForTitle()) {
            booleanBuilder.and(QPOST.postContent.title.containsIgnoreCase(keyword));
        }
        if (searchCondition.isForName()) {
            booleanBuilder.and(QPOST.user.name.containsIgnoreCase(keyword));
        }
        if (searchCondition.isForContent()) {
            booleanBuilder.and(QPOST.postContent.content.containsIgnoreCase(keyword));
        }
        return booleanBuilder;
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
