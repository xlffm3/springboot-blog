package com.spring.blog.comment.domain.date;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Embeddable
public class BaseDate {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    protected BaseDate() {
    }
}
