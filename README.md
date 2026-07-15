# Java DBMS Engine

A Java-based Database Management System designed to demonstrate the fundamental workflow of a database engine through client-server communication.

The project demonstrates how basic SQL parsing, query execution, in-memory data management, disk persistence, and TCP socket communication work together inside a simple database engine.

---

## Overview

The application runs as two separate programs:

- A server that receives and processes SQL requests.
- A client that reads commands from the terminal and sends them to the server.

The server parses each SQL command, routes it to the query execution layer, reads or modifies the database state in memory, and persists data-changing operations to a serialized disk file.

---

## Features

- Create table schemas
- Insert rows into existing tables
- Select all columns from a table
- Select specific columns from a table
- Load stored tables and rows into memory when the server starts
- Persist database changes using Java Object Serialization
- Communicate between the client and server using TCP sockets
- Exchange requests and responses through a simple `key:>value` message format
- Keep example SQL commands in `src/main/resources/examples.sql`

---

## Supported SQL Operations

The current parser expects commands to follow the formats shown below.

### Create Table

```sql
CREATE TABLE users (username VARCHAR,age INT,salary FLOAT);
```

### Insert Row

```sql
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
```

### Select All Columns

```sql
SELECT * FROM users;
```

### Select Specific Columns

```sql
SELECT username,age FROM users;
```

> The parser currently depends on fixed spacing and token positions. Use the formats in `examples.sql` when testing the application.

---

## System Architecture

```text
ClientMain
    ↓
ClientSide
    ↓ TCP Socket
ServerSide
    ↓
SQLParser
    ↓
QueryExecutor
    ↓
BufferPoolManager
    ↓
DiskManager
    ↓
Serialized Disk File
```

### Query Flow

```text
The user enters a SQL command
        ↓
ClientSide sends the command to ServerSide through a TCP socket
        ↓
ServerSide creates an SQLParser for the received command
        ↓
SQLParser extracts the operation, table name, columns, types, and values
        ↓
ServerSide routes the parsed command to QueryExecutor
        ↓
QueryExecutor executes CREATE, INSERT, or SELECT
        ↓
CREATE and INSERT modify the in-memory state and persist changes to disk
SELECT reads the requested rows from memory
        ↓
ServerSide returns the operation result to ClientSide
        ↓
ClientSide displays the response
```

---

## Core Components

### `ClientMain`

The entry point for the client application.

It creates a `ClientSide` instance that connects to:

```text
localhost:9090
```

---

### `ClientSide`

Responsible for:

- Establishing a socket connection with the server
- Reading the database name and SQL commands from the terminal
- Sending the database name to the server for initial validation
- Sending SQL commands using the internal `query:>` message format
- Reading and displaying server responses
- Closing the streams and socket when the user enters `Exit` or the input ends

The user enters normal SQL commands. The `db:>` and `query:>` prefixes are added internally by the client.

---

### `ServerMain`

The entry point for the server application.

It creates a `ServerSide` instance and starts the server loop.

---

### `ServerSide`

Responsible for:

- Starting a `ServerSocket` on `localhost:9090`
- Accepting incoming client connections
- Reading and validating a nonblank database name
- Receiving SQL requests
- Creating an `SQLParser` for each request
- Routing parsed operations to `QueryExecutor`
- Returning acknowledgement and operation-result messages to the client
- Closing the client socket when the request stream ends

The database name is currently used only for initial connection validation.

---

### `SQLParser`

Responsible for parsing the supported SQL commands.

It extracts:

- Operation type
- Table name
- Column names
- Column types
- Inserted values

The parser recognizes:

- `CREATE TABLE`
- `INSERT INTO`
- `SELECT`

Commands that do not begin with a supported operation are classified as `unknown`.

---

### `QueryExecutor`

Responsible for executing the operation represented by `SQLParser`.

Current methods:

```java
createTable()
insertRow()
selectRows()
```

It performs the following work:

- Creates a table schema and an empty row collection
- Inserts a row into an existing table
- Returns all rows for `SELECT *`
- Builds filtered rows for specific-column `SELECT` queries
- Triggers disk persistence after data-changing operations

---

### `BufferPoolManager`

Represents the in-memory database state.

Responsible for:

- Loading the stored page from disk when the server starts
- Initializing empty table and row maps when no stored data exists
- Keeping table schemas and rows in memory
- Keeping the serialized page object
- Tracking the dirty state
- Flushing modified data to disk

The current implementation uses a simplified strategy:

```text
Load the complete stored database state into memory
        ↓
Read or modify tables and rows in memory
        ↓
Write the complete updated state back to disk after changes
```

---

### `DiskManager`

Responsible for file-based persistence.

It:
- Creates the `DataBase` directory and `DiskFile` when they do not exist
- Serializes Java objects and writes them to the disk file
- Reads and deserializes Java objects from the disk file

The project uses `ObjectOutputStream` and `ObjectInputStream` for persistence.

---

## Internal Data Representation

Table schemas are stored as:
 
```java
Map<String, Map<String, String>>
```

Conceptually:

```text
table name
    └── column name → declared column type
```

Rows are stored as:

```java
Map<String, List<Map<String, Object>>>
```

Conceptually:

```text
table name
    └── list of rows
            └── column name → stored value
```

The serialized page contains the `tables` and `rows` maps.

---

## Technologies Used

- Java 11
- Maven
- Java Sockets
- Java Collections Framework
- Java Object Serialization
- Java File I/O and NIO

---

## Project Structure

```text
src/main/java/com/db/dbms
├── application
│   ├── ClientMain.java
│   └── ServerMain.java
│
├── client
│   └── ClientSide.java
│
├── server
│   └── ServerSide.java
│
├── parser
│   └── SQLParser.java
│
├── execution
│   └── QueryExecutor.java
│
└── storage
    ├── BufferPoolManager.java
    └── DiskManager.java

src/main/resources
└── examples.sql
```

---

## Requirements

- JDK 11 or later
- Maven
- A valid local storage path configured in `BufferPoolManager` and `DiskManager`

---
## Storage Configuration

The project can be cloned and opened from any location on the computer.

However, the database data file is currently stored using the following fixed Windows path:

```text
E:\DataBaseManagementSystem\DataBase\DiskFile
```

This path refers to the database storage file, not to the location of the project source code.

---
## How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/KholoudShaarawii/database-management-system.git
```

Open the cloned project in IntelliJ IDEA or another Java IDE.

### 2. Run the Server

Open:

```text
src/main/java/com/db/dbms/application/ServerMain.java
```

Run the `main()` method in `ServerMain`.

Keep the server running while using the client. The server starts listening on:

```text
localhost:9090
```

If the disk file does not exist, the server output includes:

```text
Disk file will be created
File was created and the page object was loaded into RAM
server is listening on :localhost,9090
```

If stored data already exists, it is loaded into memory before the server starts accepting client requests.

### 4. Run the Client

After starting the server, open:

```text
src/main/java/com/db/dbms/application/ClientMain.java
```

Run the `main()` method in `ClientMain` as a separate run process.

The client connects to the running server and displays:

```text
Connected
Enter database name:
```

Enter a nonblank database name, for example:

```text
testDB
```

A successful connection displays responses similar to:

```text
Server: con:>1
Server: message:>Connected to testDB Successfully !
```

### 5. Send SQL Commands

Enter SQL commands in the client console after:

```text
Enter request:
```

Examples:

```sql
CREATE TABLE users (username VARCHAR,age INT,salary FLOAT);
```

```sql
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
```

```sql
SELECT * FROM users;
```

To stop the client, enter:

```text
Exit
```

The client displays:

```text
Client stopped sending requests
```

### Running Order

```text
Run ServerMain
        ↓
Keep the server running
        ↓
Run ClientMain
        ↓
Enter a database name
        ↓
Enter SQL commands in the client console
```

### Unsupported Command

If the user enters an unsupported command:

```text
Enter request: exii
Server: Request received
Server: Unknown operation
```
---

## Database Persistence

When `BufferPoolManager` is created:

1. It checks whether the configured disk file exists and contains data.
2. If data exists, it deserializes the stored page into memory.
3. It restores the `tables` and `rows` maps from the page.
4. If no stored data exists, it creates the file and writes an initial empty page.

During query execution:

- `CREATE TABLE` updates the in-memory schema and persists the updated page.
- `INSERT INTO` adds the row in memory and writes the updated page to disk.
- `SELECT` reads rows from memory without writing to disk.

---

## Learning Objectives

This project demonstrates:

- Basic DBMS component separation
- SQL parsing
- Query execution
- In-memory table and row management
- Dirty-state tracking
- File persistence
- Object serialization and deserialization
- Client-server communication
- Socket-based request and response flow

---

## Future Improvements

- Add `WHERE` clause support
- Add `UPDATE` and `DELETE` operations
- Add stronger SQL grammar parsing
- Add data type validation and conversion
- Add table and column validation
- Replace the fixed disk path with configurable storage
- Support multiple independent databases
- Add fixed-size page management
- Add indexing
- Add a query planner
- Add transaction management

  ---
NOTE: Not all SQL commands coludn't run because it's programmed to run the insert, select and create (table) only , And if you want to test the queries you can find exists queries in queries.txt file.


- Add concurrency control
- Handle multiple clients using threads
- Add automated tests
