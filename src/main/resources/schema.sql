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
