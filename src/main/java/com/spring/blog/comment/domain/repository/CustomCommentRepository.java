package com.spring.blog.comment.domain.repository;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.post.domain.Post;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomCommentRepository {

    List<Comment> findCommentsOrderByHierarchyAndDateDesc(Pageable pageable, Post post);
}
