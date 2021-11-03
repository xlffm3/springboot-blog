package com.spring.blog.post.application;

import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.PageMaker;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.dto.request.PostDeleteRequestDto;
import com.spring.blog.post.application.dto.request.PostEditRequestDto;
import com.spring.blog.post.application.dto.request.PostListRequestDto;
import com.spring.blog.post.application.dto.request.PostWriteRequestDto;
import com.spring.blog.post.application.dto.response.PostListResponseDto;
import com.spring.blog.post.application.dto.response.PostResponseDto;
import com.spring.blog.post.domain.FileStorage;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.SearchCondition;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FileStorage fileStorage;

    @Transactional
    public PostResponseDto write(PostWriteRequestDto postWriteRequestDto) {
        User user = userRepository.findActiveUserById(postWriteRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = new Post(postWriteRequestDto.getTitle(), postWriteRequestDto.getContent(), user);
        List<String> imageUrls = fileStorage.store(postWriteRequestDto.getFiles(), user.getName());
        post.addImages(imageUrls);
        return PostResponseDto.from(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto readById(Long id) {
        Post post = postRepository.findByIdWithAuthorAndImages(id)
            .orElseThrow(PostNotFoundException::new);
        post.updateViewCounts();
        return PostResponseDto.from(post);
    }

    public PostListResponseDto readPostList(PostListRequestDto postListRequestDto) {
        Pageable pageable = PageRequest.of(
            Math.toIntExact(postListRequestDto.getPage()),
            Math.toIntExact(postListRequestDto.getSize())
        );
        SearchCondition searchCondition =
            new SearchCondition(postListRequestDto.getSearchType(), postListRequestDto.getKeyword());
        List<Post> posts = postRepository.findPostsOrderByDateDesc(pageable, searchCondition);
        PageMaker pageMaker = generatePageMaker(postListRequestDto, searchCondition);
        return PostListResponseDto.from(posts, pageMaker);
    }

    private PageMaker generatePageMaker(
        PostListRequestDto postListRequestDto,
        SearchCondition searchCondition
    ) {
        return new PageMaker(
            Math.toIntExact(postListRequestDto.getPage()),
            Math.toIntExact(postListRequestDto.getSize()),
            Math.toIntExact(postListRequestDto.getPageBlockCounts()),
            Math.toIntExact(postRepository.countActivePosts(searchCondition))
        );
    }

    @Transactional
    public PostResponseDto edit(PostEditRequestDto postEditRequestDto) {
        Post post = postRepository.findByIdWithAuthor(postEditRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        userRepository.findActiveUserById(postEditRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        post.edit(postEditRequestDto.getTitle(), postEditRequestDto.getContent());
        return PostResponseDto.from(post);
    }

    @Transactional
    public void deletePost(PostDeleteRequestDto postDeleteRequestDto) {
        Post post = postRepository.findByIdWithAuthor(postDeleteRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        User user = userRepository.findActiveUserById(postDeleteRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        post.delete(user);
        commentRepository.deleteAllByPost(post);
    }
}
