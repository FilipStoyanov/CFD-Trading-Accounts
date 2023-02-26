create database if not exists cfd;
use cfd;


create table if not exists `users`(
 id int primary key not null auto_increment,
 username varchar(255) unique not null,
 password_hash varchar(60),
 email varchar(255) unique not null,
 national_id varchar(30) unique not null,
 created_at datetime default current_timestamp,
 updated_at datetime default current_timestamp,
 constraint USERNAME_LENGTH check (length(username) >= 3),
 constraint EMAIL_LENGTH  check (email like "%@%")
);

create table if not exists `account_cash` (
 id int not null auto_increment,
 user_id int unique not null,
 balance decimal(19,2) default 0,
 created_at datetime default current_timestamp,
 updated_at datetime default current_timestamp,
 constraint PK_ACCOUNT_CASH primary key(id),
 constraint FK_CASH_TO_USER foreign key(user_id) references users(id) on delete cascade,
 index(user_id)
);

create table if not exists `instruments` (
	id int not null auto_increment,
    name varchar(255) unique,
    fullname varchar(255) unique,
    ticker varchar(255) unique,
    min_quantity decimal(19,2) not null,
    leverage decimal(19,10),
	market_name varchar(255),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
	isDeleted boolean default false,
    constraint PK_INSTRUMENT primary key(id)
);

create table if not exists `account_positions` (
    id int not null auto_increment,
    user_id int not null,
    instrument_id int not null,
    quantity decimal(10,2) not null,
    type enum ("LONG", "SHORT"),
    buy_price decimal(19,2),
    sell_price decimal(19,2),
    is_closed boolean not null default false,
	created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    deleted_at datetime default current_timestamp,
    constraint PK_ACCOUNT_POSITIONS primary key(id),
    constraint FK_POSITION_TO_USER foreign key(user_id) references users(id) on delete cascade,
    constraint FK_POSITION_TO_INSTRUMENT foreign key(instrument_id) references instruments(id) on delete cascade
);

create table if not exists `payments_webhook` (
    webhook_id int not null unique,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    constraint PK_PAYMENTS_WEBHOOK primary key(webhook_id)
);

create table if not exists `instrument_prices` (
    instrument_id int not null,
    buy decimal(19,2),
    sell decimal(19,2),
    constraint PK_INSTRUMENT_PRICES primary key(instrument_id),
    constraint FK_INSTRUMENT_TO_PRICES foreign key(instrument_id) references instruments(id) on delete cascade
);