package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomCommentRepository {

    List<Comment> findCommentsOrderByHierarchyAndDateDesc(Pageable pageable, Post post);

    Optional<Comment> findByIdWithAuthor(Long commentId);

    Optional<Comment> findByIdWithRootComment(Long commentId);

    Optional<Comment> findByIdWithRootCommentAndAuthor(Long commentId);

    void adjustHierarchyOrders(Comment newComment);

    void deleteChildComments(Comment parentComment);

    Long countCommentByPost(Post post);
}
