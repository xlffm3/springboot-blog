package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
}
