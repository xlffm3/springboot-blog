package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    Long countCommentByPost(Post post);
}
