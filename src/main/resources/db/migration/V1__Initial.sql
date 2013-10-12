create table File(
	Id int identity primary key,
	Name varchar(256) not null,
	Data blob not null
);
