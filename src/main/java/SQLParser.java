import java.lang.invoke.StringConcatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SQLParser {
    // Parse the SQL text into a structured object,So the program can understand and process it

    String query;
    String operationType;//select , insert , create
    List<String> columns = new ArrayList<>();
    String tableName;
    List<String> values = new ArrayList<>();
    List<String> columnsNames = new ArrayList<>();
    List<String> columnsTypes = new ArrayList<>();


    public SQLParser(String query) {
        this.query = query.toLowerCase().trim();

        if (this.query.startsWith("select")) {
            selectOperation(this.query);
        } else if (this.query.startsWith("insert")) {
            insertOpertion(this.query);
        } else if (this.query.startsWith("create")) {
            createOperation(this.query);
        } else {
            this.operationType = "unknown";
        }
    }

    public void selectOperation(String query) {
        //SELECT username,age FROM users;
        //SELECT * FROM users WHERE age = 30;
        this.query = query.toLowerCase();
        String[] tokens = this.query.split(" ");
        // System.out.println(Arrays.toString(tokens)); //[select, *, from, users;] , [select, username,age, from, users;]

        if (tokens[0].equals("select")) {
            this.operationType = tokens[0];
            //  System.out.println(tokens[0]); //select
        }
        if (tokens[1].equals("*")) {
            this.columnsNames = Collections.singletonList(tokens[1]);
            //  System.out.println("all columns" + " " + tokens[1]);

        } else {
            this.columnsNames = List.of(tokens[1].split(","));

            //    System.out.println("specific columns" + " " + Arrays.toString(tokens[1].split(",")));
        }//username,age,salary

        this.tableName = this.query.substring(this.query.lastIndexOf(" ") + 1).replace(";", ""); //the last word in a query
//System.out.println(tableName);

    }


    public void insertOpertion(String query) {
        //INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
        this.query = query.toLowerCase();
        String[] tokens = this.query.split(" ");
//System.out.println(Arrays.toString(tokens));->[insert, into, users, (username,age,salary), values, ('user1',20,100.0);]

        if (tokens[0].equals("insert")) {
            this.operationType = tokens[0];
            //    System.out.println(tokens[0]); insert

        }

        this.tableName = tokens[2];
        //  System.out.println(tableName); users
        this.columnsNames = List.of(tokens[3]
                .replace("(", "")
                .replace(")", "")
                .split(","));
//System.out.println(columnsNames);-> [(username,age,salary)] before replacement

//System.out.println(columnsNames); [username, age, salary] after replacement

        this.values = List.of(tokens[5]
                .replace("(", "")
                .replace(")", "")
                .replace(";", "")
                .split(","));
        // System.out.println(values); [('user1', 20, 100.0);] before replacement

        //  System.out.println(values);  ['user1', 20, 100.0] after replacement


    }

    public void createOperation(String query) {
        //CREATE TABLE users (username VARCHAR,age INT,salary FLOAT);
        this.query = query.toLowerCase();
        String[] tokens = this.query.split(" ");
        // System.out.println(Arrays.toString(tokens)); //[create, table, users, (username, varchar,age, int,salary, float);]
        //[token0, token1 ,token2 , ....]

        this.operationType = tokens[0];
        //  this.tableName = tokens[2];
        this.tableName = tokens[2].replace("(", "").replace(")", "").replace(";", "").trim(); //؟؟؟؟
//query is dynamic, so get '(' and ')' positions first, then extract text between them
        this.columns = List.of((this.query.substring
                        (this.query.indexOf("(") + 1,
                                this.query.lastIndexOf(")"))
                .split(",")));
        //    System.out.println(columns); //[username varchar, age int, salary float]


        for (String parts : columns) {
            String[] partstokens = parts.split(" ");
//System.out.println(Arrays.toString(partstokens)); /*[username, varchar]  [age, int]  [salary, float] = 3 Arrays */
            columnsNames.add(partstokens[0]);
            //        System.out.println("columnsNames" + columnsNames); ->[username, age, salary]

            columnsTypes.add(partstokens[1]);
            //            System.out.println("columnsTypes" + columnsTypes); ->[varchar, int, float]


        }

    }
}







