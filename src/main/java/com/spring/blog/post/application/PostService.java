package com.spring.blog.post.application;

import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.dto.PostRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponseDto write(PostRequestDto postRequestDto) {
        User user = userRepository.findById(postRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        PostContent postContent =
            new PostContent(postRequestDto.getTitle(), postRequestDto.getContent());
        Post post = postRepository.save(new Post(postContent, user));
        return PostResponseDto.from(post);
    }

    @Transactional
    public PostResponseDto readById(Long id) {
        Post post = postRepository.findWithAuthorById(id)
            .orElseThrow(PostNotFoundException::new);
        post.updateViewCounts();
        return PostResponseDto.from(post);
    }
}
