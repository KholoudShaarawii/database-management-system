package com.db.dbms.application;

import com.db.dbms.server.ServerSide;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException {


        ServerSide handler = new ServerSide();
        handler. run();

    }
}
