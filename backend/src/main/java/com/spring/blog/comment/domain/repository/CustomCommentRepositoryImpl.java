package com.spring.blog.comment.domain.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.QComment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private static final QComment QCOMMENT = QComment.comment;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findCommentsOrderByHierarchy(Pageable pageable, Post post) {
        return selectCommentInnerFetchJoinUser()
            .where(isActiveCommentOf(post))
            .orderBy(QCOMMENT.hierarchy.rootComment.id.asc(),
                QCOMMENT.hierarchy.leftNode.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Optional<Comment> findByIdWithAuthor(Long id) {
        Comment comment = selectCommentInnerFetchJoinUser()
            .where(isActiveComment(id))
            .fetchOne();
        return Optional.ofNullable(comment);
    }

    @Override
    public Optional<Comment> findByIdWithRootComment(Long id) {
        Comment comment = jpaQueryFactory.selectFrom(QCOMMENT)
            .innerJoin(QCOMMENT.hierarchy.rootComment)
            .fetchJoin()
            .where(isActiveComment(id))
            .fetchOne();
        return Optional.ofNullable(comment);
    }

    @Override
    public Optional<Comment> findByIdWithRootCommentAndAuthor(Long id) {
        Comment comment = selectCommentInnerFetchJoinUser()
            .innerJoin(QCOMMENT.hierarchy.rootComment)
            .fetchJoin()
            .where(isActiveComment(id))
            .fetchOne();
        return Optional.ofNullable(comment);
    }

    @Override
    public void adjustHierarchyOrders(Comment newComment) {
        jpaQueryFactory.update(QCOMMENT)
            .set(QCOMMENT.hierarchy.leftNode, QCOMMENT.hierarchy.leftNode.add(2))
            .where(QCOMMENT.hierarchy.leftNode.goe(newComment.getRightNode())
                .and(isActiveCommentInSameGroupExceptNewComment(newComment)))
            .execute();

        jpaQueryFactory.update(QCOMMENT)
            .set(QCOMMENT.hierarchy.rightNode, QCOMMENT.hierarchy.rightNode.add(2))
            .where(QCOMMENT.hierarchy.rightNode.goe(newComment.getLeftNode())
                .and(isActiveCommentInSameGroupExceptNewComment(newComment)))
            .execute();
    }

    @Override
    public Long countCommentsByPost(Post post) {
        return jpaQueryFactory.selectFrom(QCOMMENT)
            .where(isActiveCommentOf(post))
            .fetchCount();
    }

    @Override
    public void deleteChildComments(Comment parentComment) {
        deleteComment()
            .where(QCOMMENT.hierarchy.leftNode.gt(parentComment.getLeftNode())
                .and(QCOMMENT.hierarchy.rightNode.lt(parentComment.getRightNode())
                    .and(QCOMMENT.hierarchy.rootComment.eq(parentComment.getRootComment())
                        .and(isActiveComment()))))
            .execute();
    }

    @Override
    public void deleteAllByPost(Post post) {
        deleteComment()
            .where(isActiveCommentOf(post))
            .execute();
    }

    private JPAQuery<Comment> selectCommentInnerFetchJoinUser() {
        return jpaQueryFactory.selectFrom(QCOMMENT)
            .innerJoin(QCOMMENT.user)
            .fetchJoin();
    }

    private JPAUpdateClause deleteComment() {
        return jpaQueryFactory.update(QCOMMENT)
            .set(QCOMMENT.isDeleted, true);
    }

    private Predicate isActiveCommentInSameGroupExceptNewComment(Comment newComment) {
        return QCOMMENT.hierarchy.rootComment.eq(newComment.getRootComment())
            .and(QCOMMENT.ne(newComment))
            .and(isActiveComment());
    }

    private BooleanExpression isActiveCommentOf(Post post) {
        return QCOMMENT.post.eq(post).and(isActiveComment());
    }

    private BooleanExpression isActiveComment(Long id) {
        return QCOMMENT.id.eq(id).and(isActiveComment());
    }

    private BooleanExpression isActiveComment() {
        return QCOMMENT.isDeleted.eq(false);
    }
}
