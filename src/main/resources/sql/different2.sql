-- auto-generated definition
create table student
(
    id     int auto_increment
        primary key,
    name   varchar(63) default '' null,
    age    int         default 0  not null,
    mobile varchar(11) default '' null
);

