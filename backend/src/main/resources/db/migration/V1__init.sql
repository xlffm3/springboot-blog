create table comment
(
    id                bigint       not null auto_increment,
    created_date      datetime(6),
    modified_date     datetime(6),
    content           varchar(140) not null,
    depth             bigint       not null,
    left_node         bigint       not null,
    right_node        bigint       not null,
    is_deleted        bit          not null,
    parent_comment_id bigint,
    root_comment_id   bigint,
    post_id           bigint       not null,
    user_id           bigint       not null,
    primary key (id)
);

create table image
(
    id      bigint       not null auto_increment,
    url     varchar(255) not null,
    post_id bigint       not null,
    primary key (id)
);


create table post
(
    id            bigint         not null auto_increment,
    created_date  datetime(6),
    modified_date datetime(6),
    is_deleted    bit            not null,
    content       varchar(10000) not null,
    title         varchar(100)   not null,
    view_counts   bigint         not null,
    user_id       bigint         not null,
    primary key (id)
);


create table user
(
    id            bigint       not null auto_increment,
    is_deleted    bit          not null,
    name          varchar(255) not null,
    profile_image varchar(255),
    primary key (id)
);

alter table user
    add constraint uk_user_name unique (name);

alter table comment
    add constraint fk_comment_to_parent
        foreign key (parent_comment_id) references comment (id);

alter table comment
    add constraint fk_comment_to_root
        foreign key (root_comment_id)
            references comment (id);

alter table comment
    add constraint fk_comment_to_post
        foreign key (post_id)
            references post (id);

alter table comment
    add constraint fk_comment_to_user
        foreign key (user_id)
            references user (id);

alter table image
    add constraint fk_image_to_post
        foreign key (post_id)
            references post (id);

alter table post
    add constraint fk_post_to_user
        foreign key (user_id)
            references user (id);
