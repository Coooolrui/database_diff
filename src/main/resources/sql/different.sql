-- auto-generated definition
create table course
(
    id int auto_increment
        primary key
)
    comment 'course';

-- auto-generated definition
create table student
(
    id     int auto_increment
        primary key,
    name   varchar(63) default '' null,
    age    int         default 0  not null,
    mobile varchar(12)            null
);

