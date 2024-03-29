
    create sequence Flight_SEQ start with 1 increment by 50;

    create table Flight (
        price float4,
        departureDate timestamp(6),
        id bigint not null,
        landingDate timestamp(6),
        destination varchar(255),
        duration bigint,
        origin varchar(255),
        createdAt timestamp default current_timestamp,
        primary key (id)
    );
