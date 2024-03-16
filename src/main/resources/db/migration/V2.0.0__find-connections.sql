create sequence Connection_SEQ start with 1 increment by 50;

create table Connection (
    id bigint not null,
    destination varchar(255),
    origin varchar(255),
    createdAt timestamp default current_timestamp,
    primary key (id)
);