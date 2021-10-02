package com.spring.blog.user.domain.repoistory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.user.domain.QUser;
import com.spring.blog.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findActiveUserById(Long id) {
        QUser user = QUser.user;
        User findUser = jpaQueryFactory.selectFrom(user)
            .where(user.id.eq(id)
                .and(user.isDeleted.eq(false)))
            .fetchFirst();
        return Optional.ofNullable(findUser);
    }

    @Override
    public Optional<User> findActiveUserByName(String name) {
        QUser user = QUser.user;
        User findUser = jpaQueryFactory.selectFrom(user)
            .where(user.name.eq(name)
                .and(user.isDeleted.eq(false)))
            .fetchFirst();
        return Optional.ofNullable(findUser);
    }

    @Override
    public Optional<User> findActiveUserByEmail(String email) {
        QUser user = QUser.user;
        User findUser = jpaQueryFactory.selectFrom(user)
            .where(user.email.eq(email)
                .and(user.isDeleted.eq(false)))
            .fetchFirst();
        return Optional.ofNullable(findUser);
    }
}
