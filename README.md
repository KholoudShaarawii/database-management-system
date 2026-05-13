# Java DBMS Engine

A lightweight educational Database Management System built in Java from scratch using a client-server architecture.

This project demonstrates core DBMS concepts such as SQL parsing, in-memory buffering, disk persistence, socket communication, and basic query execution.

---

## Overview

This project simulates how a simple database engine works internally.

It was built to understand how real database systems manage memory, storage, and query execution.

---

## Features

- Create tables
- Insert rows
- Select data
- Load database data into memory
- Persist database data to disk
- Client-server communication using sockets
- Basic SQL query parsing

---

## Supported SQL Operations

### Create Table

```sql
CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
```

### Insert Rows

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

---

## System Architecture

```text
ClientSide
    ↓
ServerSide
    ↓
SQLParser
    ↓
BufferPoolManager
    ↓
DiskManager
```

---

## Core Components

### ClientSide

Responsible for:

- Connecting to the server
- Sending database requests
- Reading responses from the server
- Handling user input

Communication happens using Java sockets.

---

### ServerSide

Responsible for:

- Accepting client connections
- Performing handshake validation
- Receiving SQL queries
- Routing operations to the correct database components

The server listens for incoming client requests and processes SQL commands.

---

### SQLParser

Responsible for converting raw SQL text into structured Java objects.

The parser currently supports:

- `CREATE TABLE`
- `INSERT INTO`
- `SELECT`

It extracts:

- Operation type
- Table name
- Column names
- Column types
- Values

---

### BufferPoolManager

Acts as the in-memory database layer.

Responsible for:

- Loading database data into RAM
- Managing tables and rows
- Executing create, insert, and select operations
- Tracking dirty pages
- Writing modified data back to disk

The project follows a simplified buffering strategy:

```text
Load entire database into memory
        ↓
Modify data in RAM
        ↓
Write updated data back to disk
```

---

### DiskManager

Handles persistent storage.

Responsible for:

- Creating database files
- Writing serialized objects to disk
- Reading serialized objects from disk

The project uses Java Object Serialization for persistence.

---

## Technologies Used

- Java
- Java Sockets
- Object Serialization
- Java Collections Framework
- File I/O
- Client-Server Architecture

---

## Project Structure

```text
├── ClientMain.java
├── ClientSide.java
├── ServerMain.java
├── ServerSide.java
├── SQLParser.java
├── BufferPoolManager.java
├── DiskManager.java
```

---

## How It Works

### Step 1: Start the Server

Run:

```text
ServerMain.java
```

The server starts listening on:

```text
localhost:9090
```

---

### Step 2: Start the Client

Run:

```text
ClientMain.java
```

The client connects to the server and asks for a database name.

Example:

```text
Enter database name: testDB
```

---

### Step 3: Send SQL Queries

Example:

```sql
CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
```

```sql
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
```

```sql
SELECT * FROM users;
```

---

## Example Workflow

### Create Table

```sql
CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
```

### Insert Data

```sql
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
```

### Select Data

```sql
SELECT * FROM users;
```

### Output

```text
[{salary=100.0, age=20, username='user1'}]
```

---

## Database Persistence

The database state is stored on disk using serialized Java objects.

When the application starts:

- Existing database data is loaded into RAM
- If no database file exists, a new disk file is created
- Modified data is written back to disk after changes

---

## Learning Objectives

This project was built to deeply understand core DBMS concepts, including:

- DBMS internal architecture
- Buffer pool management
- Disk persistence
- SQL parsing
- Query execution flow
- Client-server networking
- Serialization and storage management

---

## Future Improvements

- WHERE clause support
- UPDATE and DELETE operations
- Query planner
- Indexing
- Page-based storage
- Transaction management
- Concurrency control
- Better SQL grammar support
- Type validation
- Multi-client handling using threads

---
