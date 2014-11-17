create table Users(
    id int identity primary key,
    name varchar(256) not null
);

create unique index UserNameIndex on Users(name);

create table Posts(
    id int identity primary key,
    content varchar(256) not null,
    userId int not null constraint PostUserRef references Users(id)
);

create table Comments(
    id int identity primary key,
    content varchar(256) not null,
    postId int not null constraint CommentPostRef references Posts(id),
    userId int not null constraint CommentUserRef references Users(id)
);

create table Events(
    id int identity primary key,
    userId int not null constraint EventUserRef references Users(id),
    type varchar(256) not null,
    postId int constraint EventPostRef references Posts(id),
    commentId int constraint EventCommentRef references Comments(id)
);