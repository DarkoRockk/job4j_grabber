create database if not exists grabber;

create table if not exists post(
    id serial primary key,
    name text,
    text text,
    link text unique,
    created timestamp
);
