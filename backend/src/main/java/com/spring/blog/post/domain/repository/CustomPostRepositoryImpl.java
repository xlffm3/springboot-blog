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
        QPost post = QPost.post;
        Post findPost = selectPostInnerFetchJoinUser()
            .where(post.id.eq(id)
                .and(post.isDeleted.eq(false)))
            .fetchFirst();
        return Optional.ofNullable(findPost);
    }

    @Override
    public List<Post> findPostsOrderByDateDesc(Pageable pageable) {
        QPost post = QPost.post;
        return selectPostInnerFetchJoinUser()
            .where(post.isDeleted.eq(false))
            .orderBy(post.baseDate.createdDate.desc())
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
