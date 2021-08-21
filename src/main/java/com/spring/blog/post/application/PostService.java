package com.spring.blog.post.application;

import com.spring.blog.common.PageMaker;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.dto.PostListRequestDto;
import com.spring.blog.post.application.dto.PostListResponseDto;
import com.spring.blog.post.application.dto.PostWriteRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public PostResponseDto write(PostWriteRequestDto postWriteRequestDto) {
        User user = userRepository.findById(postWriteRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = new Post(postWriteRequestDto.getTitle(), postWriteRequestDto.getContent(), user);
        return PostResponseDto.from(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto readById(Long id) {
        Post post = postRepository.findWithAuthorById(id)
            .orElseThrow(PostNotFoundException::new);
        post.updateViewCounts();
        return PostResponseDto.from(post);
    }

    public PostListResponseDto readPostList(PostListRequestDto postListRequestDto) {
        Pageable pageable = PageRequest.of(
            Math.toIntExact(postListRequestDto.getPage()),
            Math.toIntExact(postListRequestDto.getSize())
        );
        List<Post> posts = postRepository.findPostsOrderByDateDesc(pageable);
        PageMaker pageMaker = generatePageMaker(postListRequestDto);
        return PostListResponseDto.from(posts, pageMaker);
    }

    private PageMaker generatePageMaker(PostListRequestDto postListRequestDto) {
        return new PageMaker(
            Math.toIntExact(postListRequestDto.getPage()),
            Math.toIntExact(postListRequestDto.getSize()),
            Math.toIntExact(postListRequestDto.getPageBlockCounts()),
            Math.toIntExact(postRepository.count())
        );
    }
}
