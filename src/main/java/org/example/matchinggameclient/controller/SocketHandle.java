package org.example.matchinggameclient.controller;

import org.example.matchinggameclient.model.User;

import java.io.*;
import java.net.Socket;

public class SocketHandle implements Runnable {
    private BufferedWriter outputWriter;
    private Socket socketOfClient;
    private MessageHandler messageHandler; // Reference to the message handler

    public SocketHandle(MessageHandler messageHandler) {
        this.messageHandler = messageHandler; // Initialize the message handler
    }

    @Override
    public void run() {
        try {
            socketOfClient = new Socket("127.0.0.1", 7777);
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while ((message = inputReader.readLine()) != null) {
                if (messageHandler != null) {
                    messageHandler.onMessageReceived(message); // Notify the handler
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }
}
