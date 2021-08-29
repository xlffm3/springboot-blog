package com.spring.blog.post.domain.repository;

import com.spring.blog.post.domain.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    @Override
    @Query("select p from Post p where p.id = :id and p.isDeleted = false")
    Optional<Post> findById(@Param("id") Long aLong);
}
