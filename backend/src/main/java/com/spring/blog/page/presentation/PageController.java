package com.spring.blog.page.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/page")
@Controller
public class PageController {

    @GetMapping("/auth")
    public String moveToAuthPage() {
        return "auth.html";
    }

    @GetMapping("/user/register")
    public String moveToRegisterPage() {
        return "user-register-oauth.html";
    }

    @GetMapping("/post/{postId}")
    public String moveToPostPage(@PathVariable Long postId) {
        return "post.html";
    }

    @GetMapping("/post/edit")
    public String moveToPostEditPage() {
        return "post-edit.html";
    }

    @GetMapping("/post/write")
    public String moveToPostWritePage() {
        return "post-write.html";
    }
}
