package com.spring.blog.comment.application;

import com.spring.blog.comment.application.dto.request.CommentDeleteRequestDto;
import com.spring.blog.comment.application.dto.request.CommentEditRequestDto;
import com.spring.blog.comment.application.dto.request.CommentListRequestDto;
import com.spring.blog.comment.application.dto.request.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.request.CommentWriteRequestDto;
import com.spring.blog.comment.application.dto.response.CommentListResponseDto;
import com.spring.blog.comment.application.dto.response.CommentResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto writeComment(CommentWriteRequestDto commentWriteRequestDto) {
        User user = userRepository.findActiveUserById(commentWriteRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findActivePostById(commentWriteRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(commentWriteRequestDto.getContent(), post, user);
        comment.updateAsRoot();
        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDto replyComment(CommentReplyRequestDto commentReplyRequestDto) {
        User user = userRepository.findActiveUserById(commentReplyRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findActivePostById(commentReplyRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Comment parentComment =
            commentRepository.findByIdWithRootComment(commentReplyRequestDto.getCommentId())
                .orElseThrow(CommentNotFoundException::new);
        Comment comment = new Comment(commentReplyRequestDto.getContent(), post, user);
        parentComment.updateChildCommentHierarchy(comment);
        commentRepository.save(comment);
        commentRepository.adjustHierarchyOrders(comment);
        return CommentResponseDto.from(comment);
    }

    public CommentListResponseDto readCommentList(CommentListRequestDto commentListRequestDto) {
        Post post = postRepository.findActivePostById(commentListRequestDto.getPostId())
            .orElseThrow(PostNotFoundException::new);
        Pageable pageable = PageRequest.of(
            Math.toIntExact(commentListRequestDto.getPage()),
            Math.toIntExact(commentListRequestDto.getSize())
        );
        List<Comment> comments = commentRepository.findCommentsOrderByHierarchy(pageable, post);
        PageMaker pageMaker = generatePageMaker(commentListRequestDto, post);
        return CommentListResponseDto.from(comments, pageMaker);
    }

    private PageMaker generatePageMaker(CommentListRequestDto commentListRequestDto, Post post) {
        return new PageMaker(
            Math.toIntExact(commentListRequestDto.getPage()),
            Math.toIntExact(commentListRequestDto.getSize()),
            Math.toIntExact(commentListRequestDto.getPageBlockCounts()),
            Math.toIntExact(commentRepository.countCommentsByPost(post))
        );
    }

    @Transactional
    public CommentResponseDto editComment(CommentEditRequestDto commentEditRequestDto) {
        Comment comment = commentRepository.findByIdWithAuthor(commentEditRequestDto.getCommentId())
            .orElseThrow(CommentNotFoundException::new);
        User user = userRepository.findActiveUserById(commentEditRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        comment.editContent(commentEditRequestDto.getContent(), user);
        return CommentResponseDto.from(comment);
    }

    @Transactional
    public void deleteComment(CommentDeleteRequestDto commentDeleteRequestDto) {
        Comment comment = commentRepository
            .findByIdWithRootCommentAndAuthor(commentDeleteRequestDto.getCommentId())
            .orElseThrow(CommentNotFoundException::new);
        User user = userRepository.findActiveUserById(commentDeleteRequestDto.getUserId())
            .orElseThrow(UserNotFoundException::new);
        comment.delete(user);
        commentRepository.deleteChildComments(comment);
    }
}
