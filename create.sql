create database if not exists grabber;

create table if not exists post(
    id serial primary key,
    name text unique,
    text text,
    link text,
    created timestamp
);
