package com.spring.blog.user.domain.repoistory;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.blog.user.domain.QUser;
import com.spring.blog.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private static final QUser QUSER = QUser.user;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findActiveUserById(Long id) {
        User findUser = jpaQueryFactory.selectFrom(QUSER)
            .where(QUSER.id.eq(id)
                .and(isActiveUser()))
            .fetchOne();
        return Optional.ofNullable(findUser);
    }

    @Override
    public Optional<User> findActiveUserByName(String name) {
        User findUser = jpaQueryFactory.selectFrom(QUSER)
            .where(QUSER.name.eq(name)
                .and(isActiveUser()))
            .fetchOne();
        return Optional.ofNullable(findUser);
    }

    @Override
    public Optional<User> findActiveUserByEmail(String email) {
        User findUser = jpaQueryFactory.selectFrom(QUSER)
            .where(QUSER.email.eq(email)
                .and(isActiveUser()))
            .fetchOne();
        return Optional.ofNullable(findUser);
    }

    private BooleanExpression isActiveUser() {
        return QUSER.isDeleted.eq(false);
    }
}
