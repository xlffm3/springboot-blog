package com.spring.blog.post.domain.repository;

import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {

    Optional<Post> findByIdWithAuthor(Long id);

    Optional<Post> findByIdWithAuthorAndImages(Long id);

    List<Post> findPostsOrderByDateDesc(Pageable pageable);
}
