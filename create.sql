create database if not exists grabber;

create table if not exists post(
    id serial primary key,
    name unique text,
    text text,
    link text,
    created timestamp
);
