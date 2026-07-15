package com.db.dbms.execution;

import com.db.dbms.parser.SQLParser;
import com.db.dbms.storage.BufferPoolManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutor {

    private final BufferPoolManager bufferPoolManager;

    public QueryExecutor(BufferPoolManager bufferPoolManager) {
        this.bufferPoolManager = bufferPoolManager;
    }


    public String createTable(SQLParser sqlParser) throws IOException, ClassNotFoundException {
        //CREATE TABLE users (username VARCHAR,age INT,salary FLOAT);

        //  if (sqlParser.operationType.equals("create")) { -> NPE
        if ("create".equals(sqlParser.getOperationType())) {

            if (bufferPoolManager.tables.containsKey(sqlParser.getTableName())) {
                return "Table already exists";
            } else {
                Map<String, String> schema = new HashMap<>();
                // System.out.println(tables); -> {users={[username, age, salary]=[varchar, int, float]}}

                for (int i = 0; i < sqlParser.getColumnsNames().size(); i++) {
                    String columnName = sqlParser.getColumnsNames().get(i);
                    String columnType = sqlParser.getColumnsTypes().get(i);
                    schema.put(columnName, columnType);
                }
                bufferPoolManager.tables.put(sqlParser.getTableName(), schema);
                bufferPoolManager.rows.put(sqlParser.getTableName(), new ArrayList<>());
            }
            //  System.out.println(tables); //{users={salary=float, age=int, username=varchar}}
            //  System.out.println(page);// ->{tables={}, rows={}} , before assignment into first method

            bufferPoolManager.page.put("tables", bufferPoolManager.tables);
            bufferPoolManager.page.put("rows", bufferPoolManager.rows);
            //   System.out.println(page); //{tables={users={salary=float, age=int, username=varchar}}, rows={users=[]}}
        }

        bufferPoolManager.isDirty = true;
        bufferPoolManager.flushToDisk();
        return "Create done";
    }


    public List<Map<String, Object>> selectRows(SQLParser sqlParser) {
    /*
    originalRow = {username='user1', age=20, salary=100.0}
    user input = [username, age]

    first loop: go through all original rows
    second loop: go through the required columns
    filteredRow = {username='user1', age=20}
    put into the result list
    */
        List<Map<String, Object>> allRows = new ArrayList<>(); // all rows read from the selected table, *
        List<Map<String, Object>> resultRows = new ArrayList<>(); // final SELECT result after filtering columns


        if ("select".equals(sqlParser.getOperationType())) {
            if (bufferPoolManager.tables.containsKey(sqlParser.getTableName())) {

                System.out.println("table exists,you can select rows");
                // AllColumns.add(this.rows);
                // System.out.println(AllColumns); [{users=[{salary=100.0, age=20, username='user1'}, {salary=240.432, age=30, username='user2'}, {salary=240.432, age=30, username='user3'}]}]

                allRows = bufferPoolManager.rows.get(sqlParser.getTableName());

                if (sqlParser.getColumnsNames().get(0).equals("*")) {
                    //   System.out.println(allRows);
                    return allRows;
                } else {
                    for (int i = 0; i < allRows.size(); i++) {
                        Map<String, Object> row = allRows.get(i); // get the current row
                        Map<String, Object> filteredRow = new HashMap<>();// one filtered row before adding it to resultRows

                        for (int j = 0; j < sqlParser.getColumnsNames().size(); j++) {
                            String columnName = sqlParser.getColumnsNames().get(j);
                            filteredRow.put(columnName, row.get(columnName));
                        }
                        resultRows.add(filteredRow);
                        // System.out.println(resultRows);
                    }
                    return resultRows;
                }
            }
        }

        return allRows;
    }


    public void insertRow(SQLParser sqlParser) throws IOException, ClassNotFoundException {
        // INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
        // {username=user1, age=20, salary=100.0}
        // tableName -> list of rows
        // Object reading= diskManager.read();
        Map<String, Object> row = new HashMap<>();

        if ("insert".equals(sqlParser.getOperationType())) {
            if (bufferPoolManager.tables.containsKey(sqlParser.getTableName())) {
                System.out.println(" table exists , you can insert rows ");

                for (int i = 0; i < sqlParser.getColumnsNames().size(); i++) {

                    String columnsName = sqlParser.getColumnsNames().get(i);
                    String value = sqlParser.getValues().get(i);
                    row.put(columnsName, value);
                }

                // System.out.println(row);// {salary=100.0, age=20, username='user1'}
                bufferPoolManager.rows
                        .get(sqlParser.getTableName())
                        .add(row);                // System.out.println(rows);
                bufferPoolManager.isDirty = true;
                bufferPoolManager.diskManager.write(bufferPoolManager.page);
            }
        }
    }
}
