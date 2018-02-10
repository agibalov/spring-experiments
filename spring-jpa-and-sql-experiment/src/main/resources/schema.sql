drop table if exists Events;
drop table if exists Comments;
drop table if exists Posts;
drop table if exists Users;

create table Users(
    id serial primary key,
    name varchar(256) not null
);

create unique index UserNameIndex on Users(name);

create table Posts(
    id serial primary key,
    content varchar(256) not null,
    userId integer not null constraint PostUserRef references Users(id)
);

create index PostUserIndex on Posts(userId);

create table Comments(
    id serial primary key,
    content varchar(256) not null,
    postId integer not null constraint CommentPostRef references Posts(id),
    userId integer not null constraint CommentUserRef references Users(id)
);

create index CommentUserIndex on Comments(userId);
create index CommentPostIndex on Comments(postId);

create table Events(
    id serial primary key,
    userId integer not null constraint EventUserRef references Users(id),
    type varchar(256) not null,
    postId integer constraint EventPostRef references Posts(id),
    commentId integer constraint EventCommentRef references Comments(id)
);

create index EventUserIndex on Events(userId);
