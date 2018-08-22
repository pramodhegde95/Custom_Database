Pramod Hegde
DATABASE: DAVISBASE
1) Run DavisBase.java file
(I have used eclipse: To run on eclipse load the database folder onto eclipse and run the DavisBase.java file)
(On command prompt: 
a. javac DavisBase.java
b. java DavisBase)

2) Use the following command:

a) show tables; To get all the tables in the database
b) help; To get all the commands supported by database
c) create table utdstudent (rowid INT, name TEXT, marks INT); To create a table in the database
d) select * from utdstudent; To get the data from table utdstudent
e) insert into utdstudent (rowid,name,marks) values (1,pramod,89); To insert data into the table
f) select * from utdstudent; To get the data from table utdstudent
g) update utdstudent set marks = 100 where rowed =1; To update the record
h) insert into utdstudent (rowid,name,marks) values (2,hegde,45); To insert data into the table
i) select * from utdstudent where rowid=2; To get the row with rowid=2
j) delete from table utdstudent where rowid=2; To delete a data from the table (Bonus)
k) drop table utdstudent; To drop the table from the database
l) select * from utdstudent;
