package com.spring.blog.post.domain.repository;

import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.SearchCondition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {

    Optional<Post> findActivePostById(Long id);

    Optional<Post> findByIdWithAuthor(Long id);

    Optional<Post> findByIdWithAuthorAndImages(Long id);

    List<Post> findPostsOrderByDateDesc(Pageable pageable, SearchCondition searchCondition);

    Long countActivePosts(SearchCondition searchCondition);
}
