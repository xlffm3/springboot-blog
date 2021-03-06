package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomCommentRepository {

    List<Comment> findCommentsOrderByHierarchy(Pageable pageable, Post post);

    Optional<Comment> findByIdWithAuthor(Long id);

    Optional<Comment> findByIdWithRootComment(Long id);

    Optional<Comment> findByIdWithRootCommentAndAuthor(Long id);

    void adjustHierarchyOrders(Comment newComment);

    void deleteChildComments(Comment parentComment);

    Long countCommentsByPost(Post post);

    void deleteAllByPost(Post post);
}
