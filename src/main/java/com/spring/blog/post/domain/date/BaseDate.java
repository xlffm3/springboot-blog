package com.spring.blog.post.domain.date;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Embeddable
public class BaseDate {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    protected BaseDate() {
    }
}
