package com.spring.blog.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/page")
@Controller
public class PageController {

    @GetMapping("/auth")
    public String moveToAuthPage() {
        return "auth.html";
    }

    @GetMapping("/post")
    public String moveToPostPage() {
        return "post.html";
    }

    @GetMapping("/post/write")
    public String moveToPostEditPage() {
        return "post-write.html";
    }
}
