package org.example.matchinggameclient.controller;

import org.example.matchinggameclient.model.User;

public class Client {
    public static User user;
    public static SocketHandle socketHandle;

    public Client() {
    }
    public static void main(String[] args) {
        new Client().initView();
    }

    public void initView() {
        socketHandle = new SocketHandle();
        socketHandle.run();
    }
}
