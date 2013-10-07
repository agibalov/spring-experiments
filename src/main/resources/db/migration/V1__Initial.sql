create table Users(
	Id int identity primary key,
	RowUuid char(36) not null,
	Name varchar(256) not null
);
