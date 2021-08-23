package com.spring.blog.page;

import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public DataLoader(UserRepository userRepository,
        PostRepository postRepository,
        CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User("Kevin", "image");
        userRepository.save(user);

        for (int i = 0; i < 231; i++) {
            Post post = new Post(i + 1 + "번 글", "안녕하세요, 오늘은 날씨가 참 좋네요. 오늘 하루 어떠세요?", user);
            postRepository.save(post);
        }
    }
}

