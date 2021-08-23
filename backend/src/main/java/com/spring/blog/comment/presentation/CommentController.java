package com.spring.blog.comment.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.application.dto.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.CommentResponseDto;
import com.spring.blog.comment.application.dto.CommentWriteRequestDto;
import com.spring.blog.comment.presentation.dto.CommentListResponse;
import com.spring.blog.comment.presentation.dto.CommentResponse;
import com.spring.blog.comment.presentation.dto.CommentWriteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> write(
        @PathVariable Long postId,
        @Authenticated AppUser appUser,
        @RequestBody CommentWriteRequest commentWriteRequest
    ) {
        CommentWriteRequestDto commentWriteRequestDto =
            new CommentWriteRequestDto(postId, appUser.getId(), commentWriteRequest.getContent());
        CommentResponseDto commentResponseDto = commentService.writeComment(commentWriteRequestDto);
        CommentResponse commentResponse = CommentResponse.from(commentResponseDto);
        return ResponseEntity.ok(commentResponse);
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    public ResponseEntity<CommentResponse> reply(
        @PathVariable Long postId,
        @PathVariable Long commentId,
        @Authenticated AppUser appUser,
        @RequestBody CommentWriteRequest commentWriteRequest
    ) {
        CommentReplyRequestDto commentReplyRequestDto =
            new CommentReplyRequestDto(
                postId,
                appUser.getId(),
                commentId,
                commentWriteRequest.getContent()
            );
        CommentResponseDto commentResponseDto = commentService.replyComment(commentReplyRequestDto);
        CommentResponse commentResponse = CommentResponse.from(commentResponseDto);
        return ResponseEntity.ok(commentResponse);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponse> readList(
        @PathVariable Long postId,
        @RequestParam Long page,
        @RequestParam Long size,
        @RequestParam Long pageBlockCounts
    ) {
        CommentListRequestDto commentListRequestDto =
            new CommentListRequestDto(postId, page, size, pageBlockCounts);
        CommentListResponseDto commentListResponseDto =
            commentService.readCommentList(commentListRequestDto);
        CommentListResponse commentListResponse =
            CommentListResponse.from(commentListResponseDto);
        return ResponseEntity.ok(commentListResponse);
    }
}
