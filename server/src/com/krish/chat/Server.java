package com.krish.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class created by Krish
 */

public class Server extends Thread {

    private ArrayList<ServerWorker> workerList = new ArrayList<>();
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    List<ServerWorker> getWorkerList() {
        return workerList;
    }

    void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
