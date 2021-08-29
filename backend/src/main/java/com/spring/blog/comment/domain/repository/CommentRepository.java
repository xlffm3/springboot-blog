package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    @Override
    @Query("select c from Comment c where c.id = :id and c.isDeleted = false")
    Optional<Comment> findById(@Param("id") Long aLong);
}
