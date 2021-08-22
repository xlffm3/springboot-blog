package com.spring.blog.comment.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.QComment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Optional;
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
                comment.hierarchy.leftNode.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Optional<Comment> findByIdIdWithRootComment(Long commentId) {
        QComment comment = QComment.comment;
        Comment findComment = jpaQueryFactory.selectFrom(comment)
            .innerJoin(comment.hierarchy.rootComment)
            .fetchJoin()
            .where(comment.id.eq(commentId))
            .fetchOne();
        return Optional.ofNullable(findComment);
    }

    @Override
    public void adjustHierarchyOrders(Comment newComment) {
        QComment comment = QComment.comment;
        jpaQueryFactory.update(comment)
            .set(comment.hierarchy.leftNode, comment.hierarchy.leftNode.add(2))
            .where(comment.hierarchy.leftNode.goe(newComment.getRightNode())
                .and(comment.hierarchy.rootComment.eq(newComment.getRootComment()))
                .and(comment.ne(newComment)))
            .execute();

        jpaQueryFactory.update(comment)
            .set(comment.hierarchy.rightNode, comment.hierarchy.rightNode.add(2))
            .where(comment.hierarchy.rightNode.goe(newComment.getLeftNode())
                .and(comment.hierarchy.rootComment.eq(newComment.getRootComment()))
                .and(comment.ne(newComment)))
            .execute();
    }
}
