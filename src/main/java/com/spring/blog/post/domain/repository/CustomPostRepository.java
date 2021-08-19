package com.spring.blog.post.domain.repository;

import com.spring.blog.post.domain.Post;
import java.util.Optional;

public interface CustomPostRepository {

    Optional<Post> findWithAuthorById(Long id);
}
