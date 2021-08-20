package com.spring.blog.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/auth")
    public String moveToAuthPage() {
        return "auth.html";
    }
}
