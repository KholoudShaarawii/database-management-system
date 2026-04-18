import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BufferPoolManager {

    //Load whole database into memory, modify in memory, write whole database back to disk

    DiskManager diskManager = new DiskManager();
    Map<String, Map<String, String>> tables = new HashMap<>();
    Map<String, List<Map<String, Object>>> rows = new HashMap<>();
    Map<String, Object> page = new HashMap<>();
    public boolean isDirty = false;


    public BufferPoolManager() throws IOException, ClassNotFoundException {

        loadOrInitializePage();
    }

    public void loadOrInitializePage() throws IOException, ClassNotFoundException {
        Path DiskFile = Path.of("E:\\DataBaseManagementSystem\\DataBase\\DiskFile");
        //loaded in RAM
        // case1
        if (Files.exists(DiskFile) && Files.size(DiskFile) > 0) { //EOFEXCEPTION
            System.out.println("found and loaded into RAM ");
            this.page = (Map<String, Object>) diskManager.read();

            //this.tables.containsKey("users")-> false???
            //Ensure that the fields I work with in RAM are exactly the same as the ones stored on disk.
            // this.tables must use the tables object stored inside page
            this.tables = (Map<String, Map<String, String>>) this.page.get("tables");
            this.rows = (Map<String, List<Map<String, Object>>>) this.page.get("rows");

        } else {
            //case2
            diskManager.createFile();
            page.put("tables", tables);
            page.put("rows", rows);
            diskManager.write(page);

            System.out.println("File was created and the page object was loaded into RAM");

        }

    }

    public Object read_or_write_on_disk() throws IOException {
        if (isDirty) {
            diskManager.write(page);
            isDirty = false;
            System.out.println("Page marked dirty and written to disk ");
        }
        return page;

    }


    public String create_table(SQLParser sqlParser) throws IOException, ClassNotFoundException {
        //CREATE TABLE users (username VARCHAR,age INT,salary FLOAT);

        //  if (sqlParser.operationType.equals("create")) { -> NPE
        if ("create".equals(sqlParser.operationType)) {

            if (this.tables.containsKey(sqlParser.tableName)) {
               /* System.out.println("table already exists");
                return; //؟؟؟؟؟*/
                return "Table already exists";

            } else {
                Map<String, String> schema = new HashMap<>();

                // System.out.println(tables); -> {users={[username, age, salary]=[varchar, int, float]}}

                for (int i = 0; i < sqlParser.columnsNames.size(); i++) {
                    String columnName = sqlParser.columnsNames.get(i);
                    String columnType = sqlParser.columnsTypes.get(i);
                    schema.put(columnName, columnType);
                }
                this.tables.put(sqlParser.tableName, schema);
                this.rows.put(sqlParser.tableName, new ArrayList<>());

            }
            //  System.out.println(tables); //{users={salary=float, age=int, username=varchar}}

            //  System.out.println(page);// ->{tables={}, rows={}} , before assignment into first method

            this.page.put("tables", this.tables);
            this.page.put("rows", this.rows);
            //   System.out.println(page); //{tables={users={salary=float, age=int, username=varchar}}, rows={users=[]}}

        }

        isDirty = true;
        this.read_or_write_on_disk();
        return "Create done";
    }

    public List<Map<String, Object>> select_rows(SQLParser sqlParser) {
// Loop through all original rows, build one filtered row at a time, then add it to the result list.
// One row = Map, many rows = List of Maps

/*
originalRow = {username='user1', age=20, salary=100.0}
user input = [username, age]

first loop: go through all original rows
second loop: go through the required columns
filteredRow = {username='user1', age=20}
put into the map result
*/
        List<Map<String, Object>> AllRows = new ArrayList<>(); // all rows read from the selected table, *
        List<Map<String, Object>> resultRows = new ArrayList<>(); // final SELECT result after filtering columns


        if (sqlParser.operationType.equals("select")) {
            if (this.tables.containsKey(sqlParser.tableName)) {
                System.out.println("table exists,you can select rows");
                //AllColumns.add(this.rows);
                // System.out.println(AllColumns); //[{users=[{salary=100.0, age=20, username='user1'}, {salary=240.432, age=30, username='user2'}, {salary=240.432, age=30, username='user3'}]}]
                AllRows = this.rows.get(sqlParser.tableName); //this.rows.get("users")

                if (sqlParser.columnsNames.get(0).equals("*")) { //.get&equals? columnNames =List not a Str
                    //   System.out.println(AllRows);
                    return AllRows;
                } else {
                    for (int i = 0; i < AllRows.size(); i++) {
                        Map<String, Object> row = AllRows.get(i); // get the current row
                        Map<String, Object> filteredRow = new HashMap<>();// one filtered row before adding it to resultRows

                        for (int j = 0; j < sqlParser.columnsNames.size(); j++) {
                            String columnName = sqlParser.columnsNames.get(j);
                            filteredRow.put(columnName, row.get(columnName));
                        }
                        resultRows.add(filteredRow);
                        //      System.out.println(resultRows);
                    }
                    return resultRows;

                }

            }

        }


        return AllRows;
    }

    public void insert_row(SQLParser sqlParser) throws IOException, ClassNotFoundException {
        // INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
        // {username=user1, age=20, salary=100.0}
        // tableName -> list of rows


        // Object reading= diskManager.read();
        Map<String, Object> row = new HashMap<>();
        // if (sqlParser.operationType.equals("insert")) { ->NPE
        if ("insert".equals(sqlParser.operationType)) {
            if (this.tables.containsKey(sqlParser.tableName)) {
                System.out.println(" table exists , you can insert rows ");

                for (int i = 0; i < sqlParser.columnsNames.size(); i++) {

                    String columnsName = sqlParser.columnsNames.get(i);
                    String value = sqlParser.values.get(i);
                    row.put(columnsName, value);
                }

                //    System.out.println(row);// {salary=100.0, age=20, username='user1'}
//rows.put(sqlParser.tableName, (List<Map<String, Object>>) row);   xxxxx
                rows.get(sqlParser.tableName).add(row);
                //    System.out.println(rows);

                isDirty = true;
                diskManager.write(page);

            }
        }

    }
}