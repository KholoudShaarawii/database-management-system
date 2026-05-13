Java DBMS Engine

A lightweight educational Database Management System built in Java from scratch using a client-server architecture.
The project demonstrates core DBMS concepts including SQL parsing, in-memory buffering, disk persistence, socket communication, and basic query execution.

Overview

This project simulates how a simple database engine works internally.

The system supports:

Creating tables
Inserting rows
Selecting data
Loading data into memory
Persisting data to disk
Client-server communication using sockets
Basic SQL query parsing

The architecture was designed to help understand how real database systems manage memory, storage, and query execution.

Features
Supported SQL Operations
Create Table
CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
Insert Rows
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
Select All Columns
SELECT * FROM users;
Select Specific Columns
SELECT username,age FROM users;
System Architecture
ClientSide
    ↓
ServerSide
    ↓
SQLParser
    ↓
BufferPoolManager
    ↓
DiskManager
Core Components
ClientSide

Responsible for:

Connecting to the server
Sending database requests
Reading responses from the server
Handling user input

Communication happens using Java sockets.

ServerSide

Responsible for:

Accepting client connections
Performing handshake validation
Receiving SQL queries
Routing operations to the correct database components

The server continuously listens for incoming client requests.

SQLParser

Converts raw SQL text into structured Java objects.

The parser currently supports:

CREATE TABLE
INSERT INTO
SELECT

It extracts:

Operation type
Table name
Column names
Column types
Values
BufferPoolManager

Acts as the in-memory database layer.

Responsibilities include:

Loading database pages into RAM
Managing tables and rows
Executing create/insert/select operations
Tracking dirty pages
Writing modified pages back to disk

The project follows a simplified buffering strategy:

Load entire database into memory
↓
Modify data in RAM
↓
Write updated data back to disk
DiskManager

Handles persistent storage.

Responsibilities include:

Creating database files
Writing serialized objects to disk
Reading serialized objects from disk

The project uses Java object serialization for persistence.

Technologies Used
Java
Java Sockets
Object Serialization
Collections Framework
File I/O
Client-Server Architecture
Project Structure
├── ClientMain.java
├── ClientSide.java
├── ServerMain.java
├── ServerSide.java
├── SQLParser.java
├── BufferPoolManager.java
├── DiskManager.java
How It Works
Step 1 — Start the Server

Run:

ServerMain.java

The server starts listening on:

localhost:9090
Step 2 — Start the Client

Run:

ClientMain.java

The client connects to the server and asks for a database name.

Example:

Enter database name: testDB
Step 3 — Send SQL Queries

Example:

CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
SELECT * FROM users;
Example Workflow
Create Table
CREATE TABLE users (username VARCHAR, age INT, salary FLOAT);
Insert Data
INSERT INTO users (username,age,salary) VALUES ('user1',20,100.0);
Select Data
SELECT * FROM users;

Output:

[{salary=100.0, age=20, username='user1'}]
Database Persistence

The database state is stored on disk using serialized Java objects.

When the application starts:

Existing database pages are loaded into RAM
If no database exists, a new disk file is created

Dirty pages are automatically written back to disk after modifications.

Learning Objectives

This project was built to deeply understand:

DBMS internal architecture:

Buffer pool management
Disk persistence
SQL parsing
Query execution flow
Client-server networking
Serialization and storage management


Future Improvements:

WHERE clause support
UPDATE and DELETE operations
Query planner
Indexing
Page-based storage
Transaction management
Concurrency control
Better SQL grammar support
Type validation
Multi-client handling using threads
