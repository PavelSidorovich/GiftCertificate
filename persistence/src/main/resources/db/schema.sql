create table gift_certificate
(
    id               bigserial
        constraint gift_certificate_pk
        primary key,
    name             varchar(128)  not null,
    description      varchar(256)  not null,
    price            numeric(8, 2) not null,
    duration         integer       not null,
    create_date      date          not null,
    last_update_date date          not null
);

create unique index gift_certificate_id_uindex
    on gift_certificate (id);

create table tag
(
    id   bigserial
        constraint tag_pk
        primary key,
    name varchar(128) not null
);

create unique index tag_id_uindex
    on tag (id);

create unique index tag_name_uindex
    on tag (name);

create table gift_certificates_by_tags
(
    id                  bigserial
        constraint gift_certificates_by_tags_pk
        primary key,
    gift_certificate_id bigint not null
        constraint gift_certificates_by_tags_gift_certificate_id_fk
        references gift_certificate
        on update cascade on delete cascade,
    tag_id              bigint not null
        constraint gift_certificates_by_tags_tag_id_fk
        references tag
        on update cascade on delete cascade
);

create unique index gift_certificates_by_tags_id_uindex
    on gift_certificates_by_tags (id);
