package com.spring.blog.comment.application;

import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.PageMaker;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentListResponseDto readCommentList(CommentListRequestDto commentListRequestDto) {
        Post post = postRepository.findById(commentListRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Pageable pageable = PageRequest.of(
            Math.toIntExact(commentListRequestDto.getPage()),
            Math.toIntExact(commentListRequestDto.getSize())
        );
        List<Comment> comments =
            commentRepository.findCommentsOrderByHierarchyAndDateDesc(pageable, post);
        PageMaker pageMaker = generatePageMaker(commentListRequestDto, post);
        return CommentListResponseDto.from(comments, pageMaker);
    }

    private PageMaker generatePageMaker(CommentListRequestDto commentListRequestDto, Post post) {
        return new PageMaker(
            Math.toIntExact(commentListRequestDto.getPage()),
            Math.toIntExact(commentListRequestDto.getSize()),
            Math.toIntExact(commentListRequestDto.getPageBlockCounts()),
            Math.toIntExact(commentRepository.countCommentByPost(post))
        );
    }
}
