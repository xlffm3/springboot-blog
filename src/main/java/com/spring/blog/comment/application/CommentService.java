package com.spring.blog.comment.application;

import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.application.dto.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.CommentResponseDto;
import com.spring.blog.comment.application.dto.CommentWriteRequestDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.PageMaker;
import com.spring.blog.exception.comment.CommentNotFoundException;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(
        CommentRepository commentRepository,
        PostRepository postRepository,
        UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponseDto writeComment(CommentWriteRequestDto commentWriteRequestDto) {
        User user = userRepository.findById(commentWriteRequestDto.getAuthorId())
            .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(commentWriteRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(commentWriteRequestDto.getContent(), post, user);
        comment.updateAsRoot();
        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDto replyComment(CommentReplyRequestDto commentReplyRequestDto) {
        User user = userRepository.findById(commentReplyRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(commentReplyRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Comment parentComment = commentRepository.findById(commentReplyRequestDto.getCommentId())
            .orElseThrow(CommentNotFoundException::new);
        Comment comment = new Comment(commentReplyRequestDto.getContent(), post, user);
        parentComment.addChildComment(comment);
        return CommentResponseDto.from(commentRepository.save(comment));
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
