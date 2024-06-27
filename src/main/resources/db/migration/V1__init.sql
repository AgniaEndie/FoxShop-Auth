CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table auth_model (
    uuid varchar(256) primary key default gen_random_uuid(),
    username varchar(256),
    password varchar(256),
    role varchar(25),
    email varchar(256) unique
);

create table refresh (
    uuid varchar(256) primary key,
    token varchar(256),
    person varchar(256)
)