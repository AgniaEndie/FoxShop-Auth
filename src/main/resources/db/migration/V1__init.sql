create table auth_model (
    uuid varchar(256) primary key,
    username varchar(256),
    password varchar(256),
    role integer,
    email varchar(256) unique
);

create table refresh (
    uuid varchar(256) primary key,
    token varchar(256),
    person varchar(256)
)