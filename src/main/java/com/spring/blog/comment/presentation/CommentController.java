package com.spring.blog.comment.presentation;

import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.presentation.dto.CommentListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/comments/{postId}")
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
