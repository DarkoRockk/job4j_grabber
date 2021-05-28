create database if not exists grabber;

create table if not exists post(
    id serial primary key,
    name unique varchar(255),
    text varchar(2500),
    link varchar(255),
    created date
);
