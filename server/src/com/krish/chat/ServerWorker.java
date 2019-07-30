package com.krish.chat;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Class created by Krish
 */

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private OutputStream outputStream;
    private String login = null;

    ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    private String getLogin() {
        return login;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s");
            if (tokens.length > 0) {
                String cmd = tokens[0];
                if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("logoff")) {
                    handleLogoff();
                    break;
                } else if (cmd.equalsIgnoreCase("login")) {
                    handleLogin(tokens);
                } else if (cmd.equalsIgnoreCase("msg")) {
                    handleMessage(tokens);
                } else {
                    String msg = "Unknown command: " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void handleLogin(String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("Guest") && password.equals("password")) || (login.equals("Krish") && password.equals("admin"))) {
                String response = "success\n";
                outputStream.write(response.getBytes());
                this.login = login;
                System.out.println("User " + login + " successfully logged in");

                List<ServerWorker> workerList = server.getWorkerList();

                // send current user all other online login
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!worker.getLogin().equals(login)) {
                            String msg = "Online " + worker.getLogin() + "\n";
                            send(msg);
                        }
                    }
                }
                // send other online users current user's status
                String onlineMsg = "Online " + login + "\n";
                for (ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!worker.getLogin().equals(login)) {
                            worker.send(onlineMsg);
                        }
                    }
                }
            } else {
                String msg = "failure\n";
                outputStream.write(msg.getBytes());
                System.err.println("User " + login + " could not successfully log in");
            }
        }
    }

    private void handleMessage(String[] tokens) throws IOException {
        String receiver = tokens[1];
        StringBuilder body = new StringBuilder();
        for (int i = 2; i < tokens.length; i++) {
            body.append(tokens[i]).append(" ");
        }

        String msgToSender = "you -> " + receiver + ": " + body + "\n";
        send(msgToSender);

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if (worker.getLogin().equalsIgnoreCase(receiver)) {
                String msgToReceiver = login + " -> you: " + body + "\n";
                worker.send(msgToReceiver);
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "Offline " + login + "\n";
        for (ServerWorker worker : workerList) {
            if (!worker.getLogin().equals(login)) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    private void send(String onlineMsg) throws IOException {
        if (login != null) {
            outputStream.write(onlineMsg.getBytes());
        }
    }

}
