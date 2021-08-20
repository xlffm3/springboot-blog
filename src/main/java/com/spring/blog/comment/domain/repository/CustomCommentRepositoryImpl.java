package com.spring.blog.comment.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.QComment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomCommentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Comment> findCommentsOrderByHierarchyAndDateDesc(Pageable pageable, Post post) {
        QComment comment = QComment.comment;
        return jpaQueryFactory.selectFrom(comment)
            .innerJoin(comment.user)
            .fetchJoin()
            .where(comment.post.eq(post))
            .orderBy(
                comment.hierarchy.rootComment.id.asc(),
                comment.baseDate.createdDate.asc(),
                comment.hierarchy.depth.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
