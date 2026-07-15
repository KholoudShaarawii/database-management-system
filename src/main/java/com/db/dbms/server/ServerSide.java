package com.db.dbms.server;

import com.db.dbms.storage.BufferPoolManager;
import com.db.dbms.parser.SQLParser;
import com.db.dbms.execution.QueryExecutor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerSide {

    private ServerSocket serverSocket;
    private String host;
    private int port;
    private BufferPoolManager bufferPoolManager = new BufferPoolManager();
    QueryExecutor queryExecutor = new QueryExecutor(bufferPoolManager);

    public ServerSide() throws IOException, ClassNotFoundException {
        setupServer();
    }

    public void setupServer() throws IOException { //server configuration
        this.host = "localhost";
        this.port = 9090;
        serverSocket = new ServerSocket(this.port); // create + bind + listening= connections
        serverSocket.setReuseAddress(true); /* It's normal to see a small delay after shutdown. The OS may keep the previous connection state for a short time. This does not mean the server forgot it was closed.*/
    }

    //format is -> key:>value
    //custom serialization
    public String serialization(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(entry.getKey());
            sb.append(":>");
            sb.append((entry.getValue()));
        }

        return sb.toString();
    }

    //server-> bytes -> Format -> data ->client
    public Map<String, String> deserialization(String message) {
        Map<String, String> map = Arrays.stream(message.split("\n"))
                .map(pair -> pair.split(":>", 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
        return map;
    }

    //run() is responsible for starting the server, accepting clients, and routing each valid connection to the proper handler.
    public void run() throws IOException, ClassNotFoundException {
        System.out.println("server is listening on :" + this.host + "," + this.port);
        while (true) { //accept client connection
            Socket socket = serverSocket.accept();// Accept an incoming client connection
            System.out.println("Client connected from: " + socket.getInetAddress());
            System.out.println("Client port: " + socket.getPort());

            String dbName = this.read_db(socket);

            if (dbName == null) {
                socket.close();
                System.out.println("Socket is closed!");
                continue;
            }
            handleConnection(socket, dbName);
        }
    }

    //HandShake
    public String read_db(Socket socket) throws IOException {
        //The client sent the message over the connection. The message is located within the socket stream. `read_db(socket)` opens the socket, reads the message, extracts the database name, and returns it.
        InputStream inputStream = socket.getInputStream();// incoming data
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // convert to text
        BufferedReader in_socket = new BufferedReader(inputStreamReader);// read text easily
        String message = in_socket.readLine(); // Read a message sent by the client

        System.out.println("Client says " + message);

        Map<String, String> body = this.deserialization(message);
        String dbName = body.get("db");

        if (dbName == null || dbName.isBlank()) {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            PrintWriter out_socket = new PrintWriter(outputStreamWriter, true);
            Map<String, Object> response = Map.of(
                    "message", "Invalid Connection",
                    "con", 0
            );

            String responseText = this.serialization(response);
            out_socket.println(responseText);
            return null;
        } else {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            PrintWriter out_socket = new PrintWriter(outputStreamWriter, true);

            Map<String, Object> response = Map.of(
                    "message", "Connected to " + dbName + " Successfully !",
                    "con", 1
            );

            String responseText = this.serialization(response);
            out_socket.println(responseText);

            System.out.println("valid connection");
            return dbName;
        }
    }

    //handle_connection() = request-response loop for one connected client
    public void handleConnection(Socket socket, String dbName) throws IOException, ClassNotFoundException {

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String clientRequest;
        while ((clientRequest = in.readLine()) != null) {
            Map<String, String> requestBody = this.deserialization(clientRequest);

            String query = requestBody.get("query"); //query:>select * from users

            System.out.println("Query from client: " + query);
            SQLParser parser = new SQLParser(query);

            out.println("Request received");
            if ("create".equals(parser.getOperationType())) {
                String result = queryExecutor.createTable(parser);
                out.println(result);
            } else if ("insert".equals(parser.getOperationType())) {
                if (bufferPoolManager.tables.containsKey(parser.getTableName())) {
                    queryExecutor.insertRow(parser);
                    out.println("Insert done");
                } else {
                    out.println("Table does not exist");
                }

            } else if ("select".equals(parser.getOperationType())) {
                List<Map<String, Object>> result = queryExecutor.selectRows(parser);
                out.println((result));
            } else {
                out.println("Unknown operation");
            }

            // Process the input and respond
        }
        socket.close();
    }

}