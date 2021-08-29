package com.spring.blog.comment.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.request.CommentDeleteRequestDto;
import com.spring.blog.comment.application.dto.request.CommentEditRequestDto;
import com.spring.blog.comment.application.dto.request.CommentListRequestDto;
import com.spring.blog.comment.application.dto.response.CommentListResponseDto;
import com.spring.blog.comment.application.dto.request.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.response.CommentResponseDto;
import com.spring.blog.comment.application.dto.request.CommentWriteRequestDto;
import com.spring.blog.comment.presentation.dto.response.CommentListResponse;
import com.spring.blog.comment.presentation.dto.response.CommentResponse;
import com.spring.blog.comment.presentation.dto.request.CommentWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> write(
        @PathVariable Long postId,
        @Authenticated AppUser appUser,
        @RequestBody CommentWriteRequest commentWriteRequest
    ) {
        CommentWriteRequestDto commentWriteRequestDto = CommentWriteRequestDto.builder()
            .postId(postId)
            .userId(appUser.getId())
            .content(commentWriteRequest.getContent())
            .build();
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
        CommentReplyRequestDto commentReplyRequestDto = CommentReplyRequestDto.builder()
            .postId(postId)
            .userId(appUser.getId())
            .commentId(commentId)
            .content(commentWriteRequest.getContent())
            .build();
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
        CommentListRequestDto commentListRequestDto = CommentListRequestDto.builder()
            .postId(postId)
            .page(page)
            .size(size)
            .pageBlockCounts(pageBlockCounts)
            .build();
        CommentListResponseDto commentListResponseDto =
            commentService.readCommentList(commentListRequestDto);
        CommentListResponse commentListResponse =
            CommentListResponse.from(commentListResponseDto);
        return ResponseEntity.ok(commentListResponse);
    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<CommentResponse> edit(
        @PathVariable Long commentId,
        @Authenticated AppUser appUser,
        @RequestBody CommentWriteRequest commentWriteRequest
    ) {
        CommentEditRequestDto commentEditRequestDto = CommentEditRequestDto.builder()
            .commentId(commentId)
            .userId(appUser.getId())
            .content(commentWriteRequest.getContent())
            .build();
        CommentResponseDto commentResponseDto = commentService.editComment(commentEditRequestDto);
        CommentResponse commentResponse = CommentResponse.from(commentResponseDto);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("comments/{commentId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long commentId,
        @Authenticated AppUser appUser
    ) {
        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .commentId(commentId)
            .userId(appUser.getId())
            .build();
        commentService.deleteComment(commentDeleteRequestDto);
        return ResponseEntity.noContent()
            .build();
    }
}
