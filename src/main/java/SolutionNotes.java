public class SolutionNotes {
    //1.  exception -> E:\DataBaseManagementSystem\Disk\DiskFile ?// createFile throws an exception if the file already exists, so check with Files.exists(...) first

    //2. public  Object page; must be a global var --> what is the shadowing?

/*3. in insert into buffer pool
 Disk data and in-memory fields are not automatically linked.
 Reading from disk returns a new object, but it does not update this.page, this.tables, or this.rows by itself.

/*4. this.page, this.tables, and this.rows are only fields of the current object in RAM.
 If I want the current object to use the loaded data, I must assign the loaded object to these fields.
 Important:
 diskManager.read() loads data from the file,
 but the result stays only in the variable that receives it.
 It will not magically appear inside this.page or this.tables.
 Assignment is needed after read(),
 because read() returns data to a variable,
 while insert/select logic works on the object's fields in memory.*/


    //5. EOFException


//9. schreenshot

/*10.java.net.BindException: Address already in use: NET_Bind ->BindException happened because the program tried to open the same server port twice.
In this code, setupServer() creates a new ServerSocket on port 8080.
The constructor already calls setupServer() once.
Then Main calls setupServer() again on the same object.
So the first call opens and binds port 8080 successfully.
The second call tries to bind the same port again.
That is why Java throws
java.net.BindException: Address already in use*/


}